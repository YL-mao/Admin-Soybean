package com.ylmao.admin.controller.admin.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.LoginDto;
import com.ylmao.admin.service.AdminAuthService;
import com.ylmao.admin.service.LoginService;
import com.ylmao.admin.vo.AdminAuthVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Soybean 管理端认证：校验逻辑复用 {@link LoginService}；仅 Header token，不做 Cookie 会话恢复。
 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final LoginService loginService;
    private final AdminAuthService adminAuthService;

    @Log(title = "管理端登录", loggingType = "LOGIN", businessType = "LOGIN")
    @PostMapping("/login")
    public R<AdminAuthVo.LoginResult> adminAuthLogin(
            @Valid @RequestBody LoginDto.LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        loginService.login(loginRequest, request, response);
        return R.ok(adminAuthService.buildLoginResult(StpUtil.getTokenValue()));
    }

    @PostMapping("/logout")
    public R<Void> adminAuthLogout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        return R.ok();
    }
}
