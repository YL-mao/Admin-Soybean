import { useTitle } from '@vueuse/core';
import type { RouteLocationNormalizedLoaded } from 'vue-router';
import { $t } from '@/locales';
import { useBrandingStore } from '@/store/modules/branding';

/** 从路由 meta 解析页面标题（i18nKey 优先） */
export function pageTitleFromRoute(route: Pick<RouteLocationNormalizedLoaded, 'meta'>) {
  const { i18nKey, title } = route.meta;
  return i18nKey ? $t(i18nKey) : title;
}

/** 拼装并写入文档标题：`页面标题 - 系统名`（与路由守卫、语言切换、品牌刷新共用） */
export function applyDocumentTitle(pageTitle?: string | null) {
  const appName = useBrandingStore().displayTitle;
  useTitle(pageTitle ? `${pageTitle} - ${appName}` : appName);
}
