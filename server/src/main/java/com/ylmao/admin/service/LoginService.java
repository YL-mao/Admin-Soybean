package com.ylmao.admin.service;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ylmao.admin.common.FilterCodes;
import com.ylmao.admin.common.FingerprintKeys;
import com.ylmao.admin.common.OnlineSessionKeys;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.dto.LoginDto;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.utils.ServletUtils;
import com.ylmao.admin.utils.UserAgentUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    private static final DateTimeFormatter LOGIN_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserService userService;
    private final PasswordService passwordService;
    private final LoginFailService loginFailService;
    private final LoginRateLimitService loginRateLimitService;
    private final CaptchaService captchaService;
    private final FilterService filterService;
    private final FingerprintService fingerprintService;
    private final UserMapper userMapper;

    public void login(LoginDto.LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String clientIp = ServletUtils.getIP(request);
        // IP 软拦可在验证码前（防刷接口）；账号软拦须在验证码后，避免未鉴权打满目标账号。
        loginRateLimitService.checkLoginIp(clientIp);

        // 验证码错误不计账号失败次数，也不计账号软拦。
        captchaService.validateAndConsume(loginRequest.captcha(), request, response);

        loginRateLimitService.checkLoginAccount(loginRequest.userAccount());

        // IP 白名单：跳过 IP 黑名单、IP 失败计数与自动拉黑；仍校验用户黑名单与账号规则。
        boolean ipWhitelisted = filterService.isIpWhitelisted(clientIp);

        if (!ipWhitelisted && filterService.isBlocked(FilterCodes.TYPE_IP, clientIp)) {
            logger.info("登录拦截：IP[{}]在黑名单中", clientIp);
            applyIpFailAndMaybeBan(clientIp, "IP已在黑名单仍尝试登录", true);
            throw new BusinessException(FilterCodes.BLOCK_MSG);
        }

        User dbUser = userService.getUserByAccount(loginRequest.userAccount());
        if (dbUser == null) {
            logger.info("对用户[{}]进行登录验证..验证未通过,不存在的账户", loginRequest.userAccount());
            applyIpFailAndMaybeBan(clientIp, "不存在的账户", !ipWhitelisted);
            throw new BusinessException("账号或者密码错误");
        }
        if (filterService.isBlocked(FilterCodes.TYPE_USER_ID, dbUser.getUserId())) {
            logger.info("登录拦截：用户[{}]在黑名单中", dbUser.getUserId());
            applyIpFailAndMaybeBan(clientIp, "用户已在黑名单", !ipWhitelisted);
            throw new BusinessException(FilterCodes.BLOCK_MSG);
        }
        if (dbUser.getIsLock() != null && dbUser.getIsLock() == 1) {
            logger.info("对用户[{}]进行登录验证..验证未通过,账号已锁定", loginRequest.userAccount());
            applyIpFailAndMaybeBan(clientIp, "账号已锁定仍尝试登录", !ipWhitelisted);
            throw new BusinessException("账号已锁定，请联系管理员");
        }
        if (dbUser.getIsEnabled() == null || dbUser.getIsEnabled() != 1) {
            logger.info("对用户[{}]进行登录验证..验证未通过,账号已停用", loginRequest.userAccount());
            applyIpFailAndMaybeBan(clientIp, "账号已停用仍尝试登录", !ipWhitelisted);
            throw new BusinessException("账号已停用");
        }
        if (!passwordService.matches(loginRequest.userPassword(), dbUser.getUserPassword())) {
            int accountFails = loginFailService.recordAccountFail(dbUser.getUserAccount());
            int accountLimit = loginFailService.accountFailLimit();
            int ipFails = 0;
            if (!ipWhitelisted) {
                ipFails = loginFailService.recordIpFail(clientIp);
            }
            logger.info("对用户[{}]进行登录验证..验证未通过,错误的账户或密码凭证,账号失败次数={}/{},IP[{}]失败次数={}",
                    loginRequest.userAccount(), accountFails, accountLimit, clientIp, ipFails);

            // acctFailLim=0 表示不锁号。
            boolean lockedNow = false;
            if (accountLimit > 0 && accountFails >= accountLimit) {
                lockUser(dbUser.getUserId());
                loginFailService.clearAccountFail(dbUser.getUserAccount());
                lockedNow = true;
            }
            boolean bannedNow = false;
            if (!ipWhitelisted && loginFailService.hitIpBanThreshold(ipFails)) {
                String scene = lockedNow
                        ? "账号密码错误满" + accountLimit + "次已锁号"
                        : "账号密码错误";
                filterService.banIpAuto(clientIp, ipFails, scene);
                bannedNow = true;
            }
            if (lockedNow) {
                throw new BusinessException("账号已锁定，请联系管理员");
            }
            if (bannedNow) {
                throw new BusinessException(FilterCodes.BLOCK_MSG);
            }
            throw new BusinessException("账号或者密码错误");
        }

        loginFailService.clearAccountFail(dbUser.getUserAccount());
        loginFailService.clearIpFail(clientIp);
        // rememberMe 仅前端记账号密码，与 token 存活无关；统一按全局 timeout / active-timeout。
        // 账密通过后：已有会话则先注销再建；login 带 admin 设备类型，配合 is-concurrent=false 只顶同类型。
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        StpUtil.login(dbUser.getUserId(), FingerprintKeys.Admin.DEVICE_TYPE);
        SaTokenUtil.setUser(dbUser);
        // Token-Session 写入在线列表所需元数据（与 OnlineSessionKeys 对齐）。
        String userAgent = request.getHeader("User-Agent");
        SaSession tokenSession = StpUtil.getTokenSession();
        tokenSession.set(OnlineSessionKeys.IP, clientIp);
        tokenSession.set(OnlineSessionKeys.LOGIN_TIME, LocalDateTime.now().format(LOGIN_TIME_FMT));
        tokenSession.set(OnlineSessionKeys.BROWSER, UserAgentUtils.parseBrowser(userAgent));
        tokenSession.set(OnlineSessionKeys.OS, UserAgentUtils.parseSystemOs(userAgent));
        // 管理端指纹：IP / UA / X-Device-Id 写入同一 Token-Session。
        fingerprintService.bindOnLogin(request);
        logger.info("用户[{}]登录成功", loginRequest.userAccount());
        try {
            int cleaned = filterService.cleanExpiredForLogin(clientIp, dbUser.getUserId());
            if (cleaned > 0) {
                logger.info("登录成功清理过期访问控制 ip={} userId={} deleted={}", clientIp, dbUser.getUserId(), cleaned);
            }
        } catch (Exception e) {
            logger.warn("登录成功清理过期访问控制失败 ip={} userId={}", clientIp, dbUser.getUserId(), e);
        }
    }

    /** countIp=false 时仅记录场景日志，不累计 IP（白名单）。 */
    private void applyIpFailAndMaybeBan(String clientIp, String failScene, boolean countIp) {
        if (!countIp) {
            return;
        }
        int ipFails = loginFailService.recordIpFail(clientIp);
        logger.info("登录 IP[{}]鉴权失败累计次数={}", clientIp, ipFails);
        if (loginFailService.hitIpBanThreshold(ipFails)) {
            filterService.banIpAuto(clientIp, ipFails, failScene);
        }
    }

    private void lockUser(String userId) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUserId, userId).set(User::getIsLock, 1);
        int rows = userMapper.update(null, wrapper);
        if (rows <= 0) {
            throw new BusinessException("账号锁定失败");
        }
    }
}
