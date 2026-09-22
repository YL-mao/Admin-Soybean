import { getAuthorization } from '@/service/request/shared';
import { getDeviceId, DEVICE_ID_HEADER } from '@/utils/device-id';
import { resolveBackendAssetUrl } from '@/utils/service';
import type { TreeOption, TreeSelectOption } from 'naive-ui';

/** 虚拟根目录：列表不按目录过滤；上传落到未分类 */
export const FILE_ROOT_FOLDER_ID = '0';

/** 内置未分类目录（与 UploadConfigCodes.UNCLASSIFIED_FOLDER_ID 一致） */
export const FILE_UNCLASSIFIED_FOLDER_ID = '1229000000000000001';

/** 平铺目录组树 */
export function buildFolderOptionTree(list: Api.SystemManage.FolderOption[]): Api.SystemManage.FolderOption[] {
  const map = new Map<string, Api.SystemManage.FolderOption>();
  list.forEach(item => {
    map.set(item.folderId, { ...item, children: [] });
  });

  const roots: Api.SystemManage.FolderOption[] = [];
  map.forEach(node => {
    const parentId = node.parentId || FILE_ROOT_FOLDER_ID;
    if (parentId === FILE_ROOT_FOLDER_ID || !map.has(parentId)) {
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

function pruneEmptyChildren(nodes: Api.SystemManage.FolderOption[]) {
  nodes.forEach(node => {
    if (node.children?.length) {
      pruneEmptyChildren(node.children);
    } else {
      delete node.children;
    }
  });
}

/** 左侧 NTree 数据：虚拟根 + 真实目录 */
export function mapFolderTreeOptions(list: Api.SystemManage.FolderOption[], rootLabel: string): TreeOption[] {
  const tree = buildFolderOptionTree(list);
  return [
    {
      key: FILE_ROOT_FOLDER_ID,
      label: rootLabel,
      isLeaf: false,
      children: tree.map(mapTreeNode)
    }
  ];
}

function mapTreeNode(node: Api.SystemManage.FolderOption): TreeOption {
  return {
    key: node.folderId,
    label: node.folderName,
    isBuiltin: node.isBuiltin,
    isLeaf: !node.children?.length,
    children: node.children?.length ? node.children.map(mapTreeNode) : undefined
  };
}

/** 上级目录下拉（可排除某节点及其子孙，编辑时用） */
export function mapFolderParentSelectOptions(
  list: Api.SystemManage.FolderOption[],
  rootLabel: string,
  excludeFolderId?: string | null
): TreeSelectOption[] {
  const exclude = new Set<string>();
  if (excludeFolderId) {
    collectSelfAndDescendants(list, excludeFolderId, exclude);
  }
  const filtered = list.filter(item => !exclude.has(item.folderId));
  const tree = buildFolderOptionTree(filtered);
  return [
    {
      key: FILE_ROOT_FOLDER_ID,
      label: rootLabel,
      children: tree.map(mapSelectNode)
    }
  ];
}

function mapSelectNode(node: Api.SystemManage.FolderOption): TreeSelectOption {
  return {
    key: node.folderId,
    label: node.folderName,
    children: node.children?.length ? node.children.map(mapSelectNode) : undefined
  };
}

function collectSelfAndDescendants(list: Api.SystemManage.FolderOption[], folderId: string, out: Set<string>) {
  out.add(folderId);
  list.forEach(item => {
    if (item.parentId === folderId) {
      collectSelfAndDescendants(list, item.folderId, out);
    }
  });
}

export function formatFileSize(bytes?: number | null) {
  const size = Number(bytes || 0);
  if (size < 1024) return `${size} B`;
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
  if (size < 1024 * 1024 * 1024) return `${(size / 1024 / 1024).toFixed(1)} MB`;
  return `${(size / 1024 / 1024 / 1024).toFixed(1)} GB`;
}

/** 带登录头拉取预览/下载流，返回可释放的 blob URL */
export async function fetchFileBlobUrl(accessUrl: string) {
  const url = resolveBackendAssetUrl(accessUrl);
  const headers: Record<string, string> = {
    [DEVICE_ID_HEADER]: getDeviceId()
  };
  const token = getAuthorization();
  if (token) headers.saToken = token;

  const response = await fetch(url, { headers, credentials: 'include' });
  if (!response.ok) {
    throw new Error(`preview failed: ${response.status}`);
  }
  const blob = await response.blob();
  return URL.createObjectURL(blob);
}
