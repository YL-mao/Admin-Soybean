import { useAuthStore } from '@/store/modules/auth';
import { $t } from '@/locales';

export function useAuth() {
  const authStore = useAuthStore();

  function hasAuth(codes: string | string[]) {
    if (!authStore.isLogin) {
      return false;
    }

    if (typeof codes === 'string') {
      return authStore.userInfo.buttons.includes(codes);
    }

    return codes.some(code => authStore.userInfo.buttons.includes(code));
  }

  /** 有权限返回 true；无权限提示并返回 false（用于可展示但需拦截的开关等） */
  function guardAuth(codes: string | string[]) {
    if (hasAuth(codes)) {
      return true;
    }
    window.$message?.warning($t('common.noPermission'));
    return false;
  }

  return {
    hasAuth,
    guardAuth
  };
}
