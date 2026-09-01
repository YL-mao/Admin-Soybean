package com.ylmao.admin.vo;

import java.util.List;

/** Soybean 管理端认证接口出参。 */
public class AdminAuthVo {

    /**
     * 登录成功：token + 最小用户信息，供静态路由跳转；
     * buttons / getUserInfo 后续对接 sys_menu 再补。
     */
    public record LoginResult(
            String token,
            String userId,
            String userName,
            List<String> roles
    ) {
    }
}
