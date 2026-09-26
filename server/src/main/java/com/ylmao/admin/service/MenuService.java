package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.StpInterfaceImpl;
import com.ylmao.admin.dto.MenuDto;
import com.ylmao.admin.entity.Menu;
import com.ylmao.admin.entity.MenuRole;
import com.ylmao.admin.entity.RoleUser;
import com.ylmao.admin.mapper.MenuMapper;
import com.ylmao.admin.mapper.MenuRoleMapper;
import com.ylmao.admin.mapper.RoleUserMapper;
import com.ylmao.admin.vo.MenuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;
    private final MenuRoleMapper menuRoleMapper;
    private final RoleUserMapper roleUserMapper;

    /** 按用户角色并集组装侧栏菜单树（model.Menu）。 */
    public List<com.ylmao.admin.model.Menu> getUserMenuTree(String userId) {
        LambdaQueryWrapper<RoleUser> roleUserQueryWrapper = new LambdaQueryWrapper<>();
        roleUserQueryWrapper.eq(RoleUser::getUserId, userId);
        List<RoleUser> roleUserList = roleUserMapper.selectList(roleUserQueryWrapper);
        List<String> roleIds = new ArrayList<>();
        for (RoleUser item : roleUserList) {
            roleIds.add(item.getRoleId());
        }
        List<Menu> menuList = getMenuByRole(roleIds);
        return buildModelMenuList(menuList, "0");
    }

    /** 按角色 ID 列表一次查出启用菜单，供侧边栏组装使用。 */
    public List<Menu> getMenuByRole(List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return menuMapper.selectMenusByRoleIds(roleIds);
    }

    /** 递归组装前端菜单模型：外链优先，否则用路由 path。 */
    public List<com.ylmao.admin.model.Menu> buildModelMenuList(List<Menu> menuList, String parentId) {
        List<com.ylmao.admin.model.Menu> result = new ArrayList<>();
        for (Menu item : menuList) {
            if (item.getParentId().equals(parentId)) {
                List<com.ylmao.admin.model.Menu> childMenu = buildModelMenuList(menuList, item.getMenuId());
                com.ylmao.admin.model.Menu menu = new com.ylmao.admin.model.Menu();
                menu.setId(item.getMenuId());
                menu.setParentId(item.getParentId());
                menu.setTitle(item.getMenuName());
                menu.setType(item.getMenuType());
                menu.setIsBlank(normalizeIsBlank(item.getIsBlank()));
                menu.setIcon(item.getMenuIcon());
                // 有外链用 menuHref，否则用站内 routePath
                menu.setHref(StrUtil.isNotBlank(item.getMenuHref()) ? item.getMenuHref() : item.getRoutePath());
                if (!childMenu.isEmpty()) {
                    menu.setChildren(childMenu);
                }
                result.add(menu);
            }
        }
        return result;
    }

    /** 角色授权树接口出口，转换为 VO 避免暴露 PO。 */
    public List<MenuVo.MenuCheckVo> queryMenuCheckVoByRoleId(String roleId) {
        return menuMapper.queryMenuCheckArrByRoleId(roleId).stream()
                .map(MenuVo.MenuCheckVo::from)
                .toList();
    }

    /** 上级菜单下拉树，附带顶级节点供表单选择。 */
    public List<MenuVo.MenuParentVo> selectParentVoList() {
        List<MenuVo.MenuParentVo> list = selectList().stream()
                .map(MenuVo.MenuParentVo::from)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        list.add(new MenuVo.MenuParentVo("0", "-1", "顶级菜单", null));
        return list;
    }

    @Transactional
    public void updateRoleMenu(String roleId, String menuIds) {
        LambdaQueryWrapper<MenuRole> menuRoleQueryWrapper = new LambdaQueryWrapper<>();
        menuRoleQueryWrapper.eq(MenuRole::getRoleId, roleId);
        menuRoleMapper.delete(menuRoleQueryWrapper);
        int rows = 0;
        // 清空授权时 menuIds 为空，删除关联后即可视为保存成功。
        if (!StrUtil.isBlank(menuIds)) {
            // cascade 半选父节点不会出现在 checked-keys，保存前按 menu_path 补全祖先，避免子菜单丢目录壳。
            Set<String> menuIdSet = expandMenuIdsWithAncestors(new LinkedHashSet<>(StrUtil.splitTrim(menuIds, ',')));
            int validMenuCount = 0;

            for (String menuId : menuIdSet) {
                if (StrUtil.isBlank(menuId)) {
                    continue;
                }
                validMenuCount++;
                MenuRole menuRole = new MenuRole();
                menuRole.setRoleId(roleId);
                menuRole.setMenuId(menuId);
                rows = rows + menuRoleMapper.insert(menuRole);
            }
            if (validMenuCount > 0 && rows <= 0) {
                throw new BusinessException("授权角色菜单失败");
            }
        }
        // 授权保存后让在线用户下次鉴权重新加载角色与权限码。
        clearAuthCacheByRole(roleId);
    }

    /** 根据已选菜单的 menu_path 补全祖先目录/菜单 ID。 */
    private Set<String> expandMenuIdsWithAncestors(Set<String> menuIds) {
        if (menuIds.isEmpty()) {
            return menuIds;
        }
        Set<String> result = new LinkedHashSet<>(menuIds);
        List<Menu> menus = menuMapper.selectByIds(menuIds);
        for (Menu menu : menus) {
            if (menu == null || StrUtil.isBlank(menu.getMenuPath())) {
                continue;
            }
            for (String pathId : StrUtil.splitTrim(menu.getMenuPath(), ',')) {
                if (StrUtil.isNotBlank(pathId) && !"0".equals(pathId)) {
                    result.add(pathId);
                }
            }
        }
        return result;
    }

    /** 角色授权/启停变更后，清理持有该角色用户的角色与权限码缓存。 */
    public void clearAuthCacheByRole(String roleId) {
        List<RoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getRoleId, roleId));
        for (RoleUser roleUser : roleUsers) {
            SaSession session = StpUtil.getSessionByLoginId(roleUser.getUserId(), false);
            if (session != null) {
                session.delete(StpInterfaceImpl.ROLE_LIST);
                session.delete(StpInterfaceImpl.PERM_LIST);
            }
        }
    }

    /** 菜单启停后，清理绑定该菜单的所有角色对应用户的权限码缓存。 */
    private void clearAuthCacheByMenu(String menuId) {
        List<MenuRole> menuRoles = menuRoleMapper.selectList(
                new LambdaQueryWrapper<MenuRole>().eq(MenuRole::getMenuId, menuId));
        Set<String> roleIds = new LinkedHashSet<>();
        for (MenuRole menuRole : menuRoles) {
            if (StrUtil.isNotBlank(menuRole.getRoleId())) {
                roleIds.add(menuRole.getRoleId());
            }
        }
        for (String roleId : roleIds) {
            clearAuthCacheByRole(roleId);
        }
    }

    public List<Menu> selectList() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getOrderNum);
        return menuMapper.selectList(wrapper);
    }

    public List<MenuVo.MenuListVo> selectList(MenuDto.MenuList menuList) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if (menuList != null) {
            if (StrUtil.isNotBlank(menuList.menuName())) {
                wrapper.like(Menu::getMenuName, menuList.menuName());
            }
            if (StrUtil.isNotBlank(menuList.permCode())) {
                wrapper.like(Menu::getPermCode, menuList.permCode());
            }
            if (StrUtil.isNotBlank(menuList.routePath())) {
                wrapper.like(Menu::getRoutePath, menuList.routePath());
            }
        }
        wrapper.orderByAsc(Menu::getOrderNum);
        return menuMapper.selectList(wrapper).stream().map(MenuVo.MenuListVo::from).toList();
    }

    public Menu selectById(String menuId) {
        return menuMapper.selectById(menuId);
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的菜单");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        Long roleCount = menuRoleMapper.selectCount(new LambdaQueryWrapper<MenuRole>().in(MenuRole::getMenuId, idList));
        if (roleCount != null && roleCount > 0) {
            throw new BusinessException("菜单已分配给角色，不能删除");
        }
        Long childCount = menuMapper.selectCount(new LambdaQueryWrapper<Menu>().in(Menu::getParentId, idList));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("菜单包含下级节点，不能删除");
        }
        int rows = menuMapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("菜单不存在或删除失败");
        }
    }

    @Transactional
    public void insert(MenuDto.MenuInsert menuInsert) {
        // 菜单/按钮必须有权限标识；目录允许空标识。
        validatePermCodeByType(menuInsert.menuType(), menuInsert.permCode());
        // 同级菜单名称与权限标识不能重复。
        checkMenuUnique(menuInsert.parentId(), menuInsert.menuName(), menuInsert.permCode(), null);
        Menu menu = new Menu(menuInsert);
        String parentId = normalizeParentId(menu.getParentId());
        menu.setParentId(parentId);
        // 非根节点时先校验上级存在，便于 insert 后拼 path。
        if (!"0".equals(parentId)) {
            Menu parent = menuMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("上级菜单不存在");
            }
        }
        menu.setIsBlank(normalizeIsBlank(menu.getIsBlank()));
        menu.setMenuPath("");
        int rows = menuMapper.insert(menu);
        if (rows <= 0) {
            throw new BusinessException("新增菜单失败");
        }
        // menu_path 须以自身 menu_id 结尾，insert 拿到 ID 后再回写。
        fillMenuPath(menu);
        menuMapper.updateById(menu);
    }

    @Transactional
    public void updateById(MenuDto.MenuUpdate menuUpdate) {
        // 修改菜单校验层级关系，并约束同级名称与标识唯一。
        Menu oldMenu = menuMapper.selectById(menuUpdate.menuId());
        if (oldMenu == null) {
            throw new BusinessException("菜单不存在");
        }
        if (menuUpdate.parentId().equals(menuUpdate.menuId()) || isChildMenu(menuUpdate.menuId(), menuUpdate.parentId())) {
            throw new BusinessException("上级菜单不能选择自己或下级菜单");
        }
        validatePermCodeByType(menuUpdate.menuType(), menuUpdate.permCode());
        checkMenuUnique(menuUpdate.parentId(), menuUpdate.menuName(), menuUpdate.permCode(), menuUpdate.menuId());
        Menu menu = new Menu(menuUpdate);
        menu.setIsBlank(normalizeIsBlank(menu.getIsBlank()));
        fillMenuPath(menu);
        int rows = menuMapper.updateById(menu);
        if (rows <= 0) {
            throw new BusinessException("修改菜单失败");
        }
    }

    public Menu checkMenuNameUnique(String parentId, String menuName) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, normalizeParentId(parentId));
        wrapper.eq(Menu::getMenuName, menuName);
        return menuMapper.selectOne(wrapper);
    }

    public Menu checkMenuCodeUnique(String permCode) {
        if (StrUtil.isBlank(permCode)) {
            return null;
        }
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getPermCode, permCode);
        return menuMapper.selectOne(wrapper);
    }

    @Transactional
    public void updateMenuEnabled(MenuDto.UpdateEnabled updateEnabled) {
        Menu oldMenu = menuMapper.selectById(updateEnabled.menuId());
        if (oldMenu == null) {
            throw new BusinessException("菜单不存在");
        }
        oldMenu.setIsEnabled(updateEnabled.isEnabled());
        int rows = menuMapper.updateById(oldMenu);
        if (rows <= 0) {
            throw new BusinessException("修改菜单状态失败");
        }
        // 启停后失效在线用户已缓存的权限码（selectMenusByRoleIds 只返回启用菜单）。
        clearAuthCacheByMenu(updateEnabled.menuId());
    }

    private void checkMenuUnique(String parentId, String menuName, String permCode, String excludeMenuId) {
        Menu oldNameMenu = checkMenuNameUnique(parentId, menuName);
        if (oldNameMenu != null && (excludeMenuId == null || !oldNameMenu.getMenuId().equals(excludeMenuId))) {
            throw new BusinessException("同级菜单名称已存在");
        }
        Menu oldCodeMenu = checkMenuCodeUnique(permCode);
        if (oldCodeMenu != null && (excludeMenuId == null || !oldCodeMenu.getMenuId().equals(excludeMenuId))) {
            throw new BusinessException("权限标识已存在");
        }
    }

    /** 目录可空码；菜单与按钮必须有非空权限标识。 */
    private void validatePermCodeByType(Integer menuType, String permCode) {
        if (menuType != null && menuType != 0 && StrUtil.isBlank(permCode)) {
            throw new BusinessException("菜单或按钮权限标识不能为空");
        }
    }

    /**
     * menu_path 规则：须以自身 menu_id 结尾。
     * 根：0,{menuId}；子：{parent.menuPath},{menuId}
     */
    private void fillMenuPath(Menu menu) {
        String parentId = normalizeParentId(menu.getParentId());
        menu.setParentId(parentId);
        if ("0".equals(parentId)) {
            menu.setMenuPath("0," + menu.getMenuId());
            return;
        }
        Menu parent = menuMapper.selectById(parentId);
        if (parent == null) {
            throw new BusinessException("上级菜单不存在");
        }
        menu.setMenuPath(parent.getMenuPath() + "," + menu.getMenuId());
    }

    private boolean isChildMenu(String menuId, String parentId) {
        if (StrUtil.isBlank(parentId) || "0".equals(parentId)) {
            return false;
        }
        Menu parent = menuMapper.selectById(parentId);
        // path 以自身 id 结尾，用逗号包裹判断是否落在祖先链上。
        return parent != null && parent.getMenuPath() != null
                && ("," + parent.getMenuPath() + ",").contains("," + menuId + ",");
    }

    private String normalizeParentId(String parentId) {
        return StrUtil.isBlank(parentId) ? "0" : parentId;
    }

    private Integer normalizeIsBlank(Integer isBlank) {
        return isBlank != null && isBlank == 1 ? 1 : 0;
    }
}
