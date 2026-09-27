import { request } from '../request';

/** 岗位分页列表 */
export function fetchGetPostList(params?: Api.SystemManage.PostSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.Post[]>({
    url: '/api/admin/post/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增岗位 */
export function fetchCreatePost(data: Api.SystemManage.PostInsert) {
  return request<null>({
    url: '/api/admin/post/add',
    method: 'post',
    data
  });
}

/** 修改岗位 */
export function fetchUpdatePost(data: Api.SystemManage.PostUpdate) {
  return request<null>({
    url: '/api/admin/post/update',
    method: 'put',
    data
  });
}

/** 删除岗位（逗号分隔 id） */
export function fetchDeletePost(ids: string) {
  return request<null>({
    url: '/api/admin/post/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停岗位 */
export function fetchUpdatePostEnabled(data: { postId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/api/admin/post/updateEnabled',
    method: 'patch',
    data
  });
}

/** 岗位名称是否可用 */
export function fetchCheckPostNameUnique(params: { postName: string }) {
  return request<boolean>({
    url: '/api/admin/post/checkName',
    method: 'get',
    params
  });
}

/** 岗位编码是否可用 */
export function fetchCheckPostCodeUnique(params: { postCode: string }) {
  return request<boolean>({
    url: '/api/admin/post/checkCode',
    method: 'get',
    params
  });
}

/** 岗位下拉（用户分配） */
export function fetchGetPostOptions() {
  return request<Api.SystemManage.PostOption[]>({
    url: '/api/admin/post/options',
    method: 'get'
  });
}
