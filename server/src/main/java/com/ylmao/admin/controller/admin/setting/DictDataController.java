package com.ylmao.admin.controller.admin.setting;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.DictDataDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.DictDataService;
import com.ylmao.admin.service.DictRuntimeService;
import com.ylmao.admin.vo.DictVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "字典数据", description = "字典项 CRUD 与运行时选项")
@RestController
@RequestMapping("/api/admin/dictData")
@RequiredArgsConstructor
public class DictDataController extends BaseController {

    private final DictDataService dictDataService;
    private final DictRuntimeService dictRuntimeService;

    @Operation(summary = "字典选项")
    @Log(title = "字典选项查询", businessType = "QUERY")
    @GetMapping("/options")
    public R<?> dictDataOptions(String dictTypeCode) {
        // 运行时字典选项，供表单下拉/单选使用；登录即可访问，无需字典管理权限。
        return R.ok(dictRuntimeService.getOptions(dictTypeCode));
    }

    @Operation(summary = "刷新字典缓存")
    @Log(title = "刷新字典缓存", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dictData:update")
    @PatchMapping("/refreshCache")
    public R<?> refreshDictDataCache(String dictTypeCode) {
        dictRuntimeService.refreshCache(dictTypeCode);
        return success();
    }

    @Operation(summary = "字典数据分页列表")
    @Log(title = "字典数据分页查询", businessType = "QUERY")
    @SaCheckPermission("system:dictData:select")
    @GetMapping("/list")
    public R<?> dictDataList(@Valid PageQuery pageQuery, @Valid DictDataDto.DictDataList dictDataList) {
        // Controller 出口统一返回 VO，避免暴露字典数据 PO。
        IPage<DictVo.DictDataListVo> dictDataPage = dictDataService.selectPageList(pageQuery, dictDataList);
        return pageData(dictDataPage.getRecords(), dictDataPage.getTotal());
    }

    @Operation(summary = "新增字典数据")
    @Log(title = "新增字典数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:dictData:insert")
    @PostMapping("/add")
    public R<?> dictDataInsert(@Valid @RequestBody DictDataDto.DictDataInsert dictDataInsert) {
        dictDataService.insert(dictDataInsert);
        return success();
    }

    @Operation(summary = "修改字典数据")
    @Log(title = "修改字典数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dictData:update")
    @PutMapping("/update")
    public R<?> dictDataUpdate(@Valid @RequestBody DictDataDto.DictDataUpdate dictDataUpdate) {
        dictDataService.updateById(dictDataUpdate);
        return success();
    }

    @Operation(summary = "删除字典数据")
    @Log(title = "删除字典数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:dictData:delete")
    @DeleteMapping("/delete")
    public R<?> dictDataDelete(String ids) {
        dictDataService.deleteById(ids);
        return success();
    }

    @Operation(summary = "修改字典数据启停状态")
    @Log(title = "修改字典数据状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dictData:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateDictDataEnabled(@Valid @RequestBody DictDataDto.UpdateEnabled updateEnabled) {
        // 状态参数含义由 Service 统一校验，Controller 只负责转交 DTO。
        dictDataService.updateEnabled(updateEnabled);
        return success();
    }

    @Operation(summary = "修改字典默认项")
    @Log(title = "修改字典默认状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dictData:updateDefault")
    @PatchMapping("/updateDefault")
    public R<?> updateDictDataDefault(@Valid @RequestBody DictDataDto.UpdateDefault updateDefault) {
        // 默认项参数含义由 Service 统一校验，Controller 只负责转交 DTO。
        dictDataService.updateDefault(updateDefault);
        return success();
    }

    @Operation(summary = "字典标签是否唯一")
    @Log(title = "查询字典数据标签是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:dictData:insert", "system:dictData:update"}, mode = SaMode.OR)
    @GetMapping("/checkLabel")
    public R<Boolean> checkDictDataLabelUnique(String dictTypeCode, String dictDataLabel) {
        return R.ok(dictDataService.checkDictDataLabelUnique(dictTypeCode, dictDataLabel) == null);
    }

    @Operation(summary = "字典值是否唯一")
    @Log(title = "查询字典数据值是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:dictData:insert", "system:dictData:update"}, mode = SaMode.OR)
    @GetMapping("/checkValue")
    public R<Boolean> checkDictDataValueUnique(String dictTypeCode, String dictDataValue) {
        return R.ok(dictDataService.checkDictDataValueUnique(dictTypeCode, dictDataValue) == null);
    }
}
