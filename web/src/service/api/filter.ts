import { request } from '../request';

/** 访问控制分页列表 */
export function fetchGetFilterList(params?: Api.SystemManage.FilterSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Filter[]>({
    url: '/api/admin/filter/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增访问控制 */
export function fetchCreateFilter(data: Api.SystemManage.FilterInsert) {
  return request<null>({
    url: '/api/admin/filter/add',
    method: 'post',
    data
  });
}

/** 修改访问控制 */
export function fetchUpdateFilter(data: Api.SystemManage.FilterUpdate) {
  return request<null>({
    url: '/api/admin/filter/update',
    method: 'put',
    data
  });
}

/** 删除访问控制 */
export function fetchDeleteFilter(ids: string) {
  return request<null>({
    url: '/api/admin/filter/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停访问控制 */
export function fetchUpdateFilterEnabled(data: { filterId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/api/admin/filter/updateEnabled',
    method: 'patch',
    data
  });
}
