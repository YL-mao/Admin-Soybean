package com.ylmao.admin.common;

/**
 * 业务 Redis key 约定；与 Sa-Token 自有 key 隔离，统一 ylmao: 前缀。
 */
public final class RedisKeys {

    private RedisKeys() {
    }

    public static String captcha(String captchaId) {
        return "ylmao:captcha:" + captchaId;
    }

    public static String loginFailAccount(String userAccount) {
        return "ylmao:login:fail:account:" + userAccount;
    }

    public static String loginFailIp(String ip) {
        return "ylmao:login:fail:ip:" + ip;
    }

    public static String rateLoginIp(String ip) {
        return "ylmao:rate:login:ip:" + ip;
    }

    public static String rateLoginAccount(String userAccount) {
        return "ylmao:rate:login:account:" + userAccount;
    }

    public static String rateCaptchaIp(String ip) {
        return "ylmao:rate:captcha:ip:" + ip;
    }

    public static String jobLock(String jobCode) {
        return "ylmao:job:lock:" + jobCode;
    }

    public static String config(String configCode) {
        return "ylmao:config:data:" + configCode;
    }

    /** 已缓存配置编码列表（string JSON），刷新时用于清理旧 key。 */
    public static final String CONFIG_INDEX = "ylmao:meta:config:codes";

    public static String dictOptions(String dictTypeCode) {
        return "ylmao:dict:options:" + dictTypeCode;
    }

    /** 已缓存字典类型编码列表（string JSON），全量刷新时用于清理旧 key。 */
    public static final String DICT_INDEX = "ylmao:meta:dict:codes";
}
