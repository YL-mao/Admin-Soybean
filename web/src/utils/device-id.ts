import { localStg } from '@/utils/storage';

/** 与后端 FingerprintKeys.Admin.DEVICE_ID_HEADER 对齐 */
export const DEVICE_ID_HEADER = 'X-Device-Id';

/** 获取或生成浏览器实例 deviceId（localStorage，登录与后续请求走 Header） */
export function getDeviceId() {
  let deviceId = localStg.get('deviceId');
  if (!deviceId) {
    deviceId = crypto.randomUUID();
    localStg.set('deviceId', deviceId);
  }
  return deviceId;
}
