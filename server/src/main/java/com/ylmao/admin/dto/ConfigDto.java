package com.ylmao.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ConfigDto {

    public record GroupUpdate(
            @NotBlank(message = "配置分组不能为空") String configGroup,
            @NotEmpty(message = "配置项参数不合法") @Valid List<GroupConfig> configs
    ) {
    }

    public record GroupConfig(
            @NotBlank(message = "配置项参数不合法") String configId,
            String configCode,
            String configValue,
            Integer isEnabled
    ) {
    }
}
