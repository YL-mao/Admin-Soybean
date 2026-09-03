import { request } from '../request';

/** get user routes（动态侧栏，来自 sys_menu） */
export function fetchGetUserRoutes() {
  return request<Api.Route.UserRoute>({
    url: '/api/admin/auth/getUserRoutes',
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
    url: '/api/admin/auth/isRouteExist',
    method: 'get',
    params: { routeName }
  });
}
