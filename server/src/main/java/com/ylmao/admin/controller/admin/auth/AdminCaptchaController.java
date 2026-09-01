package com.ylmao.admin.controller.admin.auth;

import com.ylmao.admin.service.CaptchaService;
import com.ylmao.admin.service.LoginRateLimitService;
import com.ylmao.admin.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Soybean 管理端图形验证码：复用 {@link CaptchaService}，URL 前缀与认证同模块。 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminCaptchaController {

    private final CaptchaService captchaService;
    private final LoginRateLimitService loginRateLimitService;

    @GetMapping("/captchaImage")
    public void adminAuthCaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!loginRateLimitService.tryCaptchaIp(ServletUtils.getIP(request))) {
            captchaService.writeLimitedImage(response);
            return;
        }
        captchaService.writeImage(response);
    }
}
