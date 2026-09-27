package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.common.ConfigAuditCodes;
import com.ylmao.admin.common.UploadConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.log.ConfigAuditHolder;
import com.ylmao.admin.config.log.ConfigAuditItem;
import com.ylmao.admin.dto.ConfigDto;
import com.ylmao.admin.entity.Config;
import com.ylmao.admin.mapper.ConfigMapper;
import com.ylmao.admin.vo.ConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigMapper configMapper;
    private final ConfigRuntimeService configRuntimeService;
    private final JsonMapper jsonMapper;

    public List<ConfigVo.ConfigListVo> selectByGroup(String configGroup) {
        if (StrUtil.isBlank(configGroup)) {
            throw new BusinessException("配置分组不能为空");
        }
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigGroup, configGroup);
        wrapper.orderByAsc(Config::getOrderNum).orderByAsc(Config::getConfigId);
        return configMapper.selectList(wrapper).stream().map(ConfigVo.ConfigListVo::from).toList();
    }

    @Transactional
    public void updateGroup(ConfigDto.GroupUpdate groupUpdate) {
        for (ConfigDto.GroupConfig groupConfig : groupUpdate.configs()) {
            updateGroupConfig(groupUpdate.configGroup(), groupConfig);
        }
        configRuntimeService.refreshCache();
    }

    private void updateGroupConfig(String configGroup, ConfigDto.GroupConfig groupConfig) {
        Config config = configMapper.selectById(groupConfig.configId());
        if (config == null || !configGroup.equals(config.getConfigGroup())) {
            throw new BusinessException("配置项不属于当前分组");
        }
        if (StrUtil.isNotBlank(groupConfig.configCode()) && !groupConfig.configCode().equals(config.getConfigCode())) {
            throw new BusinessException("配置编码参数不合法");
        }
        // 预览路由与白名单写死 /upload，禁止改公开前缀，避免 URL 404/鉴权错位。
        if (UploadConfigCodes.PUBLIC_URL_PREFIX.equals(config.getConfigCode())
                && !Objects.equals("/upload", StrUtil.nullToEmpty(groupConfig.configValue()).trim())) {
            throw new BusinessException("公开访问前缀固定为 /upload，不可修改");
        }
        validateConfigValue(config.getValueType(), groupConfig.configValue());
        String beforeValue = config.getConfigValue();
        Integer beforeEnabled = config.getIsEnabled();
        Integer afterEnabled = groupConfig.isEnabled() != null ? groupConfig.isEnabled() : beforeEnabled;
        config.setConfigValue(groupConfig.configValue());
        if (groupConfig.isEnabled() != null) {
            if (invalidSwitch(groupConfig.isEnabled())) {
                throw new BusinessException("配置状态参数不合法");
            }
            config.setIsEnabled(groupConfig.isEnabled());
        }
        int rows = configMapper.updateById(config);
        if (rows <= 0) {
            throw new BusinessException("保存配置失败");
        }
        // 分组批量保存：值与启停都未变则不记。
        if (configChanged(beforeValue, groupConfig.configValue(), beforeEnabled, afterEnabled)) {
            ConfigAuditHolder.add(new ConfigAuditItem(
                    ConfigAuditCodes.ACTION_UPDATE,
                    config.getConfigCode(),
                    config.getConfigName(),
                    config.getIsBuiltin(),
                    beforeValue,
                    groupConfig.configValue(),
                    beforeEnabled,
                    afterEnabled
            ));
        }
    }

    private boolean configChanged(String beforeValue, String afterValue, Integer beforeEnabled, Integer afterEnabled) {
        return !Objects.equals(StrUtil.nullToEmpty(beforeValue), StrUtil.nullToEmpty(afterValue))
                || !Objects.equals(beforeEnabled, afterEnabled);
    }

    private void validateConfigValue(String valueType, String configValue) {
        if ("number".equals(valueType) && StrUtil.isNotBlank(configValue)) {
            try {
                new BigDecimal(configValue);
            } catch (NumberFormatException ex) {
                throw new BusinessException("数字配置值不合法");
            }
        }
        if ("boolean".equals(valueType) && StrUtil.isNotBlank(configValue)
                && !"true".equals(configValue) && !"false".equals(configValue)) {
            throw new BusinessException("布尔配置值只能为 true 或 false");
        }
        if ("json".equals(valueType) && StrUtil.isNotBlank(configValue)) {
            try {
                String jsonValue = configValue.trim();
                if (!jsonValue.startsWith("{") && !jsonValue.startsWith("[")) {
                    throw new IllegalArgumentException("JSON value must be object or array");
                }
                jsonMapper.readTree(jsonValue);
            } catch (Exception ex) {
                throw new BusinessException("JSON配置值不合法");
            }
        }
    }

    private boolean invalidSwitch(Integer value) {
        return value == null || (value != 0 && value != 1);
    }
}
