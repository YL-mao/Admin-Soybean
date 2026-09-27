import { request } from '../request';

export interface AdminLoginParams {
  userAccount: string;
  userPassword: string;
  captcha: string;
}

/** 管理端登录：返回 token + 最小用户信息 */
export function fetchLogin(params: AdminLoginParams) {
  return request<Api.Auth.LoginToken>({
    url: '/api/admin/auth/login',
    method: 'post',
    data: params
  });
}

/** 管理端注销 */
export function fetchLogout() {
  return request<null>({
    url: '/api/admin/auth/logout',
    method: 'post'
  });
}

/** 当前用户信息：角色码 + 按钮权限码 */
export function fetchGetUserInfo() {
  return request<Api.Auth.UserInfo>({
    url: '/api/admin/auth/userInfo',
    method: 'get'
  });
}

/** 免登录品牌引导：系统名称 / Logo / 版权等展示快照 */
export function fetchGetBranding() {
  return request<Api.Auth.Branding>({
    url: '/api/admin/auth/branding',
    method: 'get'
  });
}
