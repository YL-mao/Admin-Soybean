package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.entity.Config;
import com.ylmao.admin.mapper.ConfigMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 配置运行时读取：启用配置写入 Redis，读取时直读 Redis（方案 A）。
 * 索引与缓存值均为 string，避免客户端对索引 key 误 GET 时 WRONGTYPE。
 */
@Service
@RequiredArgsConstructor
public class ConfigRuntimeService {

    private static final Logger log = LoggerFactory.getLogger(ConfigRuntimeService.class);
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final ConfigMapper configMapper;
    private final JsonMapper jsonMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void initCache() {
        refreshCache();
    }

    /** 全量重建 Redis 配置缓存；先覆盖写入再删孤儿，避免先 clear 造成读空窗。 */
    public void refreshCache() {
        List<String> oldCodes = readCodeIndex();
        List<Config> enabledList = configMapper.selectList(new LambdaQueryWrapper<Config>()
                .eq(Config::getIsEnabled, 1));
        List<String> codes = new ArrayList<>();
        for (Config config : enabledList) {
            if (config == null || StrUtil.isBlank(config.getConfigCode())) {
                continue;
            }
            String code = config.getConfigCode();
            CacheEntry entry = new CacheEntry(config.getConfigValue(), config.getValueType());
            stringRedisTemplate.opsForValue().set(RedisKeys.config(code), jsonMapper.writeValueAsString(entry));
            codes.add(code);
        }
        stringRedisTemplate.opsForValue().set(RedisKeys.CONFIG_INDEX, jsonMapper.writeValueAsString(codes));
        // 已停用项：新索引落定后再删，读路径不会踩空。
        Set<String> enabledCodes = new HashSet<>(codes);
        Set<String> keysToDelete = new HashSet<>();
        for (String oldCode : oldCodes) {
            if (StrUtil.isNotBlank(oldCode) && !enabledCodes.contains(oldCode)) {
                keysToDelete.add(RedisKeys.config(oldCode));
            }
        }
        if (!keysToDelete.isEmpty()) {
            stringRedisTemplate.delete(keysToDelete);
        }
    }

    public Optional<String> getString(String configCode) {
        return findEnabled(configCode).map(CacheEntry::configValue);
    }

    public Optional<Boolean> getBoolean(String configCode) {
        Optional<CacheEntry> configOpt = findEnabled(configCode);
        if (configOpt.isEmpty()) {
            return Optional.empty();
        }
        CacheEntry config = configOpt.get();
        String raw = config.configValue();
        if (StrUtil.isBlank(raw)) {
            return Optional.empty();
        }
        if (!"boolean".equals(config.valueType()) && !"true".equals(raw) && !"false".equals(raw)) {
            log.warn("配置读取失败 configCode={} reason=布尔值不合法 value={}", configCode, raw);
            return Optional.empty();
        }
        if ("true".equals(raw)) {
            return Optional.of(true);
        }
        if ("false".equals(raw)) {
            return Optional.of(false);
        }
        log.warn("配置读取失败 configCode={} reason=布尔值不合法 value={}", configCode, raw);
        return Optional.empty();
    }

    public Optional<BigDecimal> getNumber(String configCode) {
        Optional<CacheEntry> configOpt = findEnabled(configCode);
        if (configOpt.isEmpty()) {
            return Optional.empty();
        }
        String raw = configOpt.get().configValue();
        if (StrUtil.isBlank(raw)) {
            return Optional.empty();
        }
        try {
            return Optional.of(new BigDecimal(raw.trim()));
        } catch (NumberFormatException ex) {
            log.warn("配置读取失败 configCode={} reason=数字值不合法 value={}", configCode, raw);
            return Optional.empty();
        }
    }

    /**
     * 读取已启用的非负整数配置；缺失、非整数或负数视为契约破坏。
     * 供 security.* 等强契约配置使用。
     */
    public int requireNonNegativeInt(String configCode) {
        BigDecimal value = getNumber(configCode)
                .orElseThrow(() -> new IllegalStateException("安全配置缺失或未启用: " + configCode));
        try {
            int n = value.intValueExact();
            if (n < 0) {
                throw new IllegalStateException("安全配置不能为负数: " + configCode + "=" + value);
            }
            return n;
        } catch (ArithmeticException ex) {
            throw new IllegalStateException("安全配置必须为整数: " + configCode + "=" + value, ex);
        }
    }

    public Optional<JsonNode> getJson(String configCode) {
        Optional<CacheEntry> configOpt = findEnabled(configCode);
        if (configOpt.isEmpty()) {
            return Optional.empty();
        }
        String raw = configOpt.get().configValue();
        if (StrUtil.isBlank(raw)) {
            return Optional.empty();
        }
        try {
            String jsonValue = raw.trim();
            if (!jsonValue.startsWith("{") && !jsonValue.startsWith("[")) {
                log.warn("配置读取失败 configCode={} reason=JSON须为对象或数组", configCode);
                return Optional.empty();
            }
            return Optional.of(jsonMapper.readTree(jsonValue));
        } catch (Exception ex) {
            log.warn("配置读取失败 configCode={} reason={}", configCode, ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<CacheEntry> findEnabled(String configCode) {
        if (StrUtil.isBlank(configCode)) {
            return Optional.empty();
        }
        String json = stringRedisTemplate.opsForValue().get(RedisKeys.config(configCode));
        if (StrUtil.isBlank(json)) {
            return Optional.empty();
        }
        try {
            return Optional.of(jsonMapper.readValue(json, CacheEntry.class));
        } catch (Exception ex) {
            log.warn("配置缓存反序列化失败 configCode={} reason={}", configCode, ex.getMessage());
            return Optional.empty();
        }
    }

    private List<String> readCodeIndex() {
        String json = stringRedisTemplate.opsForValue().get(RedisKeys.CONFIG_INDEX);
        if (StrUtil.isBlank(json)) {
            return List.of();
        }
        try {
            List<String> codes = jsonMapper.readValue(json, STRING_LIST_TYPE);
            return codes == null ? List.of() : codes;
        } catch (Exception ex) {
            log.warn("配置索引反序列化失败 key={} reason={}", RedisKeys.CONFIG_INDEX, ex.getMessage());
            return List.of();
        }
    }

    /** Redis 中仅存运行时读取所需字段。 */
    private record CacheEntry(String configValue, String valueType) {
    }
}
