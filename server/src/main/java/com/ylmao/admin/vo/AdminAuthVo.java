package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/** Soybean 管理端认证接口出参。 */
public class AdminAuthVo {

    /**
     * 登录成功：token + 最小用户信息，便于立刻跳转；
     * 完整 roles/buttons 仍以 getUserInfo 为准。
     */
    public record LoginResult(
            String token,
            String userId,
            String userName,
            List<String> roles
    ) {
    }

    /** 当前登录用户：角色码 + 按钮权限码（sys_menu.menu_type=2）+ 头像（顶栏同步）。 */
    public record UserInfoResult(
            String userId,
            String userName,
            String userAvatar,
            List<String> roles,
            List<String> buttons
    ) {
    }

    /** 动态路由：对齐 Soybean ElegantConstRoute 树 + 首页 route name。 */
    public record UserRouteResult(
            List<RouteItem> routes,
            String home
    ) {
    }

    /** 免登录品牌快照：对应 sys_config 的 system.* 展示值。 */
    public record BrandingResult(
            String name,
            String shortName,
            String logo,
            String favicon,
            String copyright,
            String adminMail,
            String version,
            String website,
            String icp,
            String policeIcp
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RouteItem(
            String id,
            String name,
            String path,
            String component,
            Map<String, Object> meta,
            List<RouteItem> children
    ) {
    }
}
