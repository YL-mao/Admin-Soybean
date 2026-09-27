import { addAPIProvider, addCollection } from '@iconify/vue';
import mdiIcons from '@iconify-json/mdi/icons.json';

/**
 * Iconify 离线：注册本地 MDI 全集（@iconify-json/mdi），
 * `mdi:*` 不再请求 api.iconify.design。
 * VITE_ICONIFY_URL 仍可作为其它图标集或自建 API 的备用入口。
 */
export function setupIconifyOffline() {
  addCollection(mdiIcons);

  const { VITE_ICONIFY_URL } = import.meta.env;

  if (VITE_ICONIFY_URL) {
    addAPIProvider('', { resources: [VITE_ICONIFY_URL] });
  }
}
