package com.ylmao.admin.controller.admin.org;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.DeptDto;
import com.ylmao.admin.service.DeptService;
import com.ylmao.admin.vo.DeptVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门", description = "部门树与 CRUD")
@RestController
@RequestMapping("/api/admin/dept")
@RequiredArgsConstructor
public class DeptController extends BaseController {

    private final DeptService deptService;

    @Operation(summary = "部门列表")
    @Log(title = "部门列表查询", businessType = "QUERY")
    @SaCheckPermission("system:dept:select")
    @GetMapping("/list")
    public R<?> deptList(@Valid DeptDto.DeptList deptList) {
        List<DeptVo.DeptListVo> list = deptService.selectList(deptList);
        return pageData(list, list.size());
    }

    @Operation(summary = "新增部门")
    @Log(title = "新增部门数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:dept:insert")
    @PostMapping("/add")
    public R<?> deptInsert(@Valid @RequestBody DeptDto.DeptInsert deptInsert) {
        deptService.insert(deptInsert);
        return success();
    }

    @Operation(summary = "修改部门")
    @Log(title = "修改部门数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dept:update")
    @PutMapping("/update")
    public R<?> deptUpdate(@Valid @RequestBody DeptDto.DeptUpdate deptUpdate) {
        deptService.updateById(deptUpdate);
        return success();
    }

    @Operation(summary = "删除部门")
    @Log(title = "删除部门数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:dept:delete")
    @DeleteMapping("/delete")
    public R<?> deptDelete(String ids) {
        deptService.deleteById(ids);
        return success();
    }

    @Operation(summary = "修改部门启停状态")
    @Log(title = "修改部门状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dept:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateDeptEnabled(@Valid @RequestBody DeptDto.UpdateEnabled updateEnabled) {
        deptService.updateDeptEnabled(updateEnabled);
        return success();
    }

    @Operation(summary = "部门名称是否唯一")
    @Log(title = "查询部门名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:dept:insert", "system:dept:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    public R<Boolean> checkDeptNameUnique(String parentId, String deptName) {
        return R.ok(deptService.checkDeptNameUnique(parentId, deptName) == null);
    }

    @Operation(summary = "查询上级部门")
    @Log(title = "查询上级部门", businessType = "QUERY")
    @SaCheckPermission("system:dept:select")
    @GetMapping("/selectParent")
    public R<?> selectDeptParent() {
        return okData(deptService.listOptions());
    }

    /** 用户分配等下拉：仅启用部门 */
    @Operation(summary = "部门下拉选项")
    @SaCheckPermission(value = {"system:user:insert", "system:user:update"}, mode = SaMode.OR)
    @GetMapping("/options")
    public R<?> deptOptions() {
        return okData(deptService.listEnabledOptions());
    }
}
