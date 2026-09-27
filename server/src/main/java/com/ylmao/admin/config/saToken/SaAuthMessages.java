package com.ylmao.admin.config.saToken;

/**
 * 鉴权对外文案：不回传 Sa-Token 原文（含 token 值 / 冻结细节 / 权限码）。
 */
public final class SaAuthMessages {

    /** 未登录、失效、冻结、顶下线等统一提示。 */
    public static final String NOT_LOGIN = "登录已失效，请重新登录";

    /** 无权限 / 无角色：不回传具体权限码。 */
    public static final String NO_PERMISSION = "无操作权限";

    private SaAuthMessages() {
    }
}
