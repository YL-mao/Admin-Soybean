import mdiIcons from '@iconify-json/mdi/icons.json';

export function getLocalIcons() {
  const svgIcons = import.meta.glob('/src/assets/svg-icon/*.svg');

  const keys = Object.keys(svgIcons)
    .map(item => item.split('/').at(-1)?.replace('.svg', '') || '')
    .filter(Boolean);

  return keys;
}

/** 本地 MDI 全名（含别名），供菜单图标检索；与 offline addCollection 同源 */
let cachedMdiIconNames: string[] | null = null;

export function getMdiIconNames() {
  if (!cachedMdiIconNames) {
    const ids = new Set<string>(Object.keys(mdiIcons.icons));
    if (mdiIcons.aliases) {
      for (const alias of Object.keys(mdiIcons.aliases)) {
        ids.add(alias);
      }
    }
    cachedMdiIconNames = [...ids].map(id => `mdi:${id}`);
  }
  return cachedMdiIconNames;
}

/** 是否为本地已注册的 mdi 图标（下拉选中值校验） */
export function isLocalMdiIcon(name: string | null | undefined) {
  if (!name || !name.startsWith('mdi:')) {
    return false;
  }
  const id = name.slice(4);
  return Object.hasOwn(mdiIcons.icons, id) || Object.hasOwn(mdiIcons.aliases ?? {}, id);
}
