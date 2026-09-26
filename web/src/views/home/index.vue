<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGetUserProfileDetail } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuthStore } from '@/store/modules/auth';
import { useRouteStore } from '@/store/modules/route';
import WorkspaceGreeting from './modules/workspace-greeting.vue';
import WorkspaceNotices from './modules/workspace-notices.vue';
import WorkspaceShortcuts from './modules/workspace-shortcuts.vue';
import WorkspaceLoginLogs from './modules/workspace-login-logs.vue';
import WorkspaceProfileSummary from './modules/workspace-profile-summary.vue';

defineOptions({ name: 'HomePage' });

const router = useRouter();
const appStore = useAppStore();
const authStore = useAuthStore();
const routeStore = useRouteStore();

const gap = computed(() => (appStore.isMobile ? 0 : 16));

/** 问候条与账号摘要共用：有个人中心路由才拉一次详情 */
const canLoadProfile = computed(() => {
  if (!authStore.isLogin || !routeStore.isInitAuthRoute) return false;
  return router.getRoutes().some(item => item.name === 'account_info');
});

const profile = ref<Api.SystemManage.UserProfileDetail | null>(null);
const profileLoading = ref(false);
/** 防并发回写乱序 */
let profileLoadSeq = 0;

async function loadProfile() {
  const seq = ++profileLoadSeq;
  if (!canLoadProfile.value) {
    profile.value = null;
    profileLoading.value = false;
    return;
  }
  profileLoading.value = true;
  const { data, error } = await fetchGetUserProfileDetail();
  if (seq !== profileLoadSeq) return;
  profileLoading.value = false;
  if (error || !data) {
    profile.value = null;
    return;
  }
  profile.value = data;
}

watch(canLoadProfile, () => {
  loadProfile();
});

onMounted(() => {
  loadProfile();
});
</script>

<template>
  <div class="workspace-page min-h-500px flex-col gap-16px">
    <!--
      页面级 Transition 要求单根元素。HTML 注释若写在本 div 外，会与根节点并列成多根，
      进出动画异常并可能导致全局白屏；注释只能写在本根节点内部。
      上下两行独立栅格：避免同行等高/层叠把迎宾区盖住。
    -->
    <WorkspaceGreeting class="workspace-greeting" :profile="profile" />
    <NGrid class="workspace-grid" :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
      <NGi span="24 s:24 m:10" class="row-cell flex">
        <WorkspaceShortcuts class="w-full" />
      </NGi>
      <NGi span="24 s:24 m:14" class="row-cell flex">
        <WorkspaceNotices class="w-full" />
      </NGi>
    </NGrid>
    <NGrid class="workspace-grid" :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
      <NGi span="24 s:24 m:10" class="row-cell flex">
        <WorkspaceProfileSummary
          class="w-full"
          :profile="profile"
          :loading="profileLoading"
          :can-view="canLoadProfile"
        />
      </NGi>
      <NGi span="24 s:24 m:14" class="row-cell flex">
        <WorkspaceLoginLogs class="w-full" />
      </NGi>
    </NGrid>
  </div>
</template>

<style scoped>
.workspace-page {
  position: relative;
  padding-bottom: 8px;
}

/* 迎宾不被下方栅格盖住，也不被外层 flex 压扁裁切 */
.workspace-greeting {
  position: relative;
  z-index: 2;
  flex-shrink: 0;
}

/* NGrid 根即本节点；同行格子默认 stretch，子卡片用 flex 吃满高度 */
.workspace-grid {
  position: relative;
  z-index: 1;
  align-items: stretch;
}

.row-cell {
  display: flex !important;
  flex-direction: column;
  align-items: stretch;
  min-height: 0;
}

/* 百分比高度在 grid 拉伸项上不可靠，改为 flex:1 吃满格子 */
.row-cell > * {
  flex: 1 1 auto;
  width: 100%;
  min-height: 0;
}
</style>
