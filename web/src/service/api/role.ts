import { request } from '../request';

/** 角色分页列表（query：page/limit + roleName） */
export function fetchGetRoleList(params?: Api.SystemManage.RoleSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Role[]>({
    url: '/api/admin/role/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 角色下拉（用户分配） */
export function fetchGetRoleOptions() {
  return request<Api.SystemManage.RoleOption[]>({
    url: '/api/admin/role/options',
    method: 'get'
  });
}

/** 新增角色 */
export function fetchCreateRole(data: Api.SystemManage.RoleInsert) {
  return request<null>({
    url: '/api/admin/role/add',
    method: 'post',
    data
  });
}

/** 修改角色 */
export function fetchUpdateRole(data: Api.SystemManage.RoleUpdate) {
  return request<null>({
    url: '/api/admin/role/update',
    method: 'put',
    data
  });
}

/** 删除角色（逗号分隔 id） */
export function fetchDeleteRole(ids: string) {
  return request<null>({
    url: '/api/admin/role/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停角色 */
export function fetchUpdateRoleEnabled(data: { roleId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/api/admin/role/updateEnabled',
    method: 'patch',
    data
  });
}

/** 角色名称是否可用 */
export function fetchCheckRoleNameUnique(params: { roleName: string }) {
  return request<boolean>({
    url: '/api/admin/role/checkName',
    method: 'get',
    params
  });
}

/** 角色编码是否可用 */
export function fetchCheckRoleCodeUnique(params: { roleCode: string }) {
  return request<boolean>({
    url: '/api/admin/role/checkCode',
    method: 'get',
    params
  });
}

/** 角色菜单授权树（平铺，含 checkArr） */
export function fetchGetRoleMenuTree(roleId: string) {
  return request<Api.SystemManage.MenuCheck[]>({
    url: '/api/admin/menu/roleTree',
    method: 'get',
    params: { roleId }
  });
}

/** 保存角色菜单授权（menuIds 逗号分隔，空串清空） */
export function fetchSaveRoleMenu(data: { roleId: string; menuIds: string }) {
  return request<null>({
    url: '/api/admin/menu/roleMenu',
    method: 'put',
    data
  });
}
