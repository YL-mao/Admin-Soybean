package com.ylmao.admin.common;

/**
 * 各端会话指纹常量（按端分组）。引用示例：{@code FingerprintKeys.Admin.DEVICE_ID_HEADER}。
 */
public final class FingerprintKeys {

    private FingerprintKeys() {
    }

    /** 管理端：请求头与 Token-Session 键。 */
    public static final class Admin {

        /** 前端每次请求携带的设备标识 Header。 */
        public static final String DEVICE_ID_HEADER = "X-Device-Id";

        /**
         * Sa-Token 登录设备类型（{@code StpUtil.login(id, device)}）。
         * 与指纹 Header {@link #DEVICE_ID_HEADER} 无关；单登录时只顶同类型会话。
         */
        public static final String DEVICE_TYPE = "admin";

        public static final String IP = "fpIp";
        public static final String UA = "fpUa";
        public static final String DEVICE_ID = "fpDeviceId";

        private Admin() {
        }
    }
}
