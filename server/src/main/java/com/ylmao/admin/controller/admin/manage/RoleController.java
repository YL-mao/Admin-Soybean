package com.ylmao.admin.controller.admin.manage;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.RoleDto;
import com.ylmao.admin.service.RoleService;
import com.ylmao.admin.vo.RoleVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "角色", description = "角色 CRUD 与下拉")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController extends BaseController {

    private final RoleService roleService;

    @Operation(summary = "角色分页列表")
    @Log(title = "角色分页查询", businessType = "QUERY")
    @SaCheckPermission("system:role:select")
    @GetMapping("/list")
    public R<?> roleList(@Valid PageQuery pageQuery, @Valid RoleDto.RoleList roleList) {
        IPage<RoleVo.RoleListVo> roleIPage = roleService.selectRolePageList(pageQuery, roleList.roleName());
        return pageData(roleIPage.getRecords(), roleIPage.getTotal());
    }

    @Operation(summary = "新增角色")
    @Log(title = "新增角色数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:role:insert")
    @PostMapping("/add")
    public R<?> roleInsert(@Valid @RequestBody RoleDto.RoleInsert roleInsert) {
        roleService.insert(roleInsert);
        return success();
    }

    @Operation(summary = "修改角色")
    @Log(title = "修改角色数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:role:update")
    @PutMapping("/update")
    public R<?> roleUpdate(@Valid @RequestBody RoleDto.RoleUpdate roleUpdate) {
        roleService.updateById(roleUpdate);
        return success();
    }

    @Operation(summary = "删除角色")
    @Log(title = "删除角色数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:role:delete")
    @DeleteMapping("/delete")
    public R<?> roleDelete(String ids) {
        roleService.deleteById(ids);
        return success();
    }

    @Operation(summary = "角色名称是否唯一")
    @Log(title = "查询角色名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:role:insert", "system:role:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    public R<Boolean> checkRoleNameUnique(String roleName) {
        return R.ok(roleService.checkRoleNameUnique(roleName) == null);
    }

    @Operation(summary = "角色编码是否唯一")
    @Log(title = "查询角色编码是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:role:insert", "system:role:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    public R<Boolean> checkRoleCodeUnique(String roleCode) {
        return R.ok(roleService.checkRoleCodeUnique(roleCode) == null);
    }

    @Operation(summary = "修改角色启停状态")
    @Log(title = "修改角色状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:role:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateRoleEnabled(@Valid @RequestBody RoleDto.UpdateEnabled updateEnabled) {
        // 状态参数含义由 Service 统一校验，Controller 只负责转交 DTO。
        roleService.updateEnabled(updateEnabled);
        return success();
    }

    /** 用户分配等下拉：返回 roleId + roleName */
    @Operation(summary = "角色下拉选项")
    @SaCheckPermission(value = {"system:user:insert", "system:user:update"}, mode = SaMode.OR)
    @GetMapping("/options")
    public R<?> roleOptions() {
        return okData(roleService.listOptions());
    }

}
