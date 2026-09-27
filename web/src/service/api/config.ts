import { request } from '../request';

/** 读取配置分组明细 */
export function fetchGetConfigGroup(configGroup: string) {
  return request<Api.SystemManage.ConfigGroupItem[]>({
    url: '/api/admin/config/group',
    method: 'get',
    params: { configGroup }
  });
}

/** 保存配置分组（只改 value / isEnabled） */
export function fetchUpdateConfigGroup(data: {
  configGroup: string;
  configs: Api.SystemManage.ConfigGroupSaveItem[];
}) {
  return request<null>({
    url: '/api/admin/config/updateGroup',
    method: 'put',
    data
  });
}
