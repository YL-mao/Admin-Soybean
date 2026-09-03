package com.ylmao.admin.config.saToken;

/**
 * 鉴权对外文案：不回传 Sa-Token 原文（含 token 值 / 冻结细节）。
 */
public final class SaAuthMessages {

    /** 未登录、失效、冻结、顶下线等统一提示。 */
    public static final String NOT_LOGIN = "登录已失效，请重新登录";

    private SaAuthMessages() {
    }
}
