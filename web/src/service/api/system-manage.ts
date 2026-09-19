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
