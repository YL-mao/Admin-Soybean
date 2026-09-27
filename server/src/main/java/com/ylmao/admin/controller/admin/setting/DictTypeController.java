package com.ylmao.admin.controller.admin.setting;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.DictTypeDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.DictTypeService;
import com.ylmao.admin.vo.DictVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "字典类型", description = "字典类型 CRUD")
@RestController
@RequestMapping("/api/admin/dictType")
@RequiredArgsConstructor
public class DictTypeController extends BaseController {

    private final DictTypeService dictTypeService;

    @Operation(summary = "字典类型分页列表")
    @Log(title = "字典类型分页查询", businessType = "QUERY")
    @SaCheckPermission("system:dictType:select")
    @GetMapping("/list")
    public R<?> dictTypeList(@Valid PageQuery pageQuery, @Valid DictTypeDto.DictTypeList dictTypeList) {
        // Controller 出口统一返回 VO，避免暴露字典类型 PO。
        IPage<DictVo.DictTypeListVo> dictTypePage = dictTypeService.selectPageList(pageQuery, dictTypeList);
        return pageData(dictTypePage.getRecords(), dictTypePage.getTotal());
    }

    @Operation(summary = "新增字典类型")
    @Log(title = "新增字典类型", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:dictType:insert")
    @PostMapping("/add")
    public R<?> dictTypeInsert(@Valid @RequestBody DictTypeDto.DictTypeInsert dictTypeInsert) {
        dictTypeService.insert(dictTypeInsert);
        return success();
    }

    @Operation(summary = "修改字典类型")
    @Log(title = "修改字典类型", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dictType:update")
    @PutMapping("/update")
    public R<?> dictTypeUpdate(@Valid @RequestBody DictTypeDto.DictTypeUpdate dictTypeUpdate) {
        dictTypeService.updateById(dictTypeUpdate);
        return success();
    }

    @Operation(summary = "删除字典类型")
    @Log(title = "删除字典类型", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:dictType:delete")
    @DeleteMapping("/delete")
    public R<?> dictTypeDelete(String ids) {
        dictTypeService.deleteById(ids);
        return success();
    }

    @Operation(summary = "修改字典类型启停状态")
    @Log(title = "修改字典类型状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dictType:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateDictTypeEnabled(@Valid @RequestBody DictTypeDto.UpdateEnabled updateEnabled) {
        // 状态参数含义由 Service 统一校验，Controller 只负责转交 DTO。
        dictTypeService.updateEnabled(updateEnabled);
        return success();
    }

    @Operation(summary = "字典编码是否唯一")
    @Log(title = "查询字典编码是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:dictType:insert", "system:dictType:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    public R<Boolean> checkDictTypeCodeUnique(String dictTypeCode) {
        return R.ok(dictTypeService.checkDictTypeCodeUnique(dictTypeCode) == null);
    }
}
