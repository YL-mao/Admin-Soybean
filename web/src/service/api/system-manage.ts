import { request } from '../request';

/** get role list */
export function fetchGetRoleList(params?: Api.SystemManage.RoleSearchParams) {
  return request<Api.SystemManage.RoleList>({
    url: '/systemManage/getRoleList',
    method: 'get',
    params
  });
}

/**
 * get all roles
 *
 * these roles are all enabled
 */
export function fetchGetAllRoles() {
  return request<Api.SystemManage.AllRole[]>({
    url: '/systemManage/getAllRoles',
    method: 'get'
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

/** get all pages（角色授权样板仍用；菜单页不再依赖） */
export function fetchGetAllPages() {
  return request<string[]>({
    url: '/systemManage/getAllPages',
    method: 'get'
  });
}

/** get menu tree（角色授权样板仍用） */
export function fetchGetMenuTree() {
  return request<Api.SystemManage.MenuTree[]>({
    url: '/systemManage/getMenuTree',
    method: 'get'
  });
}
