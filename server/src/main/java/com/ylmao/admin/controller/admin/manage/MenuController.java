package com.ylmao.admin.controller.admin.manage;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.MenuDto;
import com.ylmao.admin.service.MenuService;
import com.ylmao.admin.vo.MenuVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单", description = "菜单/权限树与授权")
@RestController
@RequestMapping("/api/admin/menu")
@RequiredArgsConstructor
public class MenuController extends BaseController {

    private final MenuService menuService;

    @Operation(summary = "角色菜单授权树")
    @SaCheckPermission("system:role:auth")
    @GetMapping("/roleTree")
    public R<?> menuRoleTree(String roleId) {
        return okData(menuService.queryMenuCheckVoByRoleId(roleId));
    }

    @Operation(summary = "保存角色菜单授权")
    @Log(title = "菜单授权保存")
    // 保存后由 MenuService 清理在线用户的权限码 Session 缓存。
    @SaCheckPermission("system:role:auth")
    @PutMapping("/roleMenu")
    public R<?> menuSaveRoleMenu(@Valid @RequestBody MenuDto.RoleMenuSave roleMenuSave) {
        menuService.updateRoleMenu(roleMenuSave.roleId(), roleMenuSave.menuIds());
        return success();
    }

    @Operation(summary = "菜单列表")
    @Log(title = "菜单列表查询", businessType = "QUERY")
    @SaCheckPermission("system:menu:select")
    @GetMapping("/list")
    public R<?> menuList(@Valid MenuDto.MenuList menuList) {
        List<MenuVo.MenuListVo> list = menuService.selectList(menuList);
        return pageData(list, list.size());
    }

    @Operation(summary = "新增菜单")
    @Log(title = "新增菜单数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:menu:insert")
    @PostMapping("/add")
    public R<?> menuInsert(@Valid @RequestBody MenuDto.MenuInsert menuInsert) {
        menuService.insert(menuInsert);
        return success();
    }

    @Operation(summary = "修改菜单")
    @Log(title = "修改菜单数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:menu:update")
    @PutMapping("/update")
    public R<?> menuUpdate(@Valid @RequestBody MenuDto.MenuUpdate menuUpdate) {
        menuService.updateById(menuUpdate);
        return success();
    }

    @Operation(summary = "删除菜单")
    @Log(title = "删除菜单数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:menu:delete")
    @DeleteMapping("/delete")
    public R<?> menuDelete(String ids) {
        menuService.deleteById(ids);
        return success();
    }

    @Operation(summary = "菜单名称是否唯一")
    @Log(title = "查询菜单名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:menu:insert", "system:menu:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    public R<Boolean> checkMenuNameUnique(String parentId, String menuName) {
        return R.ok(menuService.checkMenuNameUnique(parentId, menuName) == null);
    }

    @Operation(summary = "同父权限标识是否唯一")
    @Log(title = "查询同父权限标识是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:menu:insert", "system:menu:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    public R<Boolean> checkMenuCodeUnique(String parentId, String permCode) {
        return R.ok(menuService.checkMenuCodeUnique(parentId, permCode) == null);
    }

    @Operation(summary = "修改菜单启停状态")
    @Log(title = "修改菜单状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:menu:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateMenuEnabled(@Valid @RequestBody MenuDto.UpdateEnabled updateEnabled) {
        menuService.updateMenuEnabled(updateEnabled);
        return success();
    }

    @Operation(summary = "查询上级菜单")
    @SaCheckPermission(value = {"system:menu:insert", "system:menu:update"}, mode = SaMode.OR)
    @GetMapping("/selectParent")
    public R<?> menuSelectParent() {
        return okData(menuService.selectParentVoList());
    }
}
