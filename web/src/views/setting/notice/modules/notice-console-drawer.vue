<script setup lang="tsx">
import { computed, ref, watch } from 'vue';
import { NTag } from 'naive-ui';
import { fetchGetNoticeConsoleReceivers, fetchGetNoticeConsoleStats } from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import NoticeConsoleReceiverSearch from './notice-console-receiver-search.vue';

defineOptions({ name: 'NoticeConsoleDrawer' });

interface Props {
  /** 当前公告 ID；打开抽屉后拉统计与接收人 */
  noticeId: string;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', { default: false });
/** 直访探测已拉过的统计；与 noticeId 匹配时直接用，消费后清空 */
const prefetchedStats = defineModel<Api.SystemManage.NoticeConsoleStats | null>('prefetchedStats', {
  default: null
});

const { hasAuth } = useAuth();

/** 接收人请求过期（关抽屉 / 换公告）时抛 AbortError，由 use-table 静默丢弃 */
function abortReceiverStale() {
  const err = new Error('NOTICE_CONSOLE_RECEIVER_STALE');
  err.name = 'AbortError';
  throw err;
}

const statsLoading = ref(false);
const stats = ref<Api.SystemManage.NoticeConsoleStats | null>(null);
/** 统计与接收人分世代，避免互相 bump 导致 statsLoading 卡死 */
let statsSeq = 0;
let receiverSeq = 0;

const receiverSearch = ref<Api.SystemManage.NoticeConsoleReceiverSearchParams>({
  current: 1,
  size: 10,
  noticeId: null,
  readState: null
});

const {
  columns: receiverColumns,
  columnChecks: receiverColumnChecks,
  data: receiverRows,
  getData: getReceivers,
  getDataByPage: getReceiversByPage,
  loading: receiversLoading,
  pagination: receiverPaginationState,
  mobilePagination: receiverPagination,
  scrollX: receiverScrollX
} = useNaivePaginatedTable({
  immediate: false,
  api: async () => {
    const seq = ++receiverSeq;
    const reqNoticeId = props.noticeId;
    if (!visible.value || !reqNoticeId || !hasAuth('system:notice:console')) {
      return emptyAuthListResponse<Api.SystemManage.NoticeConsoleReceiver>();
    }
    const result = await fetchGetNoticeConsoleReceivers({
      ...receiverSearch.value,
      noticeId: reqNoticeId
    });
    // 已换公告/关抽屉：AbortError 丢弃，避免盖住新数据
    if (seq !== receiverSeq || !visible.value || props.noticeId !== reqNoticeId) {
      abortReceiverStale();
    }
    return result;
  },
  transform: response =>
    backendPageTransform(response, receiverSearch.value.current || 1, receiverSearch.value.size || 10),
  onPaginationParamsChange: params => {
    receiverSearch.value.current = params.page || 1;
    receiverSearch.value.size = params.pageSize || 10;
  },
  columns: () => [
    { key: 'userAccount', title: $t('page.setting.notice.consoleUserAccount'), align: 'center', minWidth: 120 },
    { key: 'userName', title: $t('page.setting.notice.consoleUserName'), align: 'center', minWidth: 100 },
    { key: 'deptName', title: $t('page.setting.notice.consoleDeptName'), align: 'center', minWidth: 120 },
    {
      key: 'readState',
      title: $t('page.setting.notice.consoleReadState'),
      align: 'center',
      width: 100,
      render: (row: Api.SystemManage.NoticeConsoleReceiver) =>
        row.readState === 1 ? (
          <NTag type="success" size="small">
            {$t('page.setting.notice.consoleRead')}
          </NTag>
        ) : (
          <NTag type="warning" size="small">
            {$t('page.setting.notice.consoleUnread')}
          </NTag>
        )
    },
    {
      key: 'readTime',
      title: $t('page.setting.notice.consoleReadTime'),
      align: 'center',
      width: 170,
      render: (row: Api.SystemManage.NoticeConsoleReceiver) => row.readTime || '-'
    }
  ]
});

/** 阅读率百分比数值，供环形进度使用 */
const readRatePercent = computed(() => {
  const raw = stats.value?.readRate;
  if (!raw) return 0;
  const n = Number.parseFloat(String(raw).replace('%', ''));
  return Number.isFinite(n) ? Math.min(100, Math.max(0, n)) : 0;
});

/** 仅指定角色/部门有接收对象名；全体与指定个人为空，不展示该项 */
const targetText = computed(() => {
  if (!stats.value?.receiverTargets?.length) return '';
  return stats.value.receiverTargets.join('、');
});

async function loadStats(noticeId: string, seq: number) {
  statsLoading.value = true;
  try {
    const { data, error } = await fetchGetNoticeConsoleStats(noticeId);
    if (seq !== statsSeq) return 'stale' as const;
    if (error) {
      stats.value = null;
      return false;
    }
    stats.value = data;
    return true;
  } finally {
    // 仅本世代收转圈；过期请求也要关，避免 spinner 卡死
    if (seq === statsSeq) {
      statsLoading.value = false;
    }
  }
}

watch(
  () => (visible.value ? props.noticeId : ''),
  async id => {
    if (!id) {
      // 关闭时作废在途统计/接收人请求，防止关抽屉后仍写入
      statsSeq += 1;
      receiverSeq += 1;
      statsLoading.value = false;
      stats.value = null;
      receiverRows.value = [];
      receiverPaginationState.itemCount = 0;
      return;
    }
    const seq = ++statsSeq;
    receiverSeq += 1;
    // 先清空旧统计与接收人，避免转圈下仍显示上一条
    stats.value = null;
    receiverRows.value = [];
    receiverPaginationState.itemCount = 0;
    receiverSearch.value = {
      current: 1,
      size: 10,
      noticeId: id,
      readState: null
    };

    let ok: boolean | 'stale' = false;
    // 直访探测结果可复用，避免打开后再打一次 consoleStats
    if (prefetchedStats.value?.noticeId === id) {
      stats.value = prefetchedStats.value;
      prefetchedStats.value = null;
      statsLoading.value = false;
      ok = true;
    } else {
      ok = await loadStats(id, seq);
    }
    if (ok === 'stale' || seq !== statsSeq) {
      return;
    }
    if (!ok) {
      visible.value = false;
      return;
    }
    await loadReceiversSafe();
  }
);

/** 过期请求由 use-table 按 AbortError / 世代丢弃 */
async function loadReceiversSafe() {
  await getReceiversByPage();
}

function handleReceiverSearch() {
  void loadReceiversSafe();
}
</script>

<template>
  <NDrawer v-model:show="visible" display-directive="if" :width="860">
    <NDrawerContent
      :title="$t('page.setting.notice.console')"
      :native-scrollbar="false"
      closable
      body-content-style="padding: 0"
    >
      <NSpin :show="statsLoading" class="h-full">
        <div
          v-if="stats"
          class="h-[calc(100vh-108px)] flex-col-stretch gap-16px overflow-hidden p-16px"
        >
          <!-- 顶部摘要：对齐个人中心头图气质，用主题色铺底 -->
          <div class="console-hero shrink-0">
            <div class="flex items-center gap-12px">
              <div class="console-hero-icon flex-center shrink-0">
                <SvgIcon icon="mdi:bullhorn-outline" class="text-22px" />
              </div>
              <!-- 标题独占上行 -->
              <!-- eslint-disable-next-line vue/no-v-html -->
              <div
                class="notice-html min-w-0 flex-1 text-18px font-600 leading-28px"
                v-html="stats.noticeTitle || '-'"
              ></div>
            </div>
            <!-- 标签 + 对象 + 时间：整行铺在标题下方（原时间行位置） -->
            <div class="mt-12px flex flex-nowrap items-center gap-12px overflow-x-auto text-13px">
              <NTag size="small" round :bordered="false" class="hero-tag shrink-0">
                {{ stats.noticeTypeName || '-' }}
              </NTag>
              <NTag size="small" round :bordered="false" class="hero-tag shrink-0">
                {{ stats.receiverTypeName || '-' }}
              </NTag>
              <div v-if="targetText" class="hero-meta shrink-0">
                <SvgIcon icon="mdi:account-group-outline" class="text-15px opacity-80" />
                <span class="opacity-80">{{ $t('page.setting.notice.consoleTargets') }}</span>
                <span class="max-w-200px truncate font-500">{{ targetText }}</span>
              </div>
              <div class="hero-meta shrink-0">
                <SvgIcon icon="mdi:clock-outline" class="text-15px opacity-80" />
                <span class="opacity-80">{{ $t('page.setting.notice.sendTime') }}</span>
                <span class="font-500">{{ stats.sendTime || '-' }}</span>
              </div>
              <div class="hero-meta shrink-0">
                <SvgIcon icon="mdi:timer-sand" class="text-15px opacity-80" />
                <span class="opacity-80">{{ $t('page.setting.notice.expireTime') }}</span>
                <span class="font-500">{{ stats.expireTime || '-' }}</span>
              </div>
            </div>
          </div>

          <!-- 阅读指标：环形阅读率 + 三指标 -->
          <div class="metric-row shrink-0">
            <div class="metric-rate flex-center flex-col gap-8px">
              <NProgress
                type="circle"
                :percentage="readRatePercent"
                :stroke-width="8"
                :offset-degree="0"
                class="metric-circle"
              >
                <div class="flex-col-center">
                  <span class="text-22px font-700 text-primary">{{ stats.readRate || '0%' }}</span>
                  <span class="mt-2px text-12px text-gray-400">{{ $t('page.setting.notice.consoleReadRate') }}</span>
                </div>
              </NProgress>
            </div>
            <div class="metric-list">
              <div class="metric-item">
                <div class="metric-icon metric-icon--total">
                  <SvgIcon icon="mdi:account-multiple-outline" class="text-18px" />
                </div>
                <div class="min-w-0">
                  <div class="text-12px text-gray-400">{{ $t('page.setting.notice.consoleTotal') }}</div>
                  <div class="mt-2px text-22px font-700">{{ stats.totalCount }}</div>
                </div>
              </div>
              <div class="metric-item">
                <div class="metric-icon metric-icon--read">
                  <SvgIcon icon="mdi:check-circle-outline" class="text-18px" />
                </div>
                <div class="min-w-0">
                  <div class="text-12px text-gray-400">{{ $t('page.setting.notice.consoleReadCount') }}</div>
                  <div class="mt-2px text-22px font-700 text-success">{{ stats.readCount }}</div>
                </div>
              </div>
              <div class="metric-item">
                <div class="metric-icon metric-icon--unread">
                  <SvgIcon icon="mdi:email-outline" class="text-18px" />
                </div>
                <div class="min-w-0">
                  <div class="text-12px text-gray-400">{{ $t('page.setting.notice.consoleUnreadCount') }}</div>
                  <div class="mt-2px text-22px font-700 text-warning">{{ stats.unreadCount }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 接收人明细：Soybean 统一列表 = 上搜索卡 + 下表格卡 -->
          <NoticeConsoleReceiverSearch v-model:model="receiverSearch" @search="handleReceiverSearch" />
          <NCard
            :title="$t('page.setting.notice.consoleReceiversTab')"
            :bordered="false"
            size="small"
            class="card-wrapper sm:flex-1-hidden"
          >
            <template #header-extra>
              <TableHeaderOperation
                v-model:columns="receiverColumnChecks"
                :show-add="false"
                :show-delete="false"
                :loading="receiversLoading"
                @refresh="getReceivers"
              />
            </template>
            <NDataTable
              size="small"
              :columns="receiverColumns"
              :data="receiverRows"
              :loading="receiversLoading"
              remote
              paginate-single-page
              :row-key="row => row.userId"
              :pagination="receiverPagination"
              :scroll-x="receiverScrollX"
              flex-height
              class="sm:h-full"
            />
          </NCard>
        </div>
        <div v-else class="flex-center py-80px">
          <NEmpty />
        </div>
      </NSpin>
    </NDrawerContent>
  </NDrawer>
</template>

<style scoped>
.notice-html {
  line-height: 1.55;
  word-break: break-word;
}

.notice-html :deep(p) {
  display: inline;
  margin: 0;
}

/* 头图深色底：洗掉标题内联深色，避免看不清 */
.console-hero .notice-html,
.console-hero .notice-html :deep(*) {
  color: #fff !important;
  background-color: transparent !important;
}

.console-hero {
  padding: 18px 18px 16px;
  border-radius: 10px;
  color: #fff;
  background: linear-gradient(
    135deg,
    rgb(var(--primary-color)) 0%,
    color-mix(in srgb, rgb(var(--primary-color)) 72%, #1f2937) 100%
  );
}

.console-hero-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgb(255 255 255 / 18%);
}

.hero-tag {
  background: rgb(255 255 255 / 20%) !important;
  color: #fff !important;
}

.hero-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  opacity: 0.95;
}

.metric-row {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 12px;
  align-items: stretch;
}

.metric-rate,
.metric-item {
  border-radius: 10px;
  background: rgb(0 0 0 / 2.5%);
  border: 1px solid rgb(0 0 0 / 5%);
}

.metric-rate {
  padding: 12px 8px;
}

.metric-circle {
  width: 112px;
  height: 112px;
}

.metric-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.metric-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 14px;
}

.metric-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.metric-icon--total {
  color: rgb(var(--primary-color));
  background: color-mix(in srgb, rgb(var(--primary-color)) 14%, transparent);
}

.metric-icon--read {
  color: #18a058;
  background: rgb(24 160 88 / 12%);
}

.metric-icon--unread {
  color: #f0a020;
  background: rgb(240 160 32 / 12%);
}

html.dark .metric-rate,
html.dark .metric-item {
  background: rgb(255 255 255 / 4%);
  border-color: rgb(255 255 255 / 8%);
}

@media (max-width: 640px) {
  .metric-row {
    grid-template-columns: 1fr;
  }

  .metric-list {
    grid-template-columns: 1fr;
  }
}
</style>
