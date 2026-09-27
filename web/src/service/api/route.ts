import { request } from '../request';

/** 当前用户动态路由（侧栏，来自 sys_menu） */
export function fetchGetUserRoutes() {
  return request<Api.Route.UserRoute>({
    url: '/api/admin/auth/userRoutes',
    method: 'get'
  });
}

/**
 * whether the route is exist
 *
 * @param routeName route name
 */
export function fetchIsRouteExist(routeName: string) {
  return request<boolean>({
    url: '/api/admin/auth/routeExist',
    method: 'get',
    params: { routeName }
  });
}
