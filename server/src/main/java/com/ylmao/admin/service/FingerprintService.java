package com.ylmao.admin.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.FingerprintKeys;
import com.ylmao.admin.common.SecurityConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 会话指纹比对实现。当前调用方仅管理端（{@link FingerprintKeys.Admin}）；
 * 由谁调用由拦截层决定，本类不做路径分流。
 * 比对项开关读 security.fpCheckIp / fpCheckUa / fpCheckDevice，缺省或未启用按 true。
 */
@Service
@RequiredArgsConstructor
public class FingerprintService {

    private final ConfigRuntimeService configRuntimeService;

    /** 登录成功后写入当前 token 的管理端指纹（deviceId 必填，来自 Header）。 */
    public void bindOnLogin(HttpServletRequest request) {
        String deviceId = requireDeviceId(request);
        String ip = ServletUtils.getIP(request);
        String ua = StrUtil.nullToDefault(request.getHeader("User-Agent"), "");
        SaSession tokenSession = StpUtil.getTokenSession();
        tokenSession.set(FingerprintKeys.Admin.IP, ip);
        tokenSession.set(FingerprintKeys.Admin.UA, ua);
        tokenSession.set(FingerprintKeys.Admin.DEVICE_ID, deviceId);
    }

    /** 已登录请求按配置比对管理端指纹；开启项缺失或不一致则注销并按未登录处理。三项全关则跳过。 */
    public void checkOrKickAsNotLogin(HttpServletRequest request) {
        boolean checkIp = isFpCheckEnabled(SecurityConfigCodes.FP_CHECK_IP);
        boolean checkUa = isFpCheckEnabled(SecurityConfigCodes.FP_CHECK_UA);
        boolean checkDevice = isFpCheckEnabled(SecurityConfigCodes.FP_CHECK_DEVICE);
        if (!checkIp && !checkUa && !checkDevice) {
            return;
        }

        String deviceId = request.getHeader(FingerprintKeys.Admin.DEVICE_ID_HEADER);
        String ip = ServletUtils.getIP(request);
        String ua = StrUtil.nullToDefault(request.getHeader("User-Agent"), "");

        String tokenValue = StpUtil.getTokenValue();
        SaSession tokenSession = StrUtil.isBlank(tokenValue)
                ? null
                : StpUtil.getStpLogic().getTokenSessionByToken(tokenValue, false);
        boolean mismatch = tokenSession == null
                || (checkDevice && (StrUtil.isBlank(deviceId)
                || !deviceId.equals(tokenSession.getString(FingerprintKeys.Admin.DEVICE_ID))))
                || (checkIp && !ip.equals(tokenSession.getString(FingerprintKeys.Admin.IP)))
                || (checkUa && !ua.equals(StrUtil.nullToDefault(tokenSession.getString(FingerprintKeys.Admin.UA), "")));
        if (mismatch) {
            // 一律当未登录：先注销再触发标准 NotLogin
            if (StpUtil.isLogin()) {
                StpUtil.logout();
            }
            StpUtil.checkLogin();
        }
    }

    /** 配置缺省或未启用时按 true，保持与改造前「全开比对」一致。 */
    private boolean isFpCheckEnabled(String configCode) {
        return configRuntimeService.getBoolean(configCode).orElse(true);
    }

    private String requireDeviceId(HttpServletRequest request) {
        String deviceId = request.getHeader(FingerprintKeys.Admin.DEVICE_ID_HEADER);
        if (StrUtil.isBlank(deviceId)) {
            throw new BusinessException("参数不合法");
        }
        // 限制长度，避免异常超长 Header
        if (deviceId.length() > 128) {
            throw new BusinessException("参数不合法");
        }
        return deviceId.trim();
    }
}
