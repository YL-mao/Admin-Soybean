/** 平铺部门按 parentId 组树 */
export function buildDeptTree(list: Api.SystemManage.Dept[]): Api.SystemManage.Dept[] {
  const map = new Map<string, Api.SystemManage.Dept>();

  list.forEach(item => {
    map.set(item.deptId, { ...item, children: [] });
  });

  const roots: Api.SystemManage.Dept[] = [];

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

/** 上级/下拉部门平铺组树 */
export function buildDeptOptionTree(list: Api.SystemManage.DeptOption[]): Api.SystemManage.DeptOption[] {
  const map = new Map<string, Api.SystemManage.DeptOption>();

  list.forEach(item => {
    map.set(item.deptId, { ...item, children: [] });
  });

  const roots: Api.SystemManage.DeptOption[] = [];

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

  pruneEmptyOptionChildren(roots);
  return roots;
}

function pruneEmptyChildren(nodes: Api.SystemManage.Dept[]) {
  nodes.forEach(node => {
    if (node.children?.length) {
      pruneEmptyChildren(node.children);
    } else {
      node.children = undefined;
    }
  });
}

function pruneEmptyOptionChildren(nodes: Api.SystemManage.DeptOption[]) {
  nodes.forEach(node => {
    if (node.children?.length) {
      pruneEmptyOptionChildren(node.children);
    } else {
      delete node.children;
    }
  });
}
