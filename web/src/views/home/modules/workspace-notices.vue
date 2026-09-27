<script setup lang="ts">
import { computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuth } from '@/hooks/business/auth';
import { useRouterPush } from '@/hooks/common/router';
import { useAuthStore } from '@/store/modules/auth';
import { useNoticeStore } from '@/store/modules/notice';
import { useRouteStore } from '@/store/modules/route';
import { $t } from '@/locales';

defineOptions({ name: 'HomeWorkspaceNotices' });

/** 首页未读列表硬上限，防止把同行卡片与迎宾区布局撑乱 */
const HOME_NOTICE_LIMIT = 3;

const router = useRouter();
const authStore = useAuthStore();
const routeStore = useRouteStore();
const noticeStore = useNoticeStore();
const { hasAuth } = useAuth();
const { routerPushByKey } = useRouterPush();

/** 有 user:notice:select 才展示并拉公告头 */
const canView = computed(() => {
  if (!authStore.isLogin || !routeStore.isInitAuthRoute) return false;
  return hasAuth('user:notice:select');
});

/** 跳「我的公告」需对应页面路由 */
const canOpenInbox = computed(() => router.getRoutes().some(item => item.name === 'account_notice'));

/** 展平各类型未读，按发送时间降序取全局 Top N（time 为 yyyy-MM-dd HH:mm:ss） */
const noticeRows = computed(() => {
  const rows: Array<Api.SystemManage.UserNoticeHeaderItem & { typeTitle?: string }> = [];
  for (const tab of noticeStore.tabs) {
    for (const item of tab.children || []) {
      rows.push({ ...item, typeTitle: tab.title });
    }
  }
  rows.sort((a, b) => (b.time || '').localeCompare(a.time || ''));
  return rows.slice(0, HOME_NOTICE_LIMIT);
});

async function refresh() {
  if (!authStore.isLogin) {
    noticeStore.clear();
    return;
  }
  // 动态路由未就绪时不 clear，避免冲掉顶栏铃铛已拉的未读
  if (!routeStore.isInitAuthRoute) return;
  if (!hasAuth('user:notice:select')) {
    noticeStore.clear();
    return;
  }
  await noticeStore.fetchHeader();
}

async function handleReadAll() {
  const ok = await noticeStore.readAll();
  if (!ok) return;
  window.$message?.success($t('page.account.noticeReadAllSuccess'));
}

async function openNotice(noticeId: string) {
  if (!canOpenInbox.value) return;
  await routerPushByKey('account_notice', { query: { noticeId } });
}

async function goInbox() {
  if (!canOpenInbox.value) return;
  await routerPushByKey('account_notice');
}

watch(
  () => [authStore.isLogin, routeStore.isInitAuthRoute, canView.value] as const,
  ([login, inited, show]) => {
    if (!login) {
      noticeStore.clear();
      return;
    }
    if (inited && show) {
      refresh();
    }
  }
);

onMounted(() => {
  refresh();
});
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper panel-card h-full">
    <template #header>
      <div class="flex items-center gap-8px">
        <span class="panel-title-icon flex-center">
          <SvgIcon icon="mdi:bullhorn-outline" class="text-16px" />
        </span>
        <span class="text-15px font-600">{{ $t('page.home.unreadNotices') }}</span>
        <NBadge
          v-if="canView && noticeStore.unreadCount"
          :value="noticeStore.unreadCount"
          :max="99"
          class="ml-2px"
        />
      </div>
    </template>
    <template v-if="canView" #header-extra>
      <NSpace :size="4">
        <NButton
          size="tiny"
          quaternary
          type="primary"
          :disabled="!noticeStore.unreadCount"
          :loading="noticeStore.loading"
          @click="handleReadAll"
        >
          {{ $t('page.account.noticeReadAll') }}
        </NButton>
        <NButton v-if="canOpenInbox" size="tiny" quaternary @click="goInbox">
          {{ $t('page.home.viewAll') }}
        </NButton>
      </NSpace>
    </template>

    <div v-if="!canView" class="panel-empty">
      <NEmpty :description="$t('page.home.noticeNoPerm')" />
    </div>
    <NSpin v-else class="notice-spin" :show="noticeStore.loading">
      <div v-if="noticeRows.length" class="notice-list">
        <button
          v-for="item in noticeRows"
          :key="item.id"
          type="button"
          class="notice-row"
          :class="{ 'is-static': !canOpenInbox }"
          :disabled="!canOpenInbox"
          @click="openNotice(item.id)"
        >
          <div class="notice-icon flex-center shrink-0">
            <SvgIcon icon="mdi:email-mark-as-unread" class="text-16px" />
          </div>
          <div class="min-w-0 flex-1">
            <div class="notice-title truncate">{{ item.title || '-' }}</div>
            <div class="notice-meta">
              <span v-if="item.typeTitle || item.form" class="meta-tag">
                {{ item.typeTitle || item.form }}
              </span>
              <span class="meta-time">
                <SvgIcon icon="mdi:clock-outline" class="text-13px" />
                {{ item.time || '-' }}
              </span>
            </div>
          </div>
          <span v-if="canOpenInbox" class="notice-go flex-center shrink-0">
            <SvgIcon icon="mdi:chevron-right" class="text-16px" />
          </span>
        </button>
      </div>
      <div v-else class="panel-empty">
        <NEmpty :description="$t('page.account.noticeEmptyUnread')" />
      </div>
    </NSpin>
  </NCard>
</template>

<style scoped>
.panel-card {
  position: relative;
  z-index: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.panel-card :deep(.n-card-header) {
  flex-shrink: 0;
}

.panel-card :deep(.n-card__content) {
  display: flex;
  flex: 1 1 auto;
  flex-direction: column;
  min-height: 0;
  height: 100%;
}

.panel-title-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  color: rgb(var(--primary-color));
  background: color-mix(in srgb, rgb(var(--primary-color)) 12%, transparent);
}

.panel-empty {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  min-height: 140px;
}

.notice-spin {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
}

.notice-spin :deep(.n-spin-container),
.notice-spin :deep(.n-spin-content) {
  display: flex !important;
  flex: 1;
  flex-direction: column;
  min-height: 0;
}

.notice-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
  /* 不再用 max-height 截断，改由 flex 均分铺满与左侧等高 */
  overflow: hidden;
}

.notice-row {
  display: flex;
  flex: 1;
  align-items: center;
  gap: 12px;
  width: 100%;
  min-height: 0;
  padding: 12px 12px 12px 14px;
  border: 1px solid color-mix(in srgb, rgb(var(--primary-color)) 10%, transparent);
  border-left: 3px solid rgb(var(--primary-color));
  border-radius: 12px;
  background: color-mix(in srgb, rgb(var(--primary-color)) 5%, #fff);
  text-align: left;
  cursor: pointer;
  transition:
    background 0.15s ease,
    border-color 0.15s ease,
    box-shadow 0.15s ease;
}

.notice-row.is-static {
  cursor: default;
}

.notice-row:hover {
  background: color-mix(in srgb, rgb(var(--primary-color)) 9%, #fff);
  border-color: color-mix(in srgb, rgb(var(--primary-color)) 28%, transparent);
  box-shadow: 0 6px 16px color-mix(in srgb, rgb(var(--primary-color)) 12%, transparent);
}

.notice-row:hover .notice-go {
  color: #fff;
  background: rgb(var(--primary-color));
}

.notice-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  color: #fff;
  background: rgb(var(--primary-color));
}

.notice-title {
  font-size: 14px;
  font-weight: 600;
  line-height: 22px;
  color: rgb(0 0 0 / 88%);
}

.notice-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  padding: 1px 8px;
  border-radius: 999px;
  font-size: 12px;
  line-height: 18px;
  color: rgb(var(--primary-color));
  background: color-mix(in srgb, rgb(var(--primary-color)) 12%, transparent);
}

.meta-time {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  line-height: 18px;
  color: rgb(0 0 0 / 40%);
}

.notice-go {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  color: rgb(var(--primary-color));
  background: color-mix(in srgb, rgb(var(--primary-color)) 12%, transparent);
  transition:
    color 0.15s ease,
    background 0.15s ease;
}

html.dark .notice-row {
  background: color-mix(in srgb, rgb(var(--primary-color)) 12%, transparent);
  border-color: color-mix(in srgb, rgb(var(--primary-color)) 22%, transparent);
}

html.dark .notice-title {
  color: rgb(255 255 255 / 90%);
}

html.dark .meta-time {
  color: rgb(255 255 255 / 45%);
}
</style>
