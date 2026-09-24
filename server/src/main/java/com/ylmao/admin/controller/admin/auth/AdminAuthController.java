package com.ylmao.admin.controller.admin.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.LoginDto;
import com.ylmao.admin.service.AdminAuthService;
import com.ylmao.admin.service.LoginService;
import com.ylmao.admin.vo.AdminAuthVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "管理端认证", description = "登录、注销、用户信息与路由")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final LoginService loginService;
    private final AdminAuthService adminAuthService;

    @Operation(summary = "管理端登录")
    @Log(title = "管理端登录", logType = "LOGIN", businessType = "LOGIN")
    @PostMapping("/login")
    public R<AdminAuthVo.LoginResult> adminAuthLogin(
            @Valid @RequestBody LoginDto.LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        loginService.login(loginRequest, request, response);
        return R.ok(adminAuthService.buildLoginResult(StpUtil.getTokenValue()));
    }

    @Operation(summary = "管理端注销")
    @Log(title = "管理端注销", logType = "LOGIN", businessType = "LOGOUT")
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
    @Operation(summary = "登录页品牌信息")
    @GetMapping("/getBranding")
    public R<AdminAuthVo.BrandingResult> adminAuthGetBranding() {
        return R.ok(adminAuthService.buildBranding());
    }

    /** 薄版用户信息：角色码 + 按钮权限码（来自 sys_menu）。 */
    @Operation(summary = "当前用户信息")
    @GetMapping("/getUserInfo")
    public R<AdminAuthVo.UserInfoResult> adminAuthGetUserInfo() {
        return R.ok(adminAuthService.buildUserInfo());
    }

    /** 动态路由树：按当前用户角色过滤 sys_menu（目录/菜单）。 */
    @Operation(summary = "当前用户路由")
    @GetMapping("/getUserRoutes")
    public R<AdminAuthVo.UserRouteResult> adminAuthGetUserRoutes() {
        return R.ok(adminAuthService.buildUserRoutes());
    }

    /** 动态模式下探测路由是否存在于当前授权菜单。 */
    @Operation(summary = "路由是否存在")
    @GetMapping("/isRouteExist")
    public R<Boolean> adminAuthIsRouteExist(@RequestParam String routeName) {
        return R.ok(adminAuthService.isRouteExist(routeName));
    }
}
