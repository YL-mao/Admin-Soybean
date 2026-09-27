import { request } from '../request';

/** 字典类型分页（query：page/limit + dictTypeName） */
export function fetchGetDictTypeList(params?: Api.SystemManage.DictTypeSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.DictType[]>({
    url: '/api/admin/dictType/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增字典类型 */
export function fetchCreateDictType(data: Api.SystemManage.DictTypeInsert) {
  return request<null>({
    url: '/api/admin/dictType/add',
    method: 'post',
    data
  });
}

/** 修改字典类型 */
export function fetchUpdateDictType(data: Api.SystemManage.DictTypeUpdate) {
  return request<null>({
    url: '/api/admin/dictType/update',
    method: 'put',
    data
  });
}

/** 删除字典类型（逗号分隔 id，后端会连带删除其字典数据） */
export function fetchDeleteDictType(ids: string) {
  return request<null>({
    url: '/api/admin/dictType/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停字典类型 */
export function fetchUpdateDictTypeEnabled(data: { dictTypeId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/api/admin/dictType/updateEnabled',
    method: 'patch',
    data
  });
}

/** 字典编码是否可用 */
export function fetchCheckDictTypeCodeUnique(params: { dictTypeCode: string }) {
  return request<boolean>({
    url: '/api/admin/dictType/checkCode',
    method: 'get',
    params
  });
}

/** 字典数据分页（未传 dictTypeCode 时后端返回空页） */
export function fetchGetDictDataList(params?: Api.SystemManage.DictDataSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.SystemManage.DictData[]>({
    url: '/api/admin/dictData/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 新增字典数据 */
export function fetchCreateDictData(data: Api.SystemManage.DictDataInsert) {
  return request<null>({
    url: '/api/admin/dictData/add',
    method: 'post',
    data
  });
}

/** 修改字典数据 */
export function fetchUpdateDictData(data: Api.SystemManage.DictDataUpdate) {
  return request<null>({
    url: '/api/admin/dictData/update',
    method: 'put',
    data
  });
}

/** 删除字典数据（逗号分隔 id） */
export function fetchDeleteDictData(ids: string) {
  return request<null>({
    url: '/api/admin/dictData/delete',
    method: 'delete',
    params: { ids }
  });
}

/** 启停字典数据 */
export function fetchUpdateDictDataEnabled(data: { dictDataId: string; isEnabled: Api.SystemManage.EnabledFlag }) {
  return request<null>({
    url: '/api/admin/dictData/updateEnabled',
    method: 'patch',
    data
  });
}

/** 设置或取消默认项（同一类型只保留一个默认） */
export function fetchUpdateDictDataDefault(data: { dictDataId: string; isDefault: Api.SystemManage.DictDefaultFlag }) {
  return request<null>({
    url: '/api/admin/dictData/updateDefault',
    method: 'patch',
    data
  });
}

/** 同一类型下数据标签是否可用 */
export function fetchCheckDictDataLabelUnique(params: { dictTypeCode: string; dictDataLabel: string }) {
  return request<boolean>({
    url: '/api/admin/dictData/checkLabel',
    method: 'get',
    params
  });
}

/** 同一类型下数据值是否可用 */
export function fetchCheckDictDataValueUnique(params: { dictTypeCode: string; dictDataValue: string }) {
  return request<boolean>({
    url: '/api/admin/dictData/checkValue',
    method: 'get',
    params
  });
}

/** 刷新指定字典编码的运行时缓存 */
export function fetchRefreshDictCache(dictTypeCode: string) {
  return request<null>({
    url: '/api/admin/dictData/refreshCache',
    method: 'patch',
    params: { dictTypeCode }
  });
}

/** 运行时字典选项（登录即可） */
export function fetchGetDictOptions(dictTypeCode: string) {
  return request<Api.SystemManage.DictOption[]>({
    url: '/api/admin/dictData/options',
    method: 'get',
    params: { dictTypeCode }
  });
}
