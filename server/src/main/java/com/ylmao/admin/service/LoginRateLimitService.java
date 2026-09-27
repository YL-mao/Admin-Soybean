package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.common.SecurityConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 登录 / 验证码 Redis 固定窗口限流（多实例共享）。
 * 阈值读 security.*；0 表示关闭对应项，winMinLim=0 关闭整组软拦。
 */
@Service
@RequiredArgsConstructor
public class LoginRateLimitService {

    private static final String TOO_FREQUENT = "操作过于频繁，请稍后再试";

    /** INCR + 首次 EXPIRE 原子脚本，避免计数无 TTL 永久卡住。 */
    private static final DefaultRedisScript<Long> INCR_WITH_EXPIRE = new DefaultRedisScript<>(
            "local c = redis.call('INCR', KEYS[1]) "
                    + "if c == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end "
                    + "return c",
            Long.class);

    private final StringRedisTemplate stringRedisTemplate;
    private final ConfigRuntimeService configRuntimeService;

    public void checkLoginIp(String ip) {
        // 登录 IP 软拦：超限抛业务异常。
        if (!allowHit(RedisKeys.rateLoginIp(normalizeIp(ip)), SecurityConfigCodes.LOGIN_IP_LIMIT)) {
            throw new BusinessException(TOO_FREQUENT);
        }
    }

    public void checkLoginAccount(String userAccount) {
        if (StrUtil.isBlank(userAccount)) {
            return;
        }
        // 登录账号软拦：超限抛业务异常。
        if (!allowHit(RedisKeys.rateLoginAccount(userAccount.trim()), SecurityConfigCodes.LOGIN_ACCOUNT_LIMIT)) {
            throw new BusinessException(TOO_FREQUENT);
        }
    }

    /**
     * 验证码 IP 软拦。
     *
     * @return true 允许发图；false 已超限（由调用方返回兜底图）
     */
    public boolean tryCaptchaIp(String ip) {
        return allowHit(RedisKeys.rateCaptchaIp(normalizeIp(ip)), SecurityConfigCodes.CAPTCHA_IP_LIMIT);
    }

    private boolean allowHit(String redisKey, String limitConfigCode) {
        int windowMinutes = configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.RATE_WINDOW_MINUTES);
        // 窗口为 0：整组软拦关闭。
        if (windowMinutes == 0) {
            return true;
        }
        int limit = configRuntimeService.requireNonNegativeInt(limitConfigCode);
        // 该项阈值为 0：仅关闭这一维限流。
        if (limit == 0) {
            return true;
        }
        long windowSeconds = Math.max(1L, windowMinutes * 60L);
        Long count = stringRedisTemplate.execute(
                INCR_WITH_EXPIRE,
                Collections.singletonList(redisKey),
                String.valueOf(windowSeconds));
        // Redis 异常时 fail-closed，避免限流失效被刷。
        if (count == null) {
            return false;
        }
        return count <= limit;
    }

    private static String normalizeIp(String ip) {
        return StrUtil.blankToDefault(ip, "unknown");
    }
}
