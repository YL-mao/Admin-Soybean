import { ref } from 'vue';
import { defineStore } from 'pinia';
import { fetchGetUserNoticeHeader, fetchReadAllUserNotices } from '@/service/api';
import { SetupStoreId } from '@/enum';
import { useAuthStore } from '../auth';

/** 顶栏铃铛未读短列表状态：布局挂载 / 打开下拉 / 已读后刷新 */
export const useNoticeStore = defineStore(SetupStoreId.Notice, () => {
  const authStore = useAuthStore();

  const tabs = ref<Api.SystemManage.UserNoticeHeaderTab[]>([]);
  /** 后端返回的全量未读数（不是短列表条数之和） */
  const unreadCount = ref(0);
  const loading = ref(false);
  /** 丢弃过期的 header 响应（首页与顶栏并发、全部已读后刷新） */
  let fetchSeq = 0;

  async function fetchHeader() {
    const seq = ++fetchSeq;
    if (!authStore.isLogin) {
      tabs.value = [];
      unreadCount.value = 0;
      loading.value = false;
      return;
    }
    loading.value = true;
    const { data, error } = await fetchGetUserNoticeHeader();
    if (seq !== fetchSeq) return;
    loading.value = false;
    if (error) return;
    tabs.value = data?.tabs ?? [];
    unreadCount.value = data?.unreadCount ?? 0;
  }

  async function readAll() {
    if (!authStore.isLogin) return false;
    const { error } = await fetchReadAllUserNotices();
    if (error) return false;
    await fetchHeader();
    return true;
  }

  function clear() {
    // 作废在途请求，避免 clear 后又被旧响应写回
    fetchSeq += 1;
    tabs.value = [];
    unreadCount.value = 0;
    loading.value = false;
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
