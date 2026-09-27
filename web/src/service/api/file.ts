import { request } from '../request';

/** 目录树（平铺，前端组树） */
export function fetchGetFolderTree() {
  return request<Api.SystemManage.FolderOption[]>({
    url: '/api/admin/folder/tree',
    method: 'get'
  });
}

/** 新增目录 */
export function fetchCreateFolder(data: Api.SystemManage.FolderInsert) {
  return request<null>({
    url: '/api/admin/folder/add',
    method: 'post',
    data
  });
}

/** 修改目录 */
export function fetchUpdateFolder(data: Api.SystemManage.FolderUpdate) {
  return request<null>({
    url: '/api/admin/folder/update',
    method: 'put',
    data
  });
}

/** 删除目录（级联软删） */
export function fetchDeleteFolder(ids: string) {
  return request<null>({
    url: '/api/admin/folder/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 文件分页（query：page/limit + folderId/originalName；不传 folderId 表示全部） */
export function fetchGetFileList(params?: Api.SystemManage.FileSearchParams) {
  const { current, size, ...rest } = params || {};
  const query: Record<string, unknown> = {
    ...rest,
    page: current,
    limit: size
  };
  // 虚拟根「0」不传 folderId，后端按全部文件查询
  if (!query.folderId || query.folderId === '0') {
    delete query.folderId;
  }
  return request<Api.SystemManage.FileResource[]>({
    url: '/api/admin/file/list',
    method: 'get',
    params: query
  });
}

/** 上传规则（后缀/大小）；禁缓存，避免关开关后命中旧成功响应误开抽屉 */
export function fetchGetFileUploadRules() {
  return request<Api.SystemManage.FileUploadRules>({
    url: '/api/admin/file/uploadRules',
    method: 'get',
    headers: {
      'Cache-Control': 'no-cache',
      Pragma: 'no-cache'
    }
  });
}

/** 同目录文件名是否可用 */
/** 上传文件（multipart）；永远新建，同名可并存 */
export function fetchUploadFile(file: File, data: Api.SystemManage.FileUploadParams) {
  const formData = new FormData();
  formData.append('file', file);
  if (data.folderId != null && data.folderId !== '') {
    formData.append('folderId', data.folderId);
  }
  formData.append('fileScene', data.fileScene);
  if (data.needLogin != null) {
    formData.append('needLogin', String(data.needLogin));
  }
  return request<Api.SystemManage.FileResource>({
    url: '/api/admin/file/upload',
    method: 'post',
    data: formData
  });
}

/** 覆盖上传 */
export function fetchOverwriteFile(fileId: string, file: File) {
  const formData = new FormData();
  formData.append('fileId', fileId);
  formData.append('file', file);
  return request<Api.SystemManage.FileResource>({
    url: '/api/admin/file/overwrite',
    method: 'post',
    data: formData
  });
}

/** 修改文件元数据（本期不改目录，仍需带上当前 folderId） */
export function fetchUpdateFile(data: Api.SystemManage.FileUpdate) {
  return request<null>({
    url: '/api/admin/file/update',
    method: 'put',
    data
  });
}

/** 删除文件 */
export function fetchDeleteFile(ids: string) {
  return request<null>({
    url: '/api/admin/file/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 删除前引用检测 */
export function fetchCheckFileRef(ids: string) {
  return request<Api.SystemManage.FileCheckRefResult>({
    url: '/api/admin/file/checkRef',
    method: 'get',
    params: { ids }
  });
}
