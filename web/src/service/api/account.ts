import { request } from '../request';

/** 当前用户个人资料详情 */
export function fetchGetUserProfileDetail() {
  return request<Api.SystemManage.UserProfileDetail>({
    url: '/api/admin/user/info/detail',
    method: 'get'
  });
}

/** 保存当前用户可改资料 */
export function fetchUpdateUserProfile(data: Api.SystemManage.UserProfileSave) {
  return request<null>({
    url: '/api/admin/user/info/update',
    method: 'put',
    data
  });
}

/** 当前用户修改自己的密码（成功后后端强制注销） */
export function fetchUpdateOwnPassword(data: Api.SystemManage.UserOwnPasswordUpdate) {
  return request<null>({
    url: '/api/admin/user/info/updatePwd',
    method: 'patch',
    data
  });
}

/** 更新当前用户头像地址（上传接口返回的 accessUrl） */
export function fetchUpdateOwnAvatar(userAvatar: string) {
  return request<null>({
    url: '/api/admin/user/info/updateAvatar',
    method: 'patch',
    data: { userAvatar }
  });
}

/** 当前用户最近登录记录（分页：page/limit，倒序） */
export function fetchGetOwnLoginLogs(params?: Api.SystemManage.CommonSearchParams) {
  const { current, size } = params || {};
  return request<Api.SystemManage.UserOwnLoginLog[]>({
    url: '/api/admin/user/info/loginLog',
    method: 'get',
    params: {
      page: current ?? 1,
      limit: size ?? 10
    }
  });
}

/** 顶栏/首页公告头：真实未读总数 + 按类型短列表（每类最多 8 条） */
export function fetchGetUserNoticeHeader() {
  return request<Api.SystemManage.UserNoticeHeader>({
    url: '/api/admin/user/notice/header',
    method: 'get'
  });
}

/** 我的公告收件箱分页 */
export function fetchGetUserInboxNoticeList(params?: Api.SystemManage.UserInboxNoticeSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.UserInboxNotice[]>({
    url: '/api/admin/user/notice/list',
    method: 'get',
    params: {
      ...rest,
      page: current ?? 1,
      limit: size ?? 10
    }
  });
}

/** 单条标记已读 */
export function fetchUpdateUserNoticeRead(noticeId: string) {
  return request<null>({
    url: '/api/admin/user/notice/updateRead',
    method: 'patch',
    data: { noticeId }
  });
}

/** 全部标记已读 */
export function fetchReadAllUserNotices() {
  return request<null>({
    url: '/api/admin/user/notice/readAll',
    method: 'patch'
  });
}
