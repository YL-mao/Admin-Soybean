import type { Router } from 'vue-router';
import { applyDocumentTitle, pageTitleFromRoute } from '@/utils/document-title';

export function createDocumentTitleGuard(router: Router) {
  router.afterEach(to => {
    applyDocumentTitle(pageTitleFromRoute(to));
  });
}
