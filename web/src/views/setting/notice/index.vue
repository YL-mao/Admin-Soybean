<script setup lang="tsx">
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { NButton, NPopconfirm, NSwitch } from 'naive-ui';
import {
  fetchDeleteNotice,
  fetchGetNoticeConsoleStats,
  fetchGetNoticeList,
  fetchUpdateNoticeEnabled
} from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigGroupDrawer from '@/components/custom/config-group-drawer.vue';
import NoticeConsoleDrawer from './modules/notice-console-drawer.vue';
import NoticeOperateDrawer from './modules/notice-operate-drawer.vue';
import NoticeSearch from './modules/notice-search.vue';

defineOptions({ name: 'SettingNotice' });

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const { hasAuth, guardAuth } = useAuth();
const noticeConfigVisible = ref(false);
const detailVisible = ref(false);
const detailRow = ref<Api.SystemManage.Notice | null>(null);
const consoleVisible = ref(false);
const consoleNoticeId = ref('');
/** 直访探测得到的统计，交给抽屉复用，避免重复请求 */
const consolePrefetchStats = ref<Api.SystemManage.NoticeConsoleStats | null>(null);
/** 打开控制台世代号：直访异步与列表点击互斥，避免后到的请求盖掉当前抽屉 */
let consoleOpenSeq = 0;

const searchParams = ref<Api.SystemManage.NoticeSearchParams>({
  current: 1,
  size: 10,
  noticeTitle: null,
  noticeType: null,
  isSend: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () =>
    hasAuth('system:notice:select')
      ? fetchGetNoticeList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.Notice>()),
  transform: response =>
    backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    {
      key: 'noticeTitle',
      title: $t('page.setting.notice.noticeTitle'),
      align: 'center',
      minWidth: 120,
      render: row => (
        // 标题已在服务端按白名单清洗，列表里直接渲染样式
        <span class="notice-title" innerHTML={row.noticeTitle} onClick={() => openDetail(row)} />
      )
    },
    { key: 'noticeTypeName', title: $t('page.setting.notice.noticeType'), align: 'center', minWidth: 100 },
    { key: 'receiverTypeName', title: $t('page.setting.notice.receiverType'), align: 'center', minWidth: 100 },
    { key: 'orderNum', title: $t('page.setting.dict.orderNum'), align: 'center', width: 80 },
    {
      key: 'isSend',
      title: $t('page.setting.notice.publishStatus'),
      align: 'center',
      width: 100,
      render: row => (
        <NSwitch
          value={row.isSend === 1}
          rubberBand={false}
          disabled={row.isSend === 1 || !hasAuth('system:notice:updateEnabled')}
          onUpdateValue={value => handlePublish(row, value)}
        >
          {{
            checked: () => $t('page.setting.notice.published'),
            unchecked: () => $t('page.setting.notice.draft')
          }}
        </NSwitch>
      )
    },
    { key: 'sendTime', title: $t('page.setting.notice.sendTime'), align: 'center', width: 170 },
    { key: 'expireTime', title: $t('page.setting.notice.expireTime'), align: 'center', width: 170 },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 220,
      render: row => (
        <div class="flex-center gap-8px">
          {hasAuth('system:notice:console') && (
            <NButton
              type="info"
              ghost
              size="small"
              disabled={row.isSend !== 1}
              onClick={() => openConsole(row)}
            >
              {$t('page.setting.notice.console')}
            </NButton>
          )}
          {hasAuth('system:notice:update') && row.isSend !== 1 && (
            <NButton type="primary" ghost size="small" onClick={() => edit(row.noticeId)}>
              {$t('common.edit')}
            </NButton>
          )}
          {hasAuth('system:notice:delete') && (
            <NPopconfirm onPositiveClick={() => handleDelete(row.noticeId)}>
              {{
                default: () => $t('common.confirmDelete'),
                trigger: () => (
                  <NButton type="error" ghost size="small">
                    {$t('common.delete')}
                  </NButton>
                )
              }}
            </NPopconfirm>
          )}
        </div>
      )
    }
  ]
});

const { drawerVisible, operateType, editingData, handleAdd, handleEdit, checkedRowKeys, onBatchDeleted, onDeleted } =
  useTableOperate(data, 'noticeId', getData);

function edit(noticeId: string) {
  handleEdit(noticeId);
}

/** 详情展示后端洗过的正文，不把原始 HTML 直接塞进页面 */
function openDetail(row: Api.SystemManage.Notice) {
  detailRow.value = row;
  detailVisible.value = true;
}

/** 已发布公告打开控制台抽屉；草稿行按钮禁用 */
function openConsole(row: Api.SystemManage.Notice) {
  if (row.isSend !== 1 || !guardAuth('system:notice:console')) return;
  // 作废进行中的直访打开，避免 await 回来后盖掉当前点击
  consoleOpenSeq += 1;
  consolePrefetchStats.value = null;
  consoleNoticeId.value = row.noticeId;
  consoleVisible.value = true;
}

/** 列表页带 ?noticeId= 直访时，仅已发布才打开控制台并清掉 query */
async function openConsoleFromQuery(noticeId: string) {
  const seq = ++consoleOpenSeq;
  // 尽量等当前列表回来，减少无谓探测
  if (!data.value.length) {
    await getData();
  }
  if (seq !== consoleOpenSeq) {
    await clearConsoleQuery(noticeId);
    return;
  }
  const local = data.value.find(item => item.noticeId === noticeId);
  if (local && local.isSend !== 1) {
    window.$message?.warning($t('page.setting.notice.consoleUnpublishedTip'));
    await clearConsoleQuery(noticeId);
    return;
  }
  let prefetch: Api.SystemManage.NoticeConsoleStats | null = null;
  // 当前页找不到时先探测；成功则把结果交给抽屉，避免再打一遍 consoleStats
  if (!local) {
    const { data: statsData, error } = await fetchGetNoticeConsoleStats(noticeId);
    if (seq !== consoleOpenSeq) {
      await clearConsoleQuery(noticeId);
      return;
    }
    if (error || !statsData) {
      // 有 error 时请求层已弹过后端文案，避免再叠一条 tip
      if (!error) {
        window.$message?.warning($t('page.setting.notice.consoleOpenFailedTip'));
      }
      await clearConsoleQuery(noticeId);
      return;
    }
    prefetch = statsData;
  }
  if (seq !== consoleOpenSeq) {
    await clearConsoleQuery(noticeId);
    return;
  }
  consolePrefetchStats.value = prefetch;
  consoleNoticeId.value = noticeId;
  consoleVisible.value = true;
  await clearConsoleQuery(noticeId);
}

/** 清掉直访 query；传入 expectId 时仅清匹配项，避免误清后来的 noticeId */
function clearConsoleQuery(expectId?: string) {
  const current = route.query.noticeId;
  if (!current || typeof current !== 'string') return Promise.resolve();
  if (expectId && current !== expectId) return Promise.resolve();
  const query = { ...route.query };
  delete query.noticeId;
  return router.replace({ query });
}

watch(
  () => route.query.noticeId,
  async noticeId => {
    if (!noticeId || typeof noticeId !== 'string' || !noticeId.trim()) return;
    if (!hasAuth('system:notice:console')) {
      await clearConsoleQuery();
      return;
    }
    await openConsoleFromQuery(noticeId.trim());
  },
  { immediate: true }
);

/** 草稿可发布；已发布由开关禁用，不能改回草稿 */
async function handlePublish(row: Api.SystemManage.Notice, checked: boolean) {
  if (!checked || !guardAuth('system:notice:updateEnabled')) return;
  const { error } = await fetchUpdateNoticeEnabled({ noticeId: row.noticeId, isSend: 1 });
  if (error) {
    await getData();
    return;
  }
  row.isSend = 1;
  window.$message?.success($t('common.updateSuccess'));
  await getData();
}

async function handleBatchDelete() {
  const { error } = await fetchDeleteNotice(checkedRowKeys.value.join(','));
  if (error) return;
  onBatchDeleted();
}

async function handleDelete(noticeId: string) {
  const { error } = await fetchDeleteNotice(noticeId);
  if (error) return;
  onDeleted();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NoticeSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard
      :title="$t('page.setting.notice.title')"
      :bordered="false"
      size="small"
      class="card-wrapper sm:flex-1-hidden"
    >
      <template #header-extra>
        <NSpace>
          <NButton
            v-if="hasAuth('system:config:notice')"
            size="small"
            ghost
            type="primary"
            @click="noticeConfigVisible = true"
          >
            {{ $t('page.setting.notice.noticeConfig') }}
          </NButton>
          <TableHeaderOperation
            v-model:columns="columnChecks"
            :disabled-delete="checkedRowKeys.length === 0"
            :loading="loading"
            :show-add="hasAuth('system:notice:insert')"
            :show-delete="hasAuth('system:notice:delete')"
            @add="handleAdd"
            @delete="handleBatchDelete"
            @refresh="getData"
          />
        </NSpace>
      </template>
      <NDataTable
        v-model:checked-row-keys="checkedRowKeys"
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :scroll-x="1200"
        :loading="loading"
        remote
        :row-key="row => row.noticeId"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <NoticeOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
      <NoticeConsoleDrawer
        v-model:visible="consoleVisible"
        v-model:prefetched-stats="consolePrefetchStats"
        :notice-id="consoleNoticeId"
      />
      <ConfigGroupDrawer
        v-model:visible="noticeConfigVisible"
        config-group="notice"
        perm-code="system:config:notice"
        :title="$t('page.setting.notice.noticeConfig')"
      />
      <NModal v-model:show="detailVisible" preset="card" :title="$t('page.setting.notice.detailTitle')" class="w-640px">
        <!-- 标题、正文都已在服务端按白名单清洗 -->
        <!-- eslint-disable-next-line vue/no-v-html -->
        <div class="notice-html text-center text-16px font-600" v-html="detailRow?.noticeTitle || ''"></div>
        <NDivider />
        <!-- 正文已在服务端按标签和样式白名单清洗 -->
        <!-- eslint-disable-next-line vue/no-v-html -->
        <div class="notice-html" v-html="detailRow?.noticeContent || ''"></div>
      </NModal>
    </NCard>
  </div>
</template>

<style scoped>
.notice-title {
  cursor: pointer;
  line-height: 1.7;
  color: #2080f0;
  word-break: break-word;
}

.notice-title :deep(p) {
  display: inline;
  margin: 0;
}

.notice-html {
  line-height: 1.7;
  word-break: break-word;
  white-space: pre-wrap;
}

.notice-html :deep(ul),
.notice-html :deep(ol) {
  padding-left: 1.25em;
}

.notice-html :deep(h1) {
  font-size: 2em;
  font-weight: bold;
}

.notice-html :deep(h2) {
  font-size: 1.5em;
  font-weight: bold;
}

.notice-html :deep(h3) {
  font-size: 1.17em;
  font-weight: bold;
}

.notice-html :deep(h4) {
  font-size: 1em;
  font-weight: bold;
}

.notice-html :deep(h5) {
  font-size: 0.83em;
  font-weight: bold;
}

.notice-html :deep(blockquote) {
  margin: 0.5em 0;
  padding: 0.4em 0.8em;
  border-left: 4px solid #ccc;
  color: #666;
}

.notice-html :deep(a) {
  color: #2080f0;
  text-decoration: underline;
}
</style>
