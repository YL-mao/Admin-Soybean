import { computed, ref } from 'vue';
import { defineStore } from 'pinia';
import { fetchGetUserNoticeHeader, fetchReadAllUserNotices } from '@/service/api';
import { SetupStoreId } from '@/enum';
import { useAuthStore } from '../auth';

/** 顶栏铃铛未读短列表状态：布局挂载 / 打开下拉 / 已读后刷新 */
export const useNoticeStore = defineStore(SetupStoreId.Notice, () => {
  const authStore = useAuthStore();

  const tabs = ref<Api.SystemManage.UserNoticeHeaderTab[]>([]);
  const loading = ref(false);

  const unreadCount = computed(() =>
    tabs.value.reduce((sum, tab) => sum + (tab.children?.length || 0), 0)
  );

  async function fetchHeader() {
    if (!authStore.isLogin) {
      tabs.value = [];
      return;
    }
    loading.value = true;
    const { data, error } = await fetchGetUserNoticeHeader();
    loading.value = false;
    if (error) return;
    tabs.value = data ?? [];
  }

  async function readAll() {
    if (!authStore.isLogin) return false;
    const { error } = await fetchReadAllUserNotices();
    if (error) return false;
    await fetchHeader();
    return true;
  }

  function clear() {
    tabs.value = [];
  }

  return {
    tabs,
    loading,
    unreadCount,
    fetchHeader,
    readAll,
    clear
  };
});
