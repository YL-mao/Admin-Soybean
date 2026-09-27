import { request } from '../request';

/** 在线用户（会话）分页列表 */
export function fetchGetOnlineUserList(params?: Api.SystemManage.OnlineSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.OnlineUser[]>({
    url: '/api/admin/online/list',
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
    url: '/api/admin/online/kick',
    method: 'patch',
    data
  });
}
