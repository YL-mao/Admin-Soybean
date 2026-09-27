import type { RouteLocationNormalizedLoaded } from 'vue-router';
import { $t } from '@/locales';
import { useBrandingStore } from '@/store/modules/branding';

/** 从路由 meta 解析页面标题（i18nKey 优先） */
export function pageTitleFromRoute(route: Pick<RouteLocationNormalizedLoaded, 'meta'>) {
  const { i18nKey, title } = route.meta;
  return i18nKey ? $t(i18nKey) : title;
}

/** 拼装并写入文档标题：`页面标题 - 系统名`（直接写 document.title，避免反复 useTitle 泄漏监听） */
export function applyDocumentTitle(pageTitle?: string | null) {
  if (typeof document === 'undefined') return;
  const appName = useBrandingStore().displayTitle;
  document.title = pageTitle ? `${pageTitle} - ${appName}` : appName;
}
