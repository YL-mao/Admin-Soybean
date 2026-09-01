import { computed, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';
import { defineStore } from 'pinia';
import { useLoading } from '@sa/hooks';
import { fetchLogin, fetchLogout } from '@/service/api';
import { useRouterPush } from '@/hooks/common/router';
import { localStg } from '@/utils/storage';
import { SetupStoreId } from '@/enum';
import { $t } from '@/locales';
import { useRouteStore } from '../route';
import { useTabStore } from '../tab';
import { clearAuthStorage, getToken } from './shared';

export const useAuthStore = defineStore(SetupStoreId.Auth, () => {
  const route = useRoute();
  const authStore = useAuthStore();
  const routeStore = useRouteStore();
  const tabStore = useTabStore();
  const { toLogin, redirectFromLogin } = useRouterPush(false);
  const { loading: loginLoading, startLoading, endLoading } = useLoading();

  const token = ref('');

  const userInfo: Api.Auth.UserInfo = reactive({
    userId: '',
    userName: '',
    roles: [],
    buttons: []
  });

  /** is super role in static route */
  const isStaticSuper = computed(() => {
    const { VITE_AUTH_ROUTE_MODE, VITE_STATIC_SUPER_ROLE } = import.meta.env;

    return VITE_AUTH_ROUTE_MODE === 'static' && userInfo.roles.includes(VITE_STATIC_SUPER_ROLE);
  });

  /** Is login */
  const isLogin = computed(() => Boolean(token.value));

  /** Reset auth store */
  async function resetStore() {
    recordUserId();

    if (getToken()) {
      await fetchLogout().catch(() => undefined);
    }

    clearAuthStorage();

    authStore.$reset();

    if (!route.meta.constant) {
      await toLogin();
    }

    tabStore.cacheTabs();
    routeStore.resetStore();
  }

  function recordUserId() {
    if (!userInfo.userId) {
      return;
    }

    localStg.set('lastLoginUserId', userInfo.userId);
  }

  function checkTabClear(): boolean {
    if (!userInfo.userId) {
      return false;
    }

    const lastLoginUserId = localStg.get('lastLoginUserId');

    if (!lastLoginUserId || lastLoginUserId !== userInfo.userId) {
      localStg.remove('globalTabs');
      tabStore.clearTabs();

      localStg.remove('lastLoginUserId');
      return true;
    }

    localStg.remove('lastLoginUserId');
    return false;
  }

  /**
   * Login
   *
   * @param userAccount Login account
   * @param userPassword Password
   * @param captcha Image captcha
   * @param redirect Whether to redirect after login. Default is `true`
   */
  async function login(userAccount: string, userPassword: string, captcha: string, redirect = true) {
    startLoading();

    const { data: loginToken, error } = await fetchLogin({
      userAccount,
      userPassword,
      captcha
    });

    if (!error && loginToken) {
      const pass = loginByToken(loginToken);

      if (pass) {
        const isClear = checkTabClear();
        let needRedirect = redirect;

        if (isClear) {
          needRedirect = false;
        }
        await redirectFromLogin(needRedirect);

        window.$notification?.success({
          title: $t('page.login.common.loginSuccess'),
          content: $t('page.login.common.welcomeBack', { userName: userInfo.userName }),
          duration: 4500
        });
        endLoading();
        return true;
      }
    }

    endLoading();
    return false;
  }

  /** 落地 token / 用户信息到本地与 store（严格 Header 模式，不依赖 Cookie 恢复） */
  function loginByToken(loginToken: Api.Auth.LoginToken) {
    if (!loginToken?.token) {
      return false;
    }

    localStg.set('token', loginToken.token);
    localStg.set('userInfo', {
      userId: loginToken.userId,
      userName: loginToken.userName,
      roles: loginToken.roles,
      buttons: []
    });

    Object.assign(userInfo, {
      userId: loginToken.userId,
      userName: loginToken.userName,
      roles: loginToken.roles,
      buttons: []
    });
    token.value = loginToken.token;

    return true;
  }

  /** 刷新恢复：仅从本地缓存还原（无 getSession） */
  async function initUserInfo() {
    const maybeToken = getToken();
    const cached = localStg.get('userInfo');

    if (maybeToken && cached?.userId) {
      token.value = maybeToken;
      Object.assign(userInfo, {
        userId: cached.userId,
        userName: cached.userName,
        roles: cached.roles || [],
        buttons: cached.buttons || []
      });
      return true;
    }

    clearAuthStorage();
    token.value = '';
    return false;
  }

  return {
    token,
    userInfo,
    isStaticSuper,
    isLogin,
    loginLoading,
    resetStore,
    login,
    initUserInfo
  };
});
