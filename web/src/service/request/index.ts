import type { AxiosResponse } from 'axios';
import { BACKEND_ERROR_CODE, createFlatRequest } from '@sa/axios';
import { useAuthStore } from '@/store/modules/auth';
import { getServiceBaseURL } from '@/utils/service';
import { $t } from '@/locales';
import { getAuthorization, showErrorMsg } from './shared';
import type { RequestInstanceState } from './type';
import { getDeviceId, DEVICE_ID_HEADER } from '@/utils/device-id';

const isHttpProxy = import.meta.env.DEV && import.meta.env.VITE_HTTP_PROXY === 'Y';
const { baseURL } = getServiceBaseURL(import.meta.env, isHttpProxy);

export const request = createFlatRequest(
  {
    baseURL,
    withCredentials: true
  },
  {
    defaultState: {
      errMsgStack: []
    } as RequestInstanceState,
    transform(response: AxiosResponse<App.Service.Response<any>>) {
      return response.data.data;
    },
    async onRequest(config) {
      const token = getAuthorization();
      if (token) {
        Object.assign(config.headers, { saToken: token });
      }
      // 管理端指纹：deviceId 只走 Header，不进登录 body
      Object.assign(config.headers, { [DEVICE_ID_HEADER]: getDeviceId() });

      // FormData 必须去掉默认 application/json，否则 axios 会把表单序列化成 JSON 导致上传失败
      if (typeof FormData !== 'undefined' && config.data instanceof FormData) {
        config.headers.set('Content-Type', null);
      }

      return config;
    },
    isBackendSuccess(response) {
      // when the backend response code is "0000"(default), it means the request is success
      // to change this logic by yourself, you can modify the `VITE_SERVICE_SUCCESS_CODE` in `.env` file
      return String(response.data.code) === import.meta.env.VITE_SERVICE_SUCCESS_CODE;
    },
    async onBackendFail(response) {
      const authStore = useAuthStore();
      const responseCode = String(response.data.code);

      function handleLogout() {
        authStore.resetStore();
      }

      function logoutAndCleanup() {
        handleLogout();
        window.removeEventListener('beforeunload', handleLogout);

        request.state.errMsgStack = request.state.errMsgStack.filter(msg => msg !== response.data.msg);
      }

      // when the backend response code is in `logoutCodes`, it means the user will be logged out and redirected to login page
      const logoutCodes = import.meta.env.VITE_SERVICE_LOGOUT_CODES?.split(',') || [];
      if (logoutCodes.includes(responseCode)) {
        handleLogout();
        return null;
      }

      // when the backend response code is in `modalLogoutCodes`, it means the user will be logged out by displaying a modal
      const modalLogoutCodes = import.meta.env.VITE_SERVICE_MODAL_LOGOUT_CODES?.split(',') || [];
      if (modalLogoutCodes.includes(responseCode) && !request.state.errMsgStack?.includes(response.data.msg)) {
        request.state.errMsgStack = [...(request.state.errMsgStack || []), response.data.msg];

        // prevent the user from refreshing the page
        window.addEventListener('beforeunload', handleLogout);

        window.$dialog?.error({
          title: $t('common.error'),
          content: response.data.msg,
          positiveText: $t('common.confirm'),
          maskClosable: false,
          closeOnEsc: false,
          onPositiveClick() {
            logoutAndCleanup();
          },
          onClose() {
            logoutAndCleanup();
          }
        });

        return null;
      }

      // Sa-Token 自动续签，无独立 refresh；失效码应配置到 logout / modalLogout
      return null;
    },
    onError(error) {
      // when the request is fail, you can show error message

      let message = error.message;
      let backendErrorCode = '';

      const responseData = error.response?.data as App.Service.Response | undefined;

      // HTTP 200 但业务 code 非成功时走 BACKEND_ERROR_CODE；HTTP 4xx/5xx 且响应体为 R 时同样取 msg
      if (error.code === BACKEND_ERROR_CODE) {
        message = responseData?.msg || message;
        backendErrorCode = String(responseData?.code || '');
      } else if (responseData?.msg) {
        message = responseData.msg;
        backendErrorCode = String(responseData?.code || '');
      }

      // HTTP 401 等：提示登录失效并清会话跳转（Sa 过滤器直接回 HTTP 状态，不走 onBackendFail）
      const logoutCodes = import.meta.env.VITE_SERVICE_LOGOUT_CODES?.split(',') || [];
      if (logoutCodes.includes(backendErrorCode)) {
        // 优先用后端统一文案；无 msg 时回退 i18n
        showErrorMsg(request.state, message || $t('request.logoutMsg'));
        useAuthStore().resetStore();
        return;
      }

      // the error message is displayed in the modal
      const modalLogoutCodes = import.meta.env.VITE_SERVICE_MODAL_LOGOUT_CODES?.split(',') || [];
      if (modalLogoutCodes.includes(backendErrorCode)) {
        return;
      }

      showErrorMsg(request.state, message);
    }
  }
);
