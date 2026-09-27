package com.ylmao.admin.controller.admin.ops;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.FilterDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.FilterService;
import com.ylmao.admin.vo.FilterVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "访问控制", description = "IP 黑白名单")
@RestController
@RequestMapping("/api/admin/filter")
@RequiredArgsConstructor
public class FilterController extends BaseController {

    private final FilterService filterService;

    @Operation(summary = "访问控制分页列表")
    @Log(title = "访问控制分页查询", businessType = "QUERY")
    @SaCheckPermission("system:filter:select")
    @GetMapping("/list")
    public R<?> filterList(@Valid PageQuery pageQuery, @Valid FilterDto.FilterList filterList) {
        IPage<FilterVo.FilterListVo> page = filterService.selectPage(pageQuery, filterList);
        return pageData(page.getRecords(), page.getTotal());
    }

    @Operation(summary = "新增访问控制")
    @Log(title = "新增访问控制", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:filter:insert")
    @PostMapping("/add")
    public R<?> filterInsert(@Valid @RequestBody FilterDto.FilterInsert filterInsert) {
        filterService.insert(filterInsert);
        return success();
    }

    @Operation(summary = "修改访问控制")
    @Log(title = "修改访问控制", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:filter:update")
    @PutMapping("/update")
    public R<?> filterUpdate(@Valid @RequestBody FilterDto.FilterUpdate filterUpdate) {
        filterService.update(filterUpdate);
        return success();
    }

    @Operation(summary = "删除访问控制")
    @Log(title = "删除访问控制", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:filter:delete")
    @DeleteMapping("/delete")
    public R<?> filterDelete(String ids) {
        filterService.deleteByIds(ids);
        return success();
    }

    @Operation(summary = "修改访问控制启停状态")
    @Log(title = "修改访问控制状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:filter:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateFilterEnabled(@Valid @RequestBody FilterDto.UpdateEnabled updateEnabled) {
        filterService.updateEnabled(updateEnabled);
        return success();
    }
}
