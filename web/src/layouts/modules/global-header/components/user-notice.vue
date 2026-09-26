<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuth } from '@/hooks/business/auth';
import { useRouterPush } from '@/hooks/common/router';
import { useAuthStore } from '@/store/modules/auth';
import { useRouteStore } from '@/store/modules/route';
import { useNoticeStore } from '@/store/modules/notice';
import { $t } from '@/locales';

defineOptions({ name: 'UserNoticeBell' });

const router = useRouter();
const authStore = useAuthStore();
const routeStore = useRouteStore();
const { hasAuth } = useAuth();
const { routerPushByKey } = useRouterPush();
const noticeStore = useNoticeStore();

const showPopover = ref(false);
const activeTab = ref<number | string | undefined>(undefined);

/** 有 user:notice:select 才显示铃铛并拉头；跳收件箱另看路由 */
const visible = computed(() => {
  if (!authStore.isLogin || !routeStore.isInitAuthRoute) return false;
  return hasAuth('user:notice:select');
});

const canOpenInbox = computed(() => router.getRoutes().some(item => item.name === 'account_notice'));

const tabPanes = computed(() => noticeStore.tabs);

const activePane = computed(() => tabPanes.value.find(tab => tab.id === activeTab.value) || null);

watch(
  tabPanes,
  panes => {
    if (!panes.length) {
      activeTab.value = undefined;
      return;
    }
    if (activeTab.value == null || !panes.some(tab => tab.id === activeTab.value)) {
      activeTab.value = panes[0].id;
    }
  },
  { immediate: true }
);

async function refreshHeader() {
  if (!authStore.isLogin) {
    noticeStore.clear();
    return;
  }
  // 动态路由未就绪时不 clear，避免首页/铃铛互相冲掉未读
  if (!routeStore.isInitAuthRoute) return;
  if (!hasAuth('user:notice:select')) {
    noticeStore.clear();
    return;
  }
  await noticeStore.fetchHeader();
}

async function handlePopoverUpdate(show: boolean) {
  showPopover.value = show;
  if (show) {
    await refreshHeader();
  }
}

/** 点未读标题：跳我的公告并打开该条 */
async function openNotice(noticeId: string) {
  if (!canOpenInbox.value) return;
  showPopover.value = false;
  await routerPushByKey('account_notice', { query: { noticeId } });
}

async function handleReadAll() {
  const ok = await noticeStore.readAll();
  if (!ok) return;
  window.$message?.success($t('page.autobox.account.noticeReadAllSuccess'));
}

async function goInbox() {
  if (!canOpenInbox.value) return;
  showPopover.value = false;
  await routerPushByKey('account_notice');
}

watch(
  () => [authStore.isLogin, routeStore.isInitAuthRoute, visible.value] as const,
  ([login, inited, canShow]) => {
    if (!login) {
      noticeStore.clear();
      return;
    }
    if (inited && canShow) {
      refreshHeader();
    }
  }
);

onMounted(() => {
  refreshHeader();
});
</script>

<template>
  <!-- trigger 必须是可定位 DOM；勿套 ButtonIcon（内含 NTooltip，锚点会落到左上角） -->
  <NPopover
    v-if="visible"
    :show="showPopover"
    trigger="click"
    placement="bottom-end"
    :width="460"
    display-directive="show"
    @update:show="handlePopoverUpdate"
  >
    <template #trigger>
      <NButton quaternary class="h-36px text-icon">
        <NBadge :value="noticeStore.unreadCount" :max="99" :show-zero="false">
          <!-- 喇叭字形留白偏多，略大于 text-icon 与顶栏其它图标视觉对齐 -->
          <SvgIcon icon="mdi:bullhorn-outline" class="text-[1.25rem]" />
        </NBadge>
      </NButton>
    </template>

    <div class="user-notice-popover">
      <div class="mb-8px flex items-center justify-between px-4px">
        <span class="text-14px font-600">{{ $t('page.autobox.account.noticeTitle') }}</span>
        <NButton text type="primary" size="tiny" :disabled="!noticeStore.unreadCount" @click="handleReadAll">
          {{ $t('page.autobox.account.noticeReadAll') }}
        </NButton>
      </div>

      <NSpin :show="noticeStore.loading">
        <template v-if="tabPanes.length">
          <!-- 自绘 Tab：四类均分整行，空隙匀开，避开 NTabs 默认 36px 间距 -->
          <div class="notice-type-tabs" role="tablist">
            <button
              v-for="tab in tabPanes"
              :key="tab.id"
              type="button"
              role="tab"
              class="notice-type-tab"
              :class="{ 'is-active': tab.id === activeTab }"
              :aria-selected="tab.id === activeTab"
              @click="activeTab = tab.id"
            >
              {{ tab.title }}({{ tab.children.length }})
            </button>
          </div>

          <div class="notice-type-pane">
            <div v-if="activePane?.children.length" class="max-h-280px overflow-auto">
              <button
                v-for="item in activePane.children"
                :key="item.id"
                type="button"
                class="notice-item"
                :disabled="!canOpenInbox"
                @click="openNotice(item.id)"
              >
                <span class="truncate text-13px">{{ item.title || '-' }}</span>
                <span v-if="item.time" class="mt-2px text-12px text-gray-400">{{ item.time }}</span>
              </button>
            </div>
            <NEmpty v-else class="py-16px" :description="$t('page.autobox.account.noticeEmptyUnread')" size="small" />
          </div>
        </template>
        <NEmpty v-else class="py-16px" :description="$t('page.autobox.account.noticeEmptyUnread')" size="small" />
      </NSpin>

      <div v-if="canOpenInbox" class="mt-8px border-t border-gray-100 pt-8px text-center dark:border-gray-700">
        <NButton text type="primary" size="small" @click="goInbox">
          {{ $t('page.autobox.account.noticeViewAll') }}
        </NButton>
      </div>
    </div>
  </NPopover>
</template>

<style scoped>
.notice-type-tabs {
  display: flex;
  width: 100%;
  border-bottom: 1px solid rgb(0 0 0 / 8%);
}

html.dark .notice-type-tabs {
  border-bottom-color: rgb(255 255 255 / 12%);
}

.notice-type-tab {
  flex: 1 1 0;
  min-width: 0;
  padding: 8px 4px;
  border: 0;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  background: transparent;
  color: var(--n-text-color-3, #9ca3af);
  cursor: pointer;
  font-size: 13px;
  line-height: 1.4;
  text-align: center;
  white-space: nowrap;
}

.notice-type-tab.is-active {
  border-bottom-color: rgb(var(--primary-color));
  color: rgb(var(--primary-color));
  font-weight: 600;
}

.notice-type-pane {
  padding-top: 8px;
}

.notice-item {
  display: flex;
  width: 100%;
  flex-direction: column;
  align-items: flex-start;
  padding: 10px 8px;
  border: 0;
  border-radius: 6px;
  background: transparent;
  cursor: pointer;
  text-align: left;
}

.notice-item:hover {
  background: rgb(0 0 0 / 4%);
}

html.dark .notice-item:hover {
  background: rgb(255 255 255 / 6%);
}
</style>
