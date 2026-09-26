package com.ylmao.admin.common;

/**
 * 登录安全策略配置编码（security.*）。
 */
public final class SecurityConfigCodes {

    private SecurityConfigCodes() {
    }

    /** 账号连续失败锁定阈值；0 不锁号。 */
    public static final String ACCOUNT_FAIL_LIMIT = "security.acctFailLim";
    /** IP 鉴权失败拉黑阈值；0 不自动拉黑。 */
    public static final String IP_FAIL_LIMIT = "security.ipFailLim";
    /** 自动拉黑每次叠加分钟数；0 不自动拉黑。 */
    public static final String AUTO_BAN_MINUTES = "security.autoBanMin";

    /** 验证码接口每 IP 限流次数；0 关闭该项。 */
    public static final String CAPTCHA_IP_LIMIT = "security.capIpLim";
    /** 登录接口每 IP 限流次数；0 关闭该项。 */
    public static final String LOGIN_IP_LIMIT = "security.loginIpLim";
    /** 登录接口每账号限流次数；0 关闭该项。 */
    public static final String LOGIN_ACCOUNT_LIMIT = "security.loginAcctLim";
    /** 软拦固定窗口分钟；0 关闭整组软拦。 */
    public static final String RATE_WINDOW_MINUTES = "security.winMinLim";

    /** 会话指纹是否比对 IP；false 跳过。 */
    public static final String FP_CHECK_IP = "security.fpCheckIp";
    /** 会话指纹是否比对 User-Agent；false 跳过。 */
    public static final String FP_CHECK_UA = "security.fpCheckUa";
    /** 会话指纹是否比对 X-Device-Id；false 跳过。 */
    public static final String FP_CHECK_DEVICE = "security.fpCheckDevice";

    /**
     * security 强契约清单：启动须存在、启用、number、非负整数。
     * 含失败处罚 3 项 + 软拦 4 项。
     */
    public static final String[] REQUIRED_CODES = {
            ACCOUNT_FAIL_LIMIT,
            IP_FAIL_LIMIT,
            AUTO_BAN_MINUTES,
            CAPTCHA_IP_LIMIT,
            LOGIN_IP_LIMIT,
            LOGIN_ACCOUNT_LIMIT,
            RATE_WINDOW_MINUTES
    };

    /** 会话指纹三项：启动须存在、启用、boolean、值为 true/false。缺省按 true（与上线前全开行为一致）。 */
    public static final String[] FP_CHECK_CODES = {
            FP_CHECK_IP,
            FP_CHECK_UA,
            FP_CHECK_DEVICE
    };
}
