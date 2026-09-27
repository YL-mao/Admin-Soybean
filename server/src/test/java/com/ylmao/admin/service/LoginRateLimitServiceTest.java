package com.ylmao.admin.service;

import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.common.SecurityConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.mapper.ConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 软拦限流与配置强契约读取的基础单测（Mock Redis，不启 Spring）。
 */
@ExtendWith(MockitoExtension.class)
class LoginRateLimitServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private ConfigMapper configMapper;

    private ConfigRuntimeService configRuntimeService;
    private LoginRateLimitService loginRateLimitService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        configRuntimeService = new ConfigRuntimeService(configMapper, JsonMapper.builder().build(), stringRedisTemplate);
        loginRateLimitService = new LoginRateLimitService(stringRedisTemplate, configRuntimeService);
    }

    @Test
    void requireNonNegativeInt_ok() {
        stubConfig(SecurityConfigCodes.CAPTCHA_IP_LIMIT, "10");
        assertEquals(10, configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.CAPTCHA_IP_LIMIT));
    }

    @Test
    void requireNonNegativeInt_missing() {
        when(valueOperations.get(RedisKeys.config(SecurityConfigCodes.CAPTCHA_IP_LIMIT))).thenReturn(null);
        assertThrows(IllegalStateException.class,
                () -> configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.CAPTCHA_IP_LIMIT));
    }

    @Test
    void requireNonNegativeInt_negative() {
        stubConfig(SecurityConfigCodes.CAPTCHA_IP_LIMIT, "-1");
        assertThrows(IllegalStateException.class,
                () -> configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.CAPTCHA_IP_LIMIT));
    }

    @Test
    void softRate_windowZero_skipsIncrement() {
        stubConfig(SecurityConfigCodes.RATE_WINDOW_MINUTES, "0");
        assertTrue(loginRateLimitService.tryCaptchaIp("1.1.1.1"));
        verify(stringRedisTemplate, never()).execute(any(DefaultRedisScript.class), anyList(), anyString());
    }

    @Test
    void softRate_captchaLimitZero_skipsIncrement() {
        stubConfig(SecurityConfigCodes.RATE_WINDOW_MINUTES, "1");
        stubConfig(SecurityConfigCodes.CAPTCHA_IP_LIMIT, "0");
        assertTrue(loginRateLimitService.tryCaptchaIp("1.1.1.1"));
        verify(stringRedisTemplate, never()).execute(any(DefaultRedisScript.class), anyList(), anyString());
    }

    @Test
    void softRate_captchaOverLimit_returnsFalse() {
        stubConfig(SecurityConfigCodes.RATE_WINDOW_MINUTES, "1");
        stubConfig(SecurityConfigCodes.CAPTCHA_IP_LIMIT, "2");
        when(stringRedisTemplate.execute(any(DefaultRedisScript.class), eq(Collections.singletonList(RedisKeys.rateCaptchaIp("1.1.1.1"))), eq("60")))
                .thenReturn(3L);
        assertFalse(loginRateLimitService.tryCaptchaIp("1.1.1.1"));
    }

    @Test
    void softRate_loginIpOverLimit_throws() {
        stubConfig(SecurityConfigCodes.RATE_WINDOW_MINUTES, "1");
        stubConfig(SecurityConfigCodes.LOGIN_IP_LIMIT, "2");
        when(stringRedisTemplate.execute(any(DefaultRedisScript.class), eq(Collections.singletonList(RedisKeys.rateLoginIp("1.1.1.1"))), eq("60")))
                .thenReturn(3L);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginRateLimitService.checkLoginIp("1.1.1.1"));
        assertEquals("操作过于频繁，请稍后再试", ex.getMessage());
    }

    @Test
    void softRate_firstHit_usesLuaScript() {
        stubConfig(SecurityConfigCodes.RATE_WINDOW_MINUTES, "5");
        stubConfig(SecurityConfigCodes.CAPTCHA_IP_LIMIT, "10");
        String key = RedisKeys.rateCaptchaIp("1.1.1.1");
        when(stringRedisTemplate.execute(any(DefaultRedisScript.class), eq(Collections.singletonList(key)), eq("300")))
                .thenReturn(1L);
        assertTrue(loginRateLimitService.tryCaptchaIp("1.1.1.1"));
        verify(stringRedisTemplate).execute(any(DefaultRedisScript.class), eq(Collections.singletonList(key)), eq("300"));
    }

    @Test
    void softRate_redisException_failClosed() {
        stubConfig(SecurityConfigCodes.RATE_WINDOW_MINUTES, "1");
        stubConfig(SecurityConfigCodes.CAPTCHA_IP_LIMIT, "10");
        when(stringRedisTemplate.execute(any(DefaultRedisScript.class), anyList(), anyString()))
                .thenThrow(new RuntimeException("redis down"));
        assertFalse(loginRateLimitService.tryCaptchaIp("1.1.1.1"));
    }

    private void stubConfig(String configCode, String value) {
        when(valueOperations.get(RedisKeys.config(configCode)))
                .thenReturn("{\"configValue\":\"" + value + "\",\"valueType\":\"number\"}");
    }
}
