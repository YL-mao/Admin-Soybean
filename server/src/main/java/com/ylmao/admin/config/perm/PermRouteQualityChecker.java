package com.ylmao.admin.config.perm;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.entity.Menu;
import com.ylmao.admin.mapper.MenuMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 启动时菜单权限与路由质量检查：只打 WARN，不拦截启动。
 * 可通过 app.perm.quality-check=false 关闭。
 */
@Component
@ConditionalOnProperty(name = "app.perm.quality-check", havingValue = "true", matchIfMissing = true)
public class PermRouteQualityChecker implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(PermRouteQualityChecker.class);

    /** 配置分组动态权限前缀，对应 ConfigController.checkGroupPermission。 */
    private static final Set<String> DYNAMIC_CONFIG_PERM_CODES = Set.of(
            "system:config:system",
            "system:config:upload",
            "system:config:log",
            "system:config:security",
            "system:config:job",
            "system:config:notice"
    );

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final MenuMapper menuMapper;

    public PermRouteQualityChecker(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping requestMappingHandlerMapping,
            MenuMapper menuMapper) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.menuMapper = menuMapper;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            Set<String> routePaths = collectRoutePaths();
            List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<>());
            checkMenuHrefs(menus, routePaths);
            checkAnnotationPermCodes(menus);
            checkDuplicatePermCodes(menus);
            checkBlankPermCodes(menus);
        } catch (Exception e) {
            // 质检失败本身不阻断启动，只记录原因。
            log.warn("[权限路由质检] 执行异常: {}", e.getMessage(), e);
        }
    }

    /** 菜单表非空 menu_href 对照已注册路由；is_blank=1 跳过。 */
    private void checkMenuHrefs(List<Menu> menus, Set<String> routePaths) {
        for (Menu menu : menus) {
            if (menu.getIsBlank() != null && menu.getIsBlank() == 1) {
                continue;
            }
            String url = StrUtil.trim(menu.getMenuHref());
            if (StrUtil.isBlank(url)) {
                continue;
            }
            String normalized = normalizePath(url);
            if (!routePaths.contains(normalized)) {
                log.warn("[权限路由质检] menu_href 无对应路由: menuId={}, menuName={}, menuHref={}",
                        menu.getMenuId(), menu.getMenuName(), url);
            }
        }
    }

    /** 仅报警：注解有权限码，库中按钮行无对应非空 perm_code。 */
    private void checkAnnotationPermCodes(List<Menu> menus) {
        Set<String> dbCodes = new HashSet<>();
        for (Menu menu : menus) {
            // 权限码只认按钮；目录/页面上的码视为脏数据，不计入「已存在」。
            if (menu.getMenuType() != null && menu.getMenuType() == 2 && StrUtil.isNotBlank(menu.getPermCode())) {
                dbCodes.add(menu.getPermCode().trim());
            }
        }
        Set<String> annotationCodes = new LinkedHashSet<>();
        for (HandlerMethod handlerMethod : requestMappingHandlerMapping.getHandlerMethods().values()) {
            SaCheckPermission ann = AnnotatedElementUtils.findMergedAnnotation(
                    handlerMethod.getMethod(), SaCheckPermission.class);
            if (ann == null) {
                continue;
            }
            for (String code : ann.value()) {
                if (StrUtil.isNotBlank(code)) {
                    annotationCodes.add(code.trim());
                }
            }
        }
        annotationCodes.addAll(DYNAMIC_CONFIG_PERM_CODES);
        for (String code : annotationCodes) {
            if (!dbCodes.contains(code)) {
                log.warn("[权限路由质检] 注解权限码在库中不存在: permCode={}", code);
            }
        }
    }

    private void checkDuplicatePermCodes(List<Menu> menus) {
        Map<String, List<String>> codeToIds = new HashMap<>();
        for (Menu menu : menus) {
            if (StrUtil.isBlank(menu.getPermCode())) {
                continue;
            }
            String code = menu.getPermCode().trim();
            codeToIds.computeIfAbsent(code, key -> new ArrayList<>()).add(menu.getMenuId());
        }
        for (Map.Entry<String, List<String>> entry : codeToIds.entrySet()) {
            if (entry.getValue().size() > 1) {
                log.warn("[权限路由质检] 权限码重复: permCode={}, menuIds={}",
                        entry.getKey(), entry.getValue());
            }
        }
    }

    /** 目录/页面必须空码；按钮必须非空。页面非空或按钮空码均 WARN。 */
    private void checkBlankPermCodes(List<Menu> menus) {
        for (Menu menu : menus) {
            Integer type = menu.getMenuType();
            if (type == null) {
                continue;
            }
            boolean blank = StrUtil.isBlank(menu.getPermCode());
            if (type == 2) {
                if (blank) {
                    log.warn("[权限路由质检] 按钮权限码为空: menuId={}, menuName={}",
                            menu.getMenuId(), menu.getMenuName());
                }
                continue;
            }
            // 目录(0)、页面(1)：权限码应为空，进路由靠角色勾选菜单。
            if (!blank) {
                log.warn("[权限路由质检] 目录/页面不应配置权限码: menuId={}, menuName={}, menuType={}, permCode={}",
                        menu.getMenuId(), menu.getMenuName(), type, menu.getPermCode());
            }
        }
    }

    private Set<String> collectRoutePaths() {
        Set<String> paths = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry
                : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            // Spring 6+ 默认 PathPattern；PatternsRequestCondition 在 7.0 已弃用。
            if (info.getPathPatternsCondition() != null) {
                addPaths(paths, info.getPathPatternsCondition().getPatternValues());
            }
        }
        return paths;
    }

    private void addPaths(Set<String> paths, Collection<String> patterns) {
        if (patterns == null) {
            return;
        }
        for (String pattern : patterns) {
            paths.add(normalizePath(pattern));
        }
    }

    private static String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
