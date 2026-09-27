import { request } from '../request';

/** 定时任务分页列表 */
export function fetchGetJobList(params?: Api.SystemManage.JobSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Job[]>({
    url: '/api/admin/job/list',
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
    url: '/api/admin/job/updateEnabled',
    method: 'patch',
    data
  });
}

/** 手动执行一次定时任务（不受启停限制） */
export function fetchRunJob(data: { jobId: string }) {
  return request<null>({
    url: '/api/admin/job/run',
    method: 'post',
    data
  });
}

/** 定时任务执行日志分页（jobId 必填） */
export function fetchGetJobLogList(params?: Api.SystemManage.JobLogSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.JobLog[]>({
    url: '/api/admin/job/logList',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}
