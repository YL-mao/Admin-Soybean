package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.common.SecurityConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(LoginRateLimitService.class);

    private static final String TOO_FREQUENT = "操作过于频繁，请稍后再试";
    private static final String SERVICE_BUSY = "服务繁忙，请稍后再试";

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
        HitResult hit = allowHit(RedisKeys.rateLoginIp(normalizeIp(ip)), SecurityConfigCodes.LOGIN_IP_LIMIT);
        if (hit == HitResult.DENY) {
            throw new BusinessException(TOO_FREQUENT);
        }
        if (hit == HitResult.UNAVAILABLE) {
            throw new BusinessException(SERVICE_BUSY);
        }
    }

    public void checkLoginAccount(String userAccount) {
        if (StrUtil.isBlank(userAccount)) {
            return;
        }
        // 登录账号软拦：超限抛业务异常。
        HitResult hit = allowHit(RedisKeys.rateLoginAccount(userAccount.trim()), SecurityConfigCodes.LOGIN_ACCOUNT_LIMIT);
        if (hit == HitResult.DENY) {
            throw new BusinessException(TOO_FREQUENT);
        }
        if (hit == HitResult.UNAVAILABLE) {
            throw new BusinessException(SERVICE_BUSY);
        }
    }

    /**
     * 验证码 IP 软拦。
     *
     * @return true 允许发图；false 已超限或 Redis 不可用（由调用方返回兜底图）
     */
    public boolean tryCaptchaIp(String ip) {
        HitResult hit = allowHit(RedisKeys.rateCaptchaIp(normalizeIp(ip)), SecurityConfigCodes.CAPTCHA_IP_LIMIT);
        return hit == HitResult.ALLOW;
    }

    private HitResult allowHit(String redisKey, String limitConfigCode) {
        int windowMinutes = configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.RATE_WINDOW_MINUTES);
        // 窗口为 0：整组软拦关闭。
        if (windowMinutes == 0) {
            return HitResult.ALLOW;
        }
        int limit = configRuntimeService.requireNonNegativeInt(limitConfigCode);
        // 该项阈值为 0：仅关闭这一维限流。
        if (limit == 0) {
            return HitResult.ALLOW;
        }
        long windowSeconds = Math.max(1L, windowMinutes * 60L);
        Long count;
        try {
            count = stringRedisTemplate.execute(
                    INCR_WITH_EXPIRE,
                    Collections.singletonList(redisKey),
                    String.valueOf(windowSeconds));
        } catch (RuntimeException ex) {
            // Redis 异常时 fail-closed，与「超限」区分文案。
            log.error("限流 Redis 不可用 key={}", redisKey, ex);
            return HitResult.UNAVAILABLE;
        }
        if (count == null) {
            return HitResult.UNAVAILABLE;
        }
        return count <= limit ? HitResult.ALLOW : HitResult.DENY;
    }

    private static String normalizeIp(String ip) {
        return StrUtil.blankToDefault(ip, "unknown");
    }

    private enum HitResult {
        ALLOW,
        DENY,
        UNAVAILABLE
    }
}
