import { request } from '../request';

/** 用户分页列表（query：page/limit + userAccount/isEnabled/isLock） */
export function fetchGetUserList(params?: Api.SystemManage.UserSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.User[]>({
    url: '/api/admin/user/list',
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
    url: '/api/admin/user/add',
    method: 'post',
    data
  });
}

/** 修改用户 */
export function fetchUpdateUser(data: Api.SystemManage.UserUpdate) {
  return request<null>({
    url: '/api/admin/user/update',
    method: 'put',
    data
  });
}

/** 删除用户（逗号分隔 id） */
export function fetchDeleteUser(ids: string) {
  return request<null>({
    url: '/api/admin/user/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停用户 */
export function fetchUpdateUserEnabled(data: { userId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/api/admin/user/updateEnabled',
    method: 'patch',
    data
  });
}

/** 改锁定状态（0 正常 / 1 锁定） */
export function fetchUpdateUserLock(data: { userId: string; isLock: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/api/admin/user/updateLock',
    method: 'patch',
    data
  });
}

/** 登录账号是否可用 */
export function fetchCheckUserAccountUnique(params: { userAccount: string }) {
  return request<boolean>({
    url: '/api/admin/user/checkAccount',
    method: 'get',
    params
  });
}

/** 导出用户列表（与列表相同筛选，不分页；返回 xlsx Blob） */
export function fetchExportUserList(
  params?: Pick<Api.SystemManage.UserSearchParams, 'userAccount' | 'isEnabled' | 'isLock'>
) {
  return request<Blob, 'blob'>({
    url: '/api/admin/user/export',
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
    url: '/api/admin/user/updatePwd',
    method: 'patch',
    data
  });
}

/** 按用户强退全部会话（在线用户与用户列表共用） */
export function fetchKickOnlineUser(data: { userId: string }) {
  return request<null>({
    url: '/api/admin/online/kickUser',
    method: 'patch',
    data
  });
}

/** 用户最终权限详情（角色 + 并集权限平铺） */
export function fetchGetUserPermDetail(userId: string) {
  return request<Api.SystemManage.UserPermDetail>({
    url: '/api/admin/user/permDetail',
    method: 'get',
    params: { userId }
  });
}

/** 用户远程检索（公告指定人、在线筛选等共用） */
export function fetchSearchUser(keyword: string) {
  return request<Api.SystemManage.UserOption[]>({
    url: '/api/admin/user/search',
    method: 'get',
    params: { keyword }
  });
}
