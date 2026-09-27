import { listIcons } from '@iconify/vue';

export function getLocalIcons() {
  const svgIcons = import.meta.glob('/src/assets/svg-icon/*.svg');

  const keys = Object.keys(svgIcons)
    .map(item => item.split('/').at(-1)?.replace('.svg', '') || '')
    .filter(Boolean);

  return keys;
}

/** 本地 addCollection 后的 MDI 全名（含别名），按需缓存 */
let cachedMdiIconNames: string[] | null = null;

export function getMdiIconNames() {
  if (!cachedMdiIconNames) {
    cachedMdiIconNames = listIcons('', 'mdi');
  }
  return cachedMdiIconNames;
}
