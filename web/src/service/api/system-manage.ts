import { request } from '../request';

/** 角色分页列表（query：page/limit + roleName） */
export function fetchGetRoleList(params?: Api.SystemManage.RoleSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Role[]>({
    url: '/role/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/**
 * get all roles
 *
 * @deprecated 改用 fetchGetRoleOptions
 */
export function fetchGetAllRoles() {
  return fetchGetRoleOptions();
}

/** 角色下拉（用户分配） */
export function fetchGetRoleOptions() {
  return request<Api.SystemManage.RoleOption[]>({
    url: '/role/options',
    method: 'get'
  });
}

/** 新增角色 */
export function fetchCreateRole(data: Api.SystemManage.RoleInsert) {
  return request<null>({
    url: '/role/add',
    method: 'post',
    data
  });
}

/** 修改角色 */
export function fetchUpdateRole(data: Api.SystemManage.RoleUpdate) {
  return request<null>({
    url: '/role/update',
    method: 'put',
    data
  });
}

/** 删除角色（逗号分隔 id） */
export function fetchDeleteRole(ids: string) {
  return request<null>({
    url: '/role/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停角色 */
export function fetchUpdateRoleEnabled(data: { roleId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/role/updateEnabled',
    method: 'patch',
    data
  });
}

/** 角色名称是否可用 */
export function fetchCheckRoleNameUnique(params: { roleName: string }) {
  return request<boolean>({
    url: '/role/checkName',
    method: 'get',
    params
  });
}

/** 角色编码是否可用 */
export function fetchCheckRoleCodeUnique(params: { roleCode: string }) {
  return request<boolean>({
    url: '/role/checkCode',
    method: 'get',
    params
  });
}

/** 角色菜单授权树（平铺，含 checkArr） */
export function fetchGetRoleMenuTree(roleId: string) {
  return request<Api.SystemManage.MenuCheck[]>({
    url: '/menu/roleTree',
    method: 'get',
    params: { roleId }
  });
}

/** 保存角色菜单授权（menuIds 逗号分隔，空串清空） */
export function fetchSaveRoleMenu(data: { roleId: string; menuIds: string }) {
  return request<null>({
    url: '/menu/roleMenu',
    method: 'put',
    data
  });
}

/** 岗位分页列表 */
export function fetchGetPostList(params?: Api.SystemManage.PostSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Post[]>({
    url: '/post/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增岗位 */
export function fetchCreatePost(data: Api.SystemManage.PostInsert) {
  return request<null>({
    url: '/post/add',
    method: 'post',
    data
  });
}

/** 修改岗位 */
export function fetchUpdatePost(data: Api.SystemManage.PostUpdate) {
  return request<null>({
    url: '/post/update',
    method: 'put',
    data
  });
}

/** 删除岗位（逗号分隔 id） */
export function fetchDeletePost(ids: string) {
  return request<null>({
    url: '/post/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停岗位 */
export function fetchUpdatePostEnabled(data: { postId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/post/updateEnabled',
    method: 'patch',
    data
  });
}

/** 岗位名称是否可用 */
export function fetchCheckPostNameUnique(params: { postName: string }) {
  return request<boolean>({
    url: '/post/checkName',
    method: 'get',
    params
  });
}

/** 岗位编码是否可用 */
export function fetchCheckPostCodeUnique(params: { postCode: string }) {
  return request<boolean>({
    url: '/post/checkCode',
    method: 'get',
    params
  });
}

/** 岗位下拉（用户分配） */
export function fetchGetPostOptions() {
  return request<Api.SystemManage.PostOption[]>({
    url: '/post/options',
    method: 'get'
  });
}

/** 部门列表（平铺，前端组树） */
export function fetchGetDeptList(params?: Api.SystemManage.DeptSearchParams) {
  return request<Api.SystemManage.Dept[]>({
    url: '/dept/list',
    method: 'get',
    params
  });
}

/** 新增部门 */
export function fetchCreateDept(data: Api.SystemManage.DeptInsert) {
  return request<null>({
    url: '/dept/add',
    method: 'post',
    data
  });
}

/** 修改部门 */
export function fetchUpdateDept(data: Api.SystemManage.DeptUpdate) {
  return request<null>({
    url: '/dept/update',
    method: 'put',
    data
  });
}

/** 删除部门（逗号分隔 id） */
export function fetchDeleteDept(ids: string) {
  return request<null>({
    url: '/dept/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停部门 */
export function fetchUpdateDeptEnabled(data: { deptId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/dept/updateEnabled',
    method: 'patch',
    data
  });
}

/** 同级部门名称是否可用 */
export function fetchCheckDeptNameUnique(params: { parentId: string; deptName: string }) {
  return request<boolean>({
    url: '/dept/checkName',
    method: 'get',
    params
  });
}

/** 上级部门选项（平铺） */
export function fetchGetDeptParentOptions() {
  return request<Api.SystemManage.DeptOption[]>({
    url: '/dept/selectParent',
    method: 'get'
  });
}

/** 部门下拉（用户分配，仅启用） */
export function fetchGetDeptOptions() {
  return request<Api.SystemManage.DeptOption[]>({
    url: '/dept/options',
    method: 'get'
  });
}

/** 用户分页列表（query：page/limit + userAccount/isEnabled/isLock） */
export function fetchGetUserList(params?: Api.SystemManage.UserSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.User[]>({
    url: '/user/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增用户 */
export function fetchCreateUser(data: Api.SystemManage.UserInsert) {
  return request<null>({
    url: '/user/add',
    method: 'post',
    data
  });
}

/** 修改用户 */
export function fetchUpdateUser(data: Api.SystemManage.UserUpdate) {
  return request<null>({
    url: '/user/update',
    method: 'put',
    data
  });
}

/** 删除用户（逗号分隔 id） */
export function fetchDeleteUser(ids: string) {
  return request<null>({
    url: '/user/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停用户 */
export function fetchUpdateUserEnabled(data: { userId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/user/updateEnabled',
    method: 'patch',
    data
  });
}

/** 改锁定状态（0 正常 / 1 锁定） */
export function fetchUpdateUserLock(data: { userId: string; isLock: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/user/updateLock',
    method: 'patch',
    data
  });
}

/** 登录账号是否可用 */
export function fetchCheckUserAccountUnique(params: { userAccount: string }) {
  return request<boolean>({
    url: '/user/checkAccount',
    method: 'get',
    params
  });
}

/** 导出用户列表（与列表相同筛选，不分页；返回 xlsx Blob） */
export function fetchExportUserList(
  params?: Pick<Api.SystemManage.UserSearchParams, 'userAccount' | 'isEnabled' | 'isLock'>
) {
  return request<Blob, 'blob'>({
    url: '/user/export',
    method: 'get',
    params: {
      userAccount: params?.userAccount,
      isEnabled: params?.isEnabled,
      isLock: params?.isLock
    },
    responseType: 'blob'
  });
}

/** 管理员重置密码 */
export function fetchUpdateUserPwd(data: Api.SystemManage.UserUpdatePwd) {
  return request<null>({
    url: '/user/updatePwd',
    method: 'patch',
    data
  });
}

/** 按用户强退全部会话 */
export function fetchKickUserSessions(data: { userId: string }) {
  return request<null>({
    url: '/user/kickSessions',
    method: 'patch',
    data
  });
}

/** 用户最终权限详情（角色 + 并集权限平铺） */
export function fetchGetUserPermDetail(userId: string) {
  return request<Api.SystemManage.UserPermDetail>({
    url: '/user/permDetail',
    method: 'get',
    params: { userId }
  });
}

/** 菜单列表（平铺，前端组树） */
export function fetchGetMenuList(params?: Api.SystemManage.MenuSearchParams) {
  return request<Api.SystemManage.Menu[]>({
    url: '/menu/list',
    method: 'get',
    params
  });
}

/** 新增菜单 */
export function fetchCreateMenu(data: Api.SystemManage.MenuInsert) {
  return request<null>({
    url: '/menu/add',
    method: 'post',
    data
  });
}

/** 修改菜单 */
export function fetchUpdateMenu(data: Api.SystemManage.MenuUpdate) {
  return request<null>({
    url: '/menu/update',
    method: 'put',
    data
  });
}

/** 删除菜单（逗号分隔 id） */
export function fetchDeleteMenu(ids: string) {
  return request<null>({
    url: '/menu/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停菜单 */
export function fetchUpdateMenuEnabled(data: { menuId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/menu/updateEnabled',
    method: 'patch',
    data
  });
}

/** 上级菜单选项（平铺，含顶级） */
export function fetchGetMenuParentOptions() {
  return request<Api.SystemManage.MenuParent[]>({
    url: '/menu/selectParent',
    method: 'get'
  });
}

/** 同级菜单名称是否可用 */
export function fetchCheckMenuNameUnique(params: { parentId: string; menuName: string }) {
  return request<boolean>({
    url: '/menu/checkName',
    method: 'get',
    params
  });
}

/** 权限标识是否可用 */
export function fetchCheckMenuCodeUnique(params: { permCode: string }) {
  return request<boolean>({
    url: '/menu/checkCode',
    method: 'get',
    params
  });
}

/** 字典类型分页（query：page/limit + dictTypeName） */
export function fetchGetDictTypeList(params?: Api.SystemManage.DictTypeSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.DictType[]>({
    url: '/dictType/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增字典类型 */
export function fetchCreateDictType(data: Api.SystemManage.DictTypeInsert) {
  return request<null>({
    url: '/dictType/add',
    method: 'post',
    data
  });
}

/** 修改字典类型 */
export function fetchUpdateDictType(data: Api.SystemManage.DictTypeUpdate) {
  return request<null>({
    url: '/dictType/update',
    method: 'put',
    data
  });
}

/** 删除字典类型（逗号分隔 id，后端会连带删除其字典数据） */
export function fetchDeleteDictType(ids: string) {
  return request<null>({
    url: '/dictType/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停字典类型 */
export function fetchUpdateDictTypeEnabled(data: { dictTypeId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/dictType/updateEnabled',
    method: 'patch',
    data
  });
}

/** 字典编码是否可用 */
export function fetchCheckDictTypeCodeUnique(params: { dictTypeCode: string }) {
  return request<boolean>({
    url: '/dictType/checkCode',
    method: 'get',
    params
  });
}

/** 字典数据分页（未传 dictTypeCode 时后端返回空页） */
export function fetchGetDictDataList(params?: Api.SystemManage.DictDataSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.DictData[]>({
    url: '/dictData/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增字典数据 */
export function fetchCreateDictData(data: Api.SystemManage.DictDataInsert) {
  return request<null>({
    url: '/dictData/add',
    method: 'post',
    data
  });
}

/** 修改字典数据 */
export function fetchUpdateDictData(data: Api.SystemManage.DictDataUpdate) {
  return request<null>({
    url: '/dictData/update',
    method: 'put',
    data
  });
}

/** 删除字典数据（逗号分隔 id） */
export function fetchDeleteDictData(ids: string) {
  return request<null>({
    url: '/dictData/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停字典数据 */
export function fetchUpdateDictDataEnabled(data: { dictDataId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/dictData/updateEnabled',
    method: 'patch',
    data
  });
}

/** 设置或取消默认项（同一类型只保留一个默认） */
export function fetchUpdateDictDataDefault(data: { dictDataId: string; isDefault: Api.SystemManage.DictDefaultFlag }) {
  return request<null>({
    url: '/dictData/updateDefault',
    method: 'patch',
    data
  });
}

/** 同一类型下数据标签是否可用 */
export function fetchCheckDictDataLabelUnique(params: { dictTypeCode: string; dictDataLabel: string }) {
  return request<boolean>({
    url: '/dictData/checkLabel',
    method: 'get',
    params
  });
}

/** 同一类型下数据值是否可用 */
export function fetchCheckDictDataValueUnique(params: { dictTypeCode: string; dictDataValue: string }) {
  return request<boolean>({
    url: '/dictData/checkValue',
    method: 'get',
    params
  });
}

/** 刷新指定字典编码的运行时缓存 */
export function fetchRefreshDictCache(dictTypeCode: string) {
  return request<null>({
    url: '/dictData/refreshCache',
    method: 'patch',
    params: { dictTypeCode }
  });
}

/** 读取配置分组明细 */
export function fetchGetConfigGroup(configGroup: string) {
  return request<Api.SystemManage.ConfigGroupItem[]>({
    url: '/config/group',
    method: 'get',
    params: { configGroup }
  });
}

/** 保存配置分组（只改 value / isEnabled） */
export function fetchUpdateConfigGroup(data: {
  configGroup: string;
  configs: Api.SystemManage.ConfigGroupSaveItem[];
}) {
  return request<null>({
    url: '/config/updateGroup',
    method: 'put',
    data
  });
}

/** 行为日志分页列表 */
export function fetchOperateLogList(params?: Api.SystemManage.OperateLogSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.OperateLog[]>({
    url: '/operateLog/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 按保留天数清理过期日志 */
export function fetchCleanOperateLogByRetention() {
  return request<number>({
    url: '/operateLog/cleanByRetention',
    method: 'delete'
  });
}

/** 访问控制分页列表 */
export function fetchGetFilterList(params?: Api.SystemManage.FilterSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Filter[]>({
    url: '/filter/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增访问控制 */
export function fetchCreateFilter(data: Api.SystemManage.FilterInsert) {
  return request<null>({
    url: '/filter/add',
    method: 'post',
    data
  });
}

/** 修改访问控制 */
export function fetchUpdateFilter(data: Api.SystemManage.FilterUpdate) {
  return request<null>({
    url: '/filter/update',
    method: 'put',
    data
  });
}

/** 删除访问控制 */
export function fetchDeleteFilter(ids: string) {
  return request<null>({
    url: '/filter/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停访问控制 */
export function fetchUpdateFilterEnabled(data: { filterId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/filter/updateEnabled',
    method: 'patch',
    data
  });
}

/** 定时任务分页列表 */
export function fetchGetJobList(params?: Api.SystemManage.JobSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Job[]>({
    url: '/job/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 启停定时任务 */
export function fetchUpdateJobEnabled(data: { jobId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/job/updateEnabled',
    method: 'patch',
    data
  });
}

/** 手动执行一次定时任务（不受启停限制） */
export function fetchRunJob(data: { jobId: string }) {
  return request<null>({
    url: '/job/run',
    method: 'post',
    data
  });
}

/** 定时任务执行日志分页（jobId 必填） */
export function fetchGetJobLogList(params?: Api.SystemManage.JobLogSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.JobLog[]>({
    url: '/job/logList',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 在线用户（会话）分页列表 */
export function fetchGetOnlineUserList(params?: Api.SystemManage.OnlineSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.OnlineUser[]>({
    url: '/online/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 按 Token 强退单个会话 */
export function fetchKickOnlineSession(data: { tokenValue: string }) {
  return request<null>({
    url: '/online/kick',
    method: 'patch',
    data
  });
}

/** 运行时字典选项（登录即可） */
export function fetchGetDictOptions(dictTypeCode: string) {
  return request<Api.SystemManage.DictOption[]>({
    url: '/dictData/options',
    method: 'get',
    params: { dictTypeCode }
  });
}

/** 公告分页（query：page/limit + 标题/类型/发布状态） */
export function fetchGetNoticeList(params?: Api.SystemManage.NoticeSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Notice[]>({
    url: '/notice/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增公告 */
export function fetchCreateNotice(data: Api.SystemManage.NoticeInsert) {
  return request<null>({
    url: '/notice/add',
    method: 'post',
    data
  });
}

/** 修改公告（已发布后端会拒绝） */
export function fetchUpdateNotice(data: Api.SystemManage.NoticeUpdate) {
  return request<null>({
    url: '/notice/update',
    method: 'put',
    data
  });
}

/** 删除公告（逗号分隔 id） */
export function fetchDeleteNotice(ids: string) {
  return request<null>({
    url: '/notice/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 修改公告发布状态（已发布不能改回草稿） */
export function fetchUpdateNoticeEnabled(data: { noticeId: string; isSend: 0 | 1 }) {
  return request<null>({
    url: '/notice/updateEnabled',
    method: 'patch',
    data
  });
}

/** 公告接收人检索（指定个人） */
export function fetchSearchNoticeUser(keyword: string) {
  return request<Api.SystemManage.UserOption[]>({
    url: '/notice/searchUser',
    method: 'get',
    params: { keyword }
  });
}

/** 目录树（平铺，前端组树） */
export function fetchGetFolderTree() {
  return request<Api.SystemManage.FolderOption[]>({
    url: '/folder/tree',
    method: 'get'
  });
}

/** 新增目录 */
export function fetchCreateFolder(data: Api.SystemManage.FolderInsert) {
  return request<null>({
    url: '/folder/add',
    method: 'post',
    data
  });
}

/** 修改目录 */
export function fetchUpdateFolder(data: Api.SystemManage.FolderUpdate) {
  return request<null>({
    url: '/folder/update',
    method: 'put',
    data
  });
}

/** 删除目录（级联软删） */
export function fetchDeleteFolder(ids: string) {
  return request<null>({
    url: '/folder/delete',
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
  // 虚拟根「0」不传 folderId，与 Layui 一致查全部
  if (!query.folderId || query.folderId === '0') {
    delete query.folderId;
  }
  return request<Api.SystemManage.FileResource[]>({
    url: '/file/list',
    method: 'get',
    params: query
  });
}

/** 上传规则（后缀/大小）；禁缓存，避免关开关后命中旧成功响应误开抽屉 */
export function fetchGetFileUploadRules() {
  return request<Api.SystemManage.FileUploadRules>({
    url: '/file/uploadRules',
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
    url: '/file/upload',
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
    url: '/file/overwrite',
    method: 'post',
    data: formData
  });
}

/** 修改文件元数据（本期不改目录，仍需带上当前 folderId） */
export function fetchUpdateFile(data: Api.SystemManage.FileUpdate) {
  return request<null>({
    url: '/file/update',
    method: 'put',
    data
  });
}

/** 删除文件 */
export function fetchDeleteFile(ids: string) {
  return request<null>({
    url: '/file/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 删除前引用检测 */
export function fetchCheckFileRef(ids: string) {
  return request<Api.SystemManage.FileCheckRefResult>({
    url: '/file/checkRef',
    method: 'get',
    params: { ids }
  });
}

/** 当前用户个人资料详情 */
export function fetchGetUserProfileDetail() {
  return request<Api.SystemManage.UserProfileDetail>({
    url: '/user/info/detail',
    method: 'get'
  });
}

/** 保存当前用户可改资料 */
export function fetchUpdateUserProfile(data: Api.SystemManage.UserProfileSave) {
  return request<null>({
    url: '/user/info/update',
    method: 'put',
    data
  });
}

/** 当前用户修改自己的密码（成功后后端强制注销） */
export function fetchUpdateOwnPassword(data: Api.SystemManage.UserOwnPasswordUpdate) {
  return request<null>({
    url: '/user/info/updatePwd',
    method: 'patch',
    data
  });
}

/** 更新当前用户头像地址（上传接口返回的 accessUrl） */
export function fetchUpdateOwnAvatar(userAvatar: string) {
  return request<null>({
    url: '/user/info/updateAvatar',
    method: 'patch',
    data: { userAvatar }
  });
}

/** 当前用户最近登录记录（分页：page/limit，倒序） */
export function fetchGetOwnLoginLogs(params?: Api.SystemManage.CommonSearchParams) {
  const { current, size } = params || {};
  return request<Api.SystemManage.UserOwnLoginLog[]>({
    url: '/user/info/loginLog',
    method: 'get',
    params: {
      page: current ?? 1,
      limit: size ?? 10
    }
  });
}
