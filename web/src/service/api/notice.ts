import { request } from '../request';

/** 公告分页（query：page/limit + 标题/类型/发布状态） */
export function fetchGetNoticeList(params?: Api.SystemManage.NoticeSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Notice[]>({
    url: '/api/admin/notice/list',
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
    url: '/api/admin/notice/add',
    method: 'post',
    data
  });
}

/** 修改公告（已发布后端会拒绝） */
export function fetchUpdateNotice(data: Api.SystemManage.NoticeUpdate) {
  return request<null>({
    url: '/api/admin/notice/update',
    method: 'put',
    data
  });
}

/** 删除公告（逗号分隔 id） */
export function fetchDeleteNotice(ids: string) {
  return request<null>({
    url: '/api/admin/notice/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 修改公告发布状态（已发布不能改回草稿） */
export function fetchUpdateNoticeEnabled(data: { noticeId: string; isSend: 0 | 1 }) {
  return request<null>({
    url: '/api/admin/notice/updateEnabled',
    method: 'patch',
    data
  });
}

/** 公告控制台：阅读统计（仅已发布） */
export function fetchGetNoticeConsoleStats(noticeId: string) {
  return request<Api.SystemManage.NoticeConsoleStats>({
    url: '/api/admin/notice/consoleStats',
    method: 'get',
    params: { noticeId }
  });
}

/** 公告控制台：接收人分页 */
export function fetchGetNoticeConsoleReceivers(params: Api.SystemManage.NoticeConsoleReceiverSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.NoticeConsoleReceiver[]>({
    url: '/api/admin/notice/receiverList',
    method: 'get',
    params: {
      ...rest,
      page: current ?? 1,
      limit: size ?? 10
    }
  });
}
