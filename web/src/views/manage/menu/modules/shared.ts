const LAYOUT_PREFIX = 'layout.';
const VIEW_PREFIX = 'view.';
const FIRST_LEVEL_ROUTE_COMPONENT_SPLIT = '$';

export function getLayoutAndPage(component?: string | null) {
  let layout = '';
  let page = '';

  const [layoutOrPage = '', pageItem = ''] = component?.split(FIRST_LEVEL_ROUTE_COMPONENT_SPLIT) || [];

  layout = getLayout(layoutOrPage);
  page = getPage(pageItem || layoutOrPage);

  return { layout, page };
}

function getLayout(layout: string) {
  return layout.startsWith(LAYOUT_PREFIX) ? layout.replace(LAYOUT_PREFIX, '') : '';
}

function getPage(page: string) {
  return page.startsWith(VIEW_PREFIX) ? page.replace(VIEW_PREFIX, '') : '';
}

export function transformLayoutAndPageToComponent(layout: string, page: string) {
  const hasLayout = Boolean(layout);
  const hasPage = Boolean(page);

  if (hasLayout && hasPage) {
    return `${LAYOUT_PREFIX}${layout}${FIRST_LEVEL_ROUTE_COMPONENT_SPLIT}${VIEW_PREFIX}${page}`;
  }

  if (hasLayout) {
    return `${LAYOUT_PREFIX}${layout}`;
  }

  if (hasPage) {
    return `${VIEW_PREFIX}${page}`;
  }

  return '';
}

/**
 * Get route name by route path
 *
 * @param routeName
 */
export function getRoutePathByRouteName(routeName: string) {
  return `/${routeName.replace(/_/g, '/')}`;
}

/** 平铺菜单按 parentId 组树 */
export function buildMenuTree(list: Api.SystemManage.Menu[]): Api.SystemManage.Menu[] {
  const map = new Map<string, Api.SystemManage.Menu>();

  list.forEach(item => {
    map.set(item.menuId, { ...item, children: [] });
  });

  const roots: Api.SystemManage.Menu[] = [];

  map.forEach(node => {
    const parentId = node.parentId || '0';
    if (parentId === '0' || !map.has(parentId)) {
      roots.push(node);
      return;
    }
    const parent = map.get(parentId)!;
    parent.children = parent.children || [];
    parent.children.push(node);
  });

  pruneEmptyChildren(roots);
  return roots;
}

/** 上级菜单平铺组树（下拉用） */
export function buildMenuParentTree(list: Api.SystemManage.MenuParent[]): Api.SystemManage.MenuParent[] {
  const map = new Map<string, Api.SystemManage.MenuParent>();

  list.forEach(item => {
    map.set(item.menuId, { ...item, children: [] });
  });

  const roots: Api.SystemManage.MenuParent[] = [];

  map.forEach(node => {
    const parentId = node.parentId || '0';
    // 顶级占位 parentId=-1
    if (parentId === '0' || parentId === '-1' || !map.has(parentId)) {
      roots.push(node);
      return;
    }
    const parent = map.get(parentId)!;
    parent.children = parent.children || [];
    parent.children.push(node);
  });

  pruneEmptyParentChildren(roots);
  return roots;
}

function pruneEmptyChildren(nodes: Api.SystemManage.Menu[]) {
  nodes.forEach(node => {
    if (node.children?.length) {
      pruneEmptyChildren(node.children);
    } else {
      node.children = null;
    }
  });
}

function pruneEmptyParentChildren(nodes: Api.SystemManage.MenuParent[]) {
  nodes.forEach(node => {
    if (node.children?.length) {
      pruneEmptyParentChildren(node.children);
    } else {
      delete node.children;
    }
  });
}

/** 收集树中已使用的 Iconify 图标名（含 `:` 或 iconType=1） */
export function collectMenuIconifyIcons(nodes: Api.SystemManage.Menu[]): string[] {
  const set = new Set<string>();

  const walk = (list: Api.SystemManage.Menu[]) => {
    list.forEach(node => {
      const icon = node.menuIcon?.trim();
      if (icon && (node.iconType === 1 || icon.includes(':'))) {
        set.add(icon);
      }
      if (node.children?.length) {
        walk(node.children);
      }
    });
  };

  walk(nodes);
  return [...set].sort();
}
