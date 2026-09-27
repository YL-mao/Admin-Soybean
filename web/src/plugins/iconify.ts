import { addCollection } from '@iconify/vue/offline';
import mdiIcons from '@iconify-json/mdi/icons.json';

/**
 * Iconify 离线：用 offline 入口注册本地 MDI 全集，
 * 运行时不再请求 api.iconify.design（含缺图标也不会出网）。
 */
export function setupIconifyOffline() {
  addCollection(mdiIcons);
}
