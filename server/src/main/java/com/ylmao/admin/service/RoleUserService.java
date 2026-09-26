package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.entity.RoleUser;
import com.ylmao.admin.mapper.RoleUserMapper;
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
 * 用户-角色关系表：只访问 {@link RoleUserMapper}，不回调 User/Role 等业务 Service。
 */
@Service
@RequiredArgsConstructor
public class RoleUserService {

    private final RoleUserMapper roleUserMapper;

    /** 某用户已绑定的角色 ID（保序去重）。 */
    public List<String> listRoleIdsByUserId(String userId) {
        if (StrUtil.isBlank(userId)) {
            return List.of();
        }
        return roleUserMapper.selectList(new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getUserId, userId))
                .stream()
                .map(RoleUser::getRoleId)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }

    /** 批量用户的关系行（列表组装角色名等）。 */
    public List<RoleUser> listByUserIds(Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return List.of();
        }
        return roleUserMapper.selectList(new LambdaQueryWrapper<RoleUser>().in(RoleUser::getUserId, userIds));
    }

    /** 某角色下绑定的用户 ID（清 Session 缓存等）。 */
    public List<String> listUserIdsByRoleId(String roleId) {
        if (StrUtil.isBlank(roleId)) {
            return List.of();
        }
        return roleUserMapper.selectList(new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getRoleId, roleId))
                .stream()
                .map(RoleUser::getUserId)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }

    /** 多角色下绑定的用户 ID 并集（公告按角色投递等）。 */
    public List<String> listUserIdsByRoleIds(Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return List.of();
        }
        return roleUserMapper.selectList(new LambdaQueryWrapper<RoleUser>().in(RoleUser::getRoleId, roleIds))
                .stream()
                .map(RoleUser::getUserId)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }

    /** 是否有用户绑定了给定角色（删角色前守卫）。 */
    public long countByRoleIds(Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0L;
        }
        Long count = roleUserMapper.selectCount(new LambdaQueryWrapper<RoleUser>().in(RoleUser::getRoleId, roleIds));
        return count == null ? 0L : count;
    }

    /**
     * 以提交集合为准重建某用户的角色绑定：先清空再插入；空串表示清空全部角色。
     * roleIdsCsv 内保序去重。
     */
    @Transactional
    public void replaceUserRoles(String userId, String roleIdsCsv) {
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户ID不能为空");
        }
        roleUserMapper.delete(new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getUserId, userId));
        if (StrUtil.isBlank(roleIdsCsv)) {
            return;
        }
        Set<String> uniqueRoleIds = new LinkedHashSet<>(StrUtil.splitTrim(roleIdsCsv, ','));
        for (String roleId : uniqueRoleIds) {
            if (StrUtil.isBlank(roleId)) {
                continue;
            }
            RoleUser roleUser = new RoleUser();
            roleUser.setUserId(userId);
            roleUser.setRoleId(roleId);
            int rows = roleUserMapper.insert(roleUser);
            if (rows <= 0) {
                throw new BusinessException("用户角色保存失败");
            }
        }
    }

    /** 删除用户后清理其角色关系。 */
    @Transactional
    public void deleteByUserIds(Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<String> idList = new ArrayList<>();
        for (String userId : userIds) {
            if (StrUtil.isNotBlank(userId)) {
                idList.add(userId);
            }
        }
        if (idList.isEmpty()) {
            return;
        }
        roleUserMapper.delete(new LambdaQueryWrapper<RoleUser>().in(RoleUser::getUserId, idList));
    }
}
