import { computed, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';
import { defineStore } from 'pinia';
import { useLoading } from '@sa/hooks';
import { fetchGetUserInfo, fetchLogin, fetchLogout } from '@/service/api';
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
    userAvatar: '',
    roles: [],
    buttons: []
  });

  /** 头像覆盖后路径不变时用来bust缓存；顶栏与个人中心共用 */
  const avatarStamp = ref(Date.now());

  /** is super role in static route */
  const isStaticSuper = computed(() => {
    const { VITE_AUTH_ROUTE_MODE, VITE_STATIC_SUPER_ROLE } = import.meta.env;

    return VITE_AUTH_ROUTE_MODE === 'static' && userInfo.roles.includes(VITE_STATIC_SUPER_ROLE);
  });

  /** Is login */
  const isLogin = computed(() => Boolean(token.value));

  /** 防止并发 401 重复 reset / 重复打注销 */
  let resetting = false;

  /** Reset auth store */
  async function resetStore() {
    if (resetting) {
      return;
    }
    resetting = true;
    try {
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
    } finally {
      resetting = false;
    }
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
      const pass = await loginByToken(loginToken);

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

  /** 落地 token 后拉 userInfo（roles/buttons），严格 Header 模式 */
  async function loginByToken(loginToken: Api.Auth.LoginToken) {
    if (!loginToken?.token) {
      return false;
    }

    localStg.set('token', loginToken.token);
    token.value = loginToken.token;

    const pass = await getUserInfo();
    if (!pass) {
      clearAuthStorage();
      token.value = '';
      return false;
    }

    return true;
  }

  /** 从后端刷新用户信息到内存（不落 localStorage，刷新靠 token + 再请求） */
  async function getUserInfo() {
    const { data: info, error } = await fetchGetUserInfo();
    if (error || !info) {
      return false;
    }

    Object.assign(userInfo, {
      userId: info.userId,
      userName: info.userName,
      userAvatar: info.userAvatar || '',
      roles: info.roles || [],
      buttons: info.buttons || []
    });
    return true;
  }

  /** 个人中心改昵称/头像后立刻同步顶栏，不必整页重登 */
  function syncProfile(partial: { userName?: string; userAvatar?: string | null }) {
    if (partial.userName !== undefined) {
      userInfo.userName = partial.userName;
    }
    if (partial.userAvatar !== undefined) {
      userInfo.userAvatar = partial.userAvatar || '';
      avatarStamp.value = Date.now();
    }
  }

  /** 对外：角色授权变更后刷新 roles/buttons */
  async function refreshUserInfo() {
    if (!getToken()) {
      return false;
    }
    return getUserInfo();
  }

  /** 刷新恢复：有 token 则打 userInfo 校验会话并刷新角色/按钮 */
  async function initUserInfo() {
    const maybeToken = getToken();

    if (!maybeToken) {
      clearAuthStorage();
      token.value = '';
      return false;
    }

    token.value = maybeToken;
    const pass = await getUserInfo();
    if (!pass) {
      clearAuthStorage();
      token.value = '';
      return false;
    }
    return true;
  }

  return {
    token,
    userInfo,
    avatarStamp,
    isStaticSuper,
    isLogin,
    loginLoading,
    resetStore,
    login,
    initUserInfo,
    refreshUserInfo,
    syncProfile
  };
});
