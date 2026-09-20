package com.ylmao.admin.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.common.SystemConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.entity.Menu;
import com.ylmao.admin.entity.Role;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.MenuMapper;
import com.ylmao.admin.mapper.RoleMapper;
import com.ylmao.admin.vo.AdminAuthVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Soybean 管理端认证：登录最小信息 + getUserInfo / 动态路由。 */
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private static final String DEFAULT_HOME = "home";
    /** menu_type=2 按钮只进 buttons；0/1 参与路由树。 */
    private static final int MENU_TYPE_BUTTON = 2;
    private static final int ICON_TYPE_ICONIFY = 1;
    private static final int ICON_TYPE_LOCAL = 2;

    private final StpInterface stpInterface;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final ConfigRuntimeService configRuntimeService;

    /** 免登录品牌引导：从启用态 system.* 配置组装展示快照。 */
    public AdminAuthVo.BrandingResult buildBranding() {
        return new AdminAuthVo.BrandingResult(
                configValue(SystemConfigCodes.NAME),
                configValue(SystemConfigCodes.SHORT_NAME),
                configValue(SystemConfigCodes.LOGO),
                configValue(SystemConfigCodes.FAVICON),
                configValue(SystemConfigCodes.COPYRIGHT),
                configValue(SystemConfigCodes.ADMIN_EMAIL),
                configValue(SystemConfigCodes.VERSION),
                configValue(SystemConfigCodes.WEBSITE),
                configValue(SystemConfigCodes.ICP),
                configValue(SystemConfigCodes.POLICE_ICP)
        );
    }

    private String configValue(String configCode) {
        return configRuntimeService.getString(configCode).orElse("");
    }

    /** 登录成功后组装：userId / userName / role_code 列表。 */
    public AdminAuthVo.LoginResult buildLoginResult(String token) {
        User user = requireCurrentUser();
        List<String> roleIds = currentRoleIds();
        return new AdminAuthVo.LoginResult(
                token,
                user.getUserId(),
                user.getUserName(),
                loadRoleCodes(roleIds)
        );
    }

    /** 刷新当前用户信息：角色码 + 按钮权限码。 */
    public AdminAuthVo.UserInfoResult buildUserInfo() {
        User user = requireCurrentUser();
        List<String> roleIds = currentRoleIds();
        List<Menu> menus = loadMenusByRoleIds(roleIds);
        return new AdminAuthVo.UserInfoResult(
                user.getUserId(),
                user.getUserName(),
                loadRoleCodes(roleIds),
                collectButtons(menus)
        );
    }

    /** 按角色从 sys_menu 组 Soybean 动态路由树。 */
    public AdminAuthVo.UserRouteResult buildUserRoutes() {
        List<String> roleIds = currentRoleIds();
        List<Menu> menus = ensureAncestorMenus(loadMenusByRoleIds(roleIds));
        List<AdminAuthVo.RouteItem> routes = buildRouteTree(menus);
        return new AdminAuthVo.UserRouteResult(routes, resolveHome(routes));
    }

    /** 当前用户授权菜单中是否存在该 route_name（动态 404 探测）。 */
    public boolean isRouteExist(String routeName) {
        if (StrUtil.isBlank(routeName)) {
            return false;
        }
        List<Menu> menus = loadMenusByRoleIds(currentRoleIds());
        for (Menu menu : menus) {
            Integer type = menu.getMenuType();
            if (type != null && type == MENU_TYPE_BUTTON) {
                continue;
            }
            if (routeName.equals(menu.getRouteName())) {
                return true;
            }
        }
        return false;
    }

    private User requireCurrentUser() {
        User user = SaTokenUtil.getUser();
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private List<String> currentRoleIds() {
        String loginId = StpUtil.getLoginIdAsString();
        return stpInterface.getRoleList(loginId, StpUtil.getLoginType());
    }

    private List<Menu> loadMenusByRoleIds(List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return List.of();
        }
        return menuMapper.selectMenusByRoleIds(roleIds);
    }

    /**
     * 补全缺失的祖先目录/菜单。cascade 半选导致库中可能只有子节点时，
     * 否则子路由会抬成顶级且缺少 layout.base，侧栏乱、点进去变单页。
     */
    private List<Menu> ensureAncestorMenus(List<Menu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return List.of();
        }
        Map<String, Menu> byId = new LinkedHashMap<>();
        for (Menu menu : menus) {
            byId.put(menu.getMenuId(), menu);
        }
        Set<String> missingIds = new LinkedHashSet<>();
        for (Menu menu : menus) {
            if (StrUtil.isBlank(menu.getMenuPath())) {
                continue;
            }
            for (String pathId : StrUtil.splitTrim(menu.getMenuPath(), ',')) {
                if (StrUtil.isNotBlank(pathId) && !"0".equals(pathId) && !byId.containsKey(pathId)) {
                    missingIds.add(pathId);
                }
            }
        }
        if (missingIds.isEmpty()) {
            return new ArrayList<>(byId.values());
        }
        for (Menu parent : menuMapper.selectByIds(missingIds)) {
            if (parent != null) {
                byId.putIfAbsent(parent.getMenuId(), parent);
            }
        }
        return new ArrayList<>(byId.values());
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

    private List<String> collectButtons(List<Menu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return List.of();
        }
        Set<String> buttons = new LinkedHashSet<>();
        for (Menu menu : menus) {
            if (menu.getMenuType() != null
                    && menu.getMenuType() == MENU_TYPE_BUTTON
                    && StrUtil.isNotBlank(menu.getPermCode())) {
                buttons.add(menu.getPermCode());
            }
        }
        return new ArrayList<>(buttons);
    }

    private List<AdminAuthVo.RouteItem> buildRouteTree(List<Menu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return List.of();
        }
        Map<String, Menu> routeMenus = new LinkedHashMap<>();
        for (Menu menu : menus) {
            Integer type = menu.getMenuType();
            if (type == null || type == MENU_TYPE_BUTTON) {
                continue;
            }
            if (StrUtil.isBlank(menu.getRouteName()) || StrUtil.isBlank(menu.getRoutePath())) {
                continue;
            }
            routeMenus.put(menu.getMenuId(), menu);
        }
        if (routeMenus.isEmpty()) {
            return List.of();
        }

        Map<String, List<String>> childrenIds = new LinkedHashMap<>();
        List<String> rootIds = new ArrayList<>();
        for (Menu menu : routeMenus.values()) {
            String parentId = StrUtil.blankToDefault(menu.getParentId(), "0");
            if ("0".equals(parentId) || !routeMenus.containsKey(parentId)) {
                rootIds.add(menu.getMenuId());
                continue;
            }
            childrenIds.computeIfAbsent(parentId, key -> new ArrayList<>()).add(menu.getMenuId());
        }

        List<AdminAuthVo.RouteItem> roots = new ArrayList<>();
        for (String rootId : rootIds) {
            roots.add(toRouteItem(routeMenus.get(rootId), routeMenus, childrenIds));
        }
        return roots;
    }

    private AdminAuthVo.RouteItem toRouteItem(
            Menu menu,
            Map<String, Menu> routeMenus,
            Map<String, List<String>> childrenIds
    ) {
        List<String> childIds = childrenIds.get(menu.getMenuId());
        List<AdminAuthVo.RouteItem> children = null;
        if (!CollectionUtils.isEmpty(childIds)) {
            children = new ArrayList<>();
            for (String childId : childIds) {
                children.add(toRouteItem(routeMenus.get(childId), routeMenus, childrenIds));
            }
        }
        return new AdminAuthVo.RouteItem(
                menu.getMenuId(),
                menu.getRouteName(),
                menu.getRoutePath(),
                menu.getRouteComp(),
                buildMeta(menu),
                children
        );
    }

    private Map<String, Object> buildMeta(Menu menu) {
        Map<String, Object> meta = new LinkedHashMap<>();
        // title 用 routeName，便于无 i18n 时仍可读；有 i18nKey 时前端优先 i18n
        meta.put("title", StrUtil.blankToDefault(menu.getRouteName(), menu.getMenuName()));
        if (StrUtil.isNotBlank(menu.getI18nKey())) {
            meta.put("i18nKey", menu.getI18nKey());
        }
        if (StrUtil.isNotBlank(menu.getMenuIcon())) {
            Integer iconType = menu.getIconType();
            if (iconType != null && iconType == ICON_TYPE_LOCAL) {
                meta.put("localIcon", menu.getMenuIcon());
            } else if (iconType == null || iconType == ICON_TYPE_ICONIFY) {
                meta.put("icon", menu.getMenuIcon());
            }
        }
        if (menu.getOrderNum() != null) {
            meta.put("order", menu.getOrderNum());
        }
        // is_show=0 表示不进侧栏，必须是布尔，避免 "0" 被当成 truthy
        if (menu.getIsShow() != null && menu.getIsShow() == 0) {
            meta.put("hideInMenu", true);
        }
        if (menu.getKeepAlive() != null && menu.getKeepAlive() == 1) {
            meta.put("keepAlive", true);
        }
        if (StrUtil.isNotBlank(menu.getActiveMenu())) {
            meta.put("activeMenu", menu.getActiveMenu());
        }
        if (StrUtil.isNotBlank(menu.getMenuHref())) {
            meta.put("href", menu.getMenuHref());
        }
        List<Map<String, String>> query = parseRouteQuery(menu.getRouteQuery());
        if (!CollectionUtils.isEmpty(query)) {
            meta.put("query", query);
        }
        return meta;
    }

    /** route_query 存 JSON 对象时转 Soybean meta.query 数组。 */
    private List<Map<String, String>> parseRouteQuery(String routeQuery) {
        if (StrUtil.isBlank(routeQuery)) {
            return List.of();
        }
        try {
            if (!JSONUtil.isTypeJSONObject(routeQuery)) {
                return List.of();
            }
            JSONObject obj = JSONUtil.parseObj(routeQuery);
            List<Map<String, String>> query = new ArrayList<>();
            for (String key : obj.keySet()) {
                Map<String, String> item = new LinkedHashMap<>(2);
                item.put("key", key);
                item.put("value", obj.getStr(key));
                query.add(item);
            }
            return query;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private String resolveHome(List<AdminAuthVo.RouteItem> routes) {
        if (containsRouteName(routes, DEFAULT_HOME)) {
            return DEFAULT_HOME;
        }
        String firstLeaf = findFirstLeafName(routes);
        return StrUtil.blankToDefault(firstLeaf, DEFAULT_HOME);
    }

    private boolean containsRouteName(List<AdminAuthVo.RouteItem> routes, String name) {
        if (CollectionUtils.isEmpty(routes)) {
            return false;
        }
        for (AdminAuthVo.RouteItem route : routes) {
            if (name.equals(route.name())) {
                return true;
            }
            if (containsRouteName(route.children(), name)) {
                return true;
            }
        }
        return false;
    }

    private String findFirstLeafName(List<AdminAuthVo.RouteItem> routes) {
        if (CollectionUtils.isEmpty(routes)) {
            return null;
        }
        for (AdminAuthVo.RouteItem route : routes) {
            if (CollectionUtils.isEmpty(route.children())) {
                return route.name();
            }
            String child = findFirstLeafName(route.children());
            if (StrUtil.isNotBlank(child)) {
                return child;
            }
        }
        return null;
    }
}
