package com.ylmao.admin.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.entity.Role;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.RoleMapper;
import com.ylmao.admin.vo.AdminAuthVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Soybean 管理端认证辅助：仅组装登录态最小用户信息，不碰 sys_perm。 */
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final StpInterface stpInterface;
    private final RoleMapper roleMapper;

    /** 登录成功后组装：userId / userName / role_code 列表。 */
    public AdminAuthVo.LoginResult buildLoginResult(String token) {
        User user = SaTokenUtil.getUser();
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String loginId = StpUtil.getLoginIdAsString();
        List<String> roleIds = stpInterface.getRoleList(loginId, StpUtil.getLoginType());
        return new AdminAuthVo.LoginResult(
                token,
                user.getUserId(),
                user.getUserName(),
                loadRoleCodes(roleIds)
        );
    }

    private List<String> loadRoleCodes(List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return List.of();
        }
        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .in(Role::getRoleId, roleIds)
                .eq(Role::getIsEnabled, 1));
        if (CollectionUtils.isEmpty(roles)) {
            return List.of();
        }
        Set<String> roleCodes = new LinkedHashSet<>();
        for (Role role : roles) {
            if (StrUtil.isNotBlank(role.getRoleCode())) {
                roleCodes.add(role.getRoleCode());
            }
        }
        return new ArrayList<>(roleCodes);
    }
}
