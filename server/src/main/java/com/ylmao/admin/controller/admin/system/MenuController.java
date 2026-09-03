package com.ylmao.admin.controller.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.MenuDto;
import com.ylmao.admin.service.MenuService;
import com.ylmao.admin.vo.MenuVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController extends BaseController {

    private final MenuService menuService;

    @SaCheckPermission("system:role:auth")
    @GetMapping("/roleTree")
    public R<?> queryRoleMenuTree(String roleId) {
        return okData(menuService.queryMenuCheckVoByRoleId(roleId));
    }

    @Log(title = "菜单授权保存")
    // 保存后由 MenuService 清理在线用户的权限码 Session 缓存。
    @SaCheckPermission("system:role:auth")
    @PutMapping("/roleMenu")
    public R<?> saveRoleMenu(@Valid @RequestBody MenuDto.RoleMenuSave roleMenuSave) {
        menuService.updateRoleMenu(roleMenuSave.roleId(), roleMenuSave.menuIds());
        return success();
    }

    @Log(title = "菜单列表查询", businessType = "QUERY")
    @SaCheckPermission("system:menu:select")
    @GetMapping("/list")
    public R<?> menuList(@Valid MenuDto.MenuList menuList) {
        List<MenuVo.MenuListVo> list = menuService.selectList(menuList);
        return pageData(list, list.size());
    }

    @Log(title = "新增菜单数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:menu:insert")
    @PostMapping("/add")
    public R<?> menuInsert(@Valid @RequestBody MenuDto.MenuInsert menuInsert) {
        menuService.insert(menuInsert);
        return success();
    }

    @Log(title = "修改菜单数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:menu:update")
    @PutMapping("/update")
    public R<?> menuUpdate(@Valid @RequestBody MenuDto.MenuUpdate menuUpdate) {
        menuService.updateById(menuUpdate);
        return success();
    }

    @Log(title = "删除菜单数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:menu:delete")
    @DeleteMapping("/delete")
    public R<?> menuDelete(String ids) {
        menuService.deleteById(ids);
        return success();
    }

    @Log(title = "查询菜单名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:menu:insert", "system:menu:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    public R<Boolean> checkMenuNameUnique(String parentId, String menuName) {
        return R.ok(menuService.checkMenuNameUnique(parentId, menuName) == null);
    }

    @Log(title = "查询权限标识是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:menu:insert", "system:menu:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    public R<Boolean> checkMenuCodeUnique(String permCode) {
        return R.ok(menuService.checkMenuCodeUnique(permCode) == null);
    }

    @Log(title = "修改菜单状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:menu:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateMenuEnabled(@Valid @RequestBody MenuDto.UpdateEnabled updateEnabled) {
        menuService.updateMenuEnabled(updateEnabled);
        return success();
    }

    @SaCheckPermission(value = {"system:menu:insert", "system:menu:update"}, mode = SaMode.OR)
    @GetMapping("/selectParent")
    public R<?> selectMenuParent() {
        return okData(menuService.selectParentVoList());
    }
}
