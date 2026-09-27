package com.ylmao.admin.controller.admin.setting;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.ylmao.admin.common.ConfigAuditCodes;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.ConfigDto;
import com.ylmao.admin.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统配置", description = "按分组读取与保存系统参数")
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class ConfigController extends BaseController {

    private final ConfigService configService;

    @Operation(summary = "配置分组明细")
    @Log(title = "系统配置分组明细", businessType = "QUERY")
    // 上传配置入口已迁到文件页，允许仅持有分组权限（无需 system:config:view）访问对应分组。
    @SaCheckPermission(value = {"system:config:system", "system:config:upload", "system:config:log", "system:config:security", "system:config:job", "system:config:notice"}, mode = SaMode.OR)
    @GetMapping("/group")
    public R<?> configGroup(String configGroup) {
        checkGroupPermission(configGroup);
        return okData(configService.selectByGroup(configGroup));
    }

    @Operation(summary = "按分组保存配置")
    @Log(title = ConfigAuditCodes.OPERATE_TITLE, businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission(value = {"system:config:system", "system:config:upload", "system:config:log", "system:config:security", "system:config:job", "system:config:notice"}, mode = SaMode.OR)
    @PutMapping("/updateGroup")
    public R<?> updateConfigGroup(@Valid @RequestBody ConfigDto.GroupUpdate groupUpdate) {
        checkGroupPermission(groupUpdate == null ? null : groupUpdate.configGroup());
        configService.updateGroup(groupUpdate);
        return success();
    }

    private void checkGroupPermission(String configGroup) {
        // 固定配置页按分组权限控制 Tab，同时后端再次校验分组访问权限。
        if (configGroup == null || configGroup.trim().isEmpty()) {
            throw new BusinessException("配置分组不能为空");
        }
        StpUtil.checkPermission("system:config:" + configGroup);
    }
}
