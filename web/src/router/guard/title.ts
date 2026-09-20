import type { Router } from 'vue-router';
import { useTitle } from '@vueuse/core';
import { $t } from '@/locales';
import { useBrandingStore } from '@/store/modules/branding';

export function createDocumentTitleGuard(router: Router) {
  router.afterEach(to => {
    const { i18nKey, title } = to.meta;
    const pageTitle = i18nKey ? $t(i18nKey) : title;
    const appName = useBrandingStore().displayTitle;
    // 路由标题 + 系统名称，便于标签页辨认
    const documentTitle = pageTitle ? `${pageTitle} - ${appName}` : appName;

    useTitle(documentTitle);
  });
}
