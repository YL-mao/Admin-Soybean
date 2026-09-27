package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.entity.RoleMenu;
import com.ylmao.admin.mapper.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色-菜单关系表：只访问 {@link RoleMenuMapper}，不回调 Menu/Role 等业务 Service。
 */
@Service
@RequiredArgsConstructor
public class RoleMenuService {

    private final RoleMenuMapper roleMenuMapper;

    /**
     * 以提交集合为准重建某角色的菜单绑定：先清空再插入；空集合表示清空授权。
     * menuIds 内保序去重。
     */
    @Transactional
    public void replaceRoleMenus(String roleId, Collection<String> menuIds) {
        if (StrUtil.isBlank(roleId)) {
            throw new BusinessException("角色ID不能为空");
        }
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }
        Set<String> uniqueMenuIds = new LinkedHashSet<>();
        for (String menuId : menuIds) {
            if (StrUtil.isNotBlank(menuId)) {
                uniqueMenuIds.add(menuId.trim());
            }
        }
        int rows = 0;
        for (String menuId : uniqueMenuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            rows = rows + roleMenuMapper.insert(roleMenu);
        }
        if (!uniqueMenuIds.isEmpty() && rows <= 0) {
            throw new BusinessException("授权角色菜单失败");
        }
    }

    /** 绑定了该菜单的角色 ID（菜单启停后清权限缓存等）。 */
    public List<String> listRoleIdsByMenuId(String menuId) {
        if (StrUtil.isBlank(menuId)) {
            return List.of();
        }
        return roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, menuId))
                .stream()
                .map(RoleMenu::getRoleId)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }

    /** 是否有角色绑定了给定菜单（删菜单前守卫）。 */
    public long countByMenuIds(Collection<String> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return 0L;
        }
        Long count = roleMenuMapper.selectCount(new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getMenuId, menuIds));
        return count == null ? 0L : count;
    }

    /** 删除角色后清理其菜单授权。 */
    @Transactional
    public void deleteByRoleIds(Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        List<String> idList = new ArrayList<>();
        for (String roleId : roleIds) {
            if (StrUtil.isNotBlank(roleId)) {
                idList.add(roleId);
            }
        }
        if (idList.isEmpty()) {
            return;
        }
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getRoleId, idList));
    }
}
