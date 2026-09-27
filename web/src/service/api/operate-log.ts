import { request } from '../request';

/** 行为日志分页列表 */
export function fetchOperateLogList(params?: Api.SystemManage.OperateLogSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.OperateLog[]>({
    url: '/api/admin/operateLog/list',
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
    url: '/api/admin/operateLog/cleanByRetention',
    method: 'delete'
  });
}
