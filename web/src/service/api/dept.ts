import { request } from '../request';

/** 部门列表（平铺，前端组树） */
export function fetchGetDeptList(params?: Api.SystemManage.DeptSearchParams) {
  return request<Api.SystemManage.Dept[]>({
    url: '/api/admin/dept/list',
    method: 'get',
    params
  });
}

/** 新增部门 */
export function fetchCreateDept(data: Api.SystemManage.DeptInsert) {
  return request<null>({
    url: '/api/admin/dept/add',
    method: 'post',
    data
  });
}

/** 修改部门 */
export function fetchUpdateDept(data: Api.SystemManage.DeptUpdate) {
  return request<null>({
    url: '/api/admin/dept/update',
    method: 'put',
    data
  });
}

/** 删除部门（逗号分隔 id） */
export function fetchDeleteDept(ids: string) {
  return request<null>({
    url: '/api/admin/dept/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停部门 */
export function fetchUpdateDeptEnabled(data: { deptId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/api/admin/dept/updateEnabled',
    method: 'patch',
    data
  });
}

/** 同级部门名称是否可用 */
export function fetchCheckDeptNameUnique(params: { parentId: string; deptName: string }) {
  return request<boolean>({
    url: '/api/admin/dept/checkName',
    method: 'get',
    params
  });
}

/** 上级部门选项（平铺） */
export function fetchGetDeptParentOptions() {
  return request<Api.SystemManage.DeptOption[]>({
    url: '/api/admin/dept/selectParent',
    method: 'get'
  });
}

/** 部门下拉（用户分配，仅启用） */
export function fetchGetDeptOptions() {
  return request<Api.SystemManage.DeptOption[]>({
    url: '/api/admin/dept/options',
    method: 'get'
  });
}
