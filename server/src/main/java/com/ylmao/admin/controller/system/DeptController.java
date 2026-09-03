package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.DeptDto;
import com.ylmao.admin.service.DeptService;
import com.ylmao.admin.vo.DeptVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor
public class DeptController extends BaseController {

    private final DeptService deptService;

    @Log(title = "部门树查询", businessType = "QUERY")
    @SaCheckPermission("system:dept:tree")
    @GetMapping("/tree")
    public R<?> deptTree() {
        return okData(deptService.listOptions());
    }

    @Log(title = "部门列表查询", businessType = "QUERY")
    @SaCheckPermission("system:dept:select")
    @GetMapping("/list")
    public R<?> deptList(@Valid DeptDto.DeptList deptList) {
        List<DeptVo.DeptListVo> list = deptService.selectList(deptList);
        return pageData(list, list.size());
    }

    @Log(title = "新增部门数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:dept:insert")
    @PostMapping("/add")
    public R<?> deptInsert(@Valid @RequestBody DeptDto.DeptInsert deptInsert) {
        deptService.insert(deptInsert);
        return success();
    }

    @Log(title = "修改部门数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dept:update")
    @PutMapping("/update")
    public R<?> deptUpdate(@Valid @RequestBody DeptDto.DeptUpdate deptUpdate) {
        deptService.updateById(deptUpdate);
        return success();
    }

    @Log(title = "删除部门数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:dept:delete")
    @DeleteMapping("/delete")
    public R<?> deptDelete(String ids) {
        deptService.deleteById(ids);
        return success();
    }

    @Log(title = "修改部门状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dept:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateDeptEnabled(@Valid @RequestBody DeptDto.UpdateEnabled updateEnabled) {
        deptService.updateDeptEnabled(updateEnabled);
        return success();
    }

    @Log(title = "查询部门名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:dept:insert", "system:dept:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    public R<Boolean> checkDeptNameUnique(String parentId, String deptName) {
        return R.ok(deptService.checkDeptNameUnique(parentId, deptName) == null);
    }

    @Log(title = "查询上级部门", businessType = "QUERY")
    @SaCheckPermission("system:dept:select")
    @GetMapping("/selectParent")
    public R<?> selectDeptParent() {
        return okData(deptService.listOptions());
    }
}
