import type { RouteMeta } from 'vue-router';
import ElegantVueRouter from '@elegant-router/vue/vite';
import type { RouteKey } from '@elegant-router/types';

/** 与 example 分支一致的菜单 Iconify 图标（侧栏二级路由） */
const ROUTE_MENU_ICONS: Partial<Record<RouteKey, string>> = {
  home: 'mdi:monitor-dashboard',
  manage: 'mdi:cloud-cog-outline',
  manage_menu: 'mdi:routes',
  manage_role: 'mdi:account-badge-outline',
  manage_user: 'mdi:account-cog-outline',
  org: 'mdi:account-group-outline',
  org_dept: 'mdi:sitemap-outline',
  org_post: 'mdi:briefcase-outline',
  setting: 'mdi:cog-outline',
  setting_config: 'mdi:tune-variant',
  setting_dict: 'mdi:book-alphabet',
  setting_file: 'mdi:file-multiple-outline',
  setting_notice: 'mdi:bullhorn-outline',
  'setting_notice-console': 'mdi:chart-box-outline',
  ops: 'mdi:shield-lock-outline',
  ops_filter: 'mdi:filter-outline',
  ops_job: 'mdi:clock-outline',
  'ops_job-log': 'mdi:text-box-outline',
  ops_online: 'mdi:account-check-outline',
  'ops_operate-log': 'mdi:history',
  dev: 'mdi:tools',
  dev_apidoc: 'mdi:api',
  account_info: 'mdi:account',
  account_notice: 'mdi:bell-outline'
};

export function setupElegantRouter() {
  return ElegantVueRouter({
    layouts: {
      base: 'src/layouts/base-layout/index.vue',
      blank: 'src/layouts/blank-layout/index.vue'
    },
    routePathTransformer(routeName, routePath) {
      const key = routeName as RouteKey;

      if (key === 'login') {
        const modules: UnionKey.LoginModule[] = ['pwd-login'];

        const moduleReg = modules.join('|');

        return `/login/:module(${moduleReg})?`;
      }

      return routePath;
    },
    onRouteMetaGen(routeName) {
      const key = routeName as RouteKey;

      const constantRoutes: RouteKey[] = ['login', '403', '404', '500'];

      const meta: Partial<RouteMeta> = {
        title: key,
        i18nKey: `route.${key}` as App.I18n.I18nKey
      };

      const icon = ROUTE_MENU_ICONS[key];
      if (icon) {
        meta.icon = icon;
      }

      if (constantRoutes.includes(key)) {
        meta.constant = true;
      }

      return meta;
    }
  });
}
