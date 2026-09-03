package com.ylmao.admin.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.FingerprintKeys;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * 会话指纹比对实现。当前调用方仅管理端（{@link FingerprintKeys.Admin}）；
 * 由谁调用由拦截层决定，本类不做路径分流。
 */
@Service
public class FingerprintService {

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

    /** 已登录请求比对管理端指纹；缺失或不一致则注销并按未登录处理。 */
    public void checkOrKickAsNotLogin(HttpServletRequest request) {
        String deviceId = request.getHeader(FingerprintKeys.Admin.DEVICE_ID_HEADER);
        String ip = ServletUtils.getIP(request);
        String ua = StrUtil.nullToDefault(request.getHeader("User-Agent"), "");

        String tokenValue = StpUtil.getTokenValue();
        SaSession tokenSession = StrUtil.isBlank(tokenValue)
                ? null
                : StpUtil.getStpLogic().getTokenSessionByToken(tokenValue, false);
        if (tokenSession == null
                || StrUtil.isBlank(deviceId)
                || !deviceId.equals(tokenSession.getString(FingerprintKeys.Admin.DEVICE_ID))
                || !ip.equals(tokenSession.getString(FingerprintKeys.Admin.IP))
                || !ua.equals(StrUtil.nullToDefault(tokenSession.getString(FingerprintKeys.Admin.UA), ""))) {
            // 一律当未登录：先注销再触发标准 NotLogin
            if (StpUtil.isLogin()) {
                StpUtil.logout();
            }
            StpUtil.checkLogin();
        }
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
