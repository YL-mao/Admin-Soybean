package com.ylmao.admin.controller.admin.auth;

import cn.dev33.satoken.exception.NotLoginException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Log(title = "管理端注销", loggingType = "LOGIN", businessType = "LOGOUT")
    @PostMapping("/logout")
    public R<Void> adminAuthLogout() {
        try {
            if (StpUtil.isLogin()) {
                StpUtil.logout();
            }
        } catch (NotLoginException ignored) {
            // 已失效 / 冻结：前端清本地即可
        }
        return R.ok();
    }

    /** 免登录品牌快照：登录页 / 布局配活用，字段来自 system.*。 */
    @GetMapping("/getBranding")
    public R<AdminAuthVo.BrandingResult> adminAuthGetBranding() {
        return R.ok(adminAuthService.buildBranding());
    }

    /** 薄版用户信息：角色码 + 按钮权限码（来自 sys_menu）。 */
    @GetMapping("/getUserInfo")
    public R<AdminAuthVo.UserInfoResult> adminAuthGetUserInfo() {
        return R.ok(adminAuthService.buildUserInfo());
    }

    /** 动态路由树：按当前用户角色过滤 sys_menu（目录/菜单）。 */
    @GetMapping("/getUserRoutes")
    public R<AdminAuthVo.UserRouteResult> adminAuthGetUserRoutes() {
        return R.ok(adminAuthService.buildUserRoutes());
    }

    /** 动态模式下探测路由是否存在于当前授权菜单。 */
    @GetMapping("/isRouteExist")
    public R<Boolean> adminAuthIsRouteExist(@RequestParam String routeName) {
        return R.ok(adminAuthService.isRouteExist(routeName));
    }
}
