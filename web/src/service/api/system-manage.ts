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
 * these roles are all enabled
 * （用户页仍用 mock；角色模块不依赖）
 */
export function fetchGetAllRoles() {
  return request<Api.SystemManage.AllRole[]>({
    url: '/systemManage/getAllRoles',
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

/** get user list */
export function fetchGetUserList(params?: Api.SystemManage.UserSearchParams) {
  return request<Api.SystemManage.UserList>({
    url: '/systemManage/getUserList',
    method: 'get',
    params
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
