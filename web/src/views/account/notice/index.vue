<script setup lang="tsx">
import { nextTick, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { NButton, NTag } from 'naive-ui';
import {
  fetchGetUserInboxNoticeList,
  fetchReadAllUserNotices,
  fetchUpdateUserNoticeRead
} from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useNoticeStore } from '@/store/modules/notice';
import { backendPageTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import UserNoticeSearch from './modules/user-notice-search.vue';

defineOptions({ name: 'AccountNotice' });

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const noticeStore = useNoticeStore();

const detailVisible = ref(false);
const detailRow = ref<Api.SystemManage.UserInboxNotice | null>(null);
const detailLoading = ref(false);
const readingAll = ref(false);

const searchParams = ref<Api.SystemManage.UserInboxNoticeSearchParams>({
  current: 1,
  size: 10,
  noticeTitle: null,
  noticeType: null,
  readState: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination, scrollX } =
  useNaivePaginatedTable({
    api: () => fetchGetUserInboxNoticeList(searchParams.value),
    transform: response =>
      backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
    onPaginationParamsChange: params => {
      searchParams.value.current = params.page || 1;
      searchParams.value.size = params.pageSize || 10;
    },
    columns: () => [
      {
        key: 'noticeTitle',
        title: $t('page.setting.notice.noticeTitle'),
        align: 'center',
        // 各列只设 minWidth、不设 width：fixed 布局下剩余宽度均分，不会出现标题列独宽
        minWidth: 160,
        render: row => (
          // 标题已在服务端按白名单清洗，列表里直接渲染样式
          <span class="notice-title" innerHTML={row.noticeTitle} onClick={() => openDetail(row)} />
        )
      },
      { key: 'noticeTypeName', title: $t('page.setting.notice.noticeType'), align: 'center', minWidth: 110 },
      {
        key: 'readState',
        title: $t('page.account.noticeReadState'),
        align: 'center',
        minWidth: 100,
        render: row =>
          row.readState === 1 ? (
            <NTag size="small">{$t('page.account.noticeRead')}</NTag>
          ) : (
            <NTag type="warning" size="small">
              {$t('page.account.noticeUnread')}
            </NTag>
          )
      },
      { key: 'sendTime', title: $t('page.setting.notice.sendTime'), align: 'center', minWidth: 170 },
      { key: 'readTime', title: $t('page.account.noticeReadTime'), align: 'center', minWidth: 170 },
      {
        key: 'operate',
        title: $t('common.operate'),
        align: 'center',
        minWidth: 100,
        render: row => (
          <NButton type="primary" ghost size="small" onClick={() => openDetail(row)}>
            {$t('page.account.noticeView')}
          </NButton>
        )
      }
    ]
  });

/** 打开详情即已读；同步顶栏未读 */
async function openDetail(row: Api.SystemManage.UserInboxNotice) {
  detailRow.value = row;
  detailVisible.value = true;
  if (row.readState === 1) return;

  detailLoading.value = true;
  const { error } = await fetchUpdateUserNoticeRead(row.noticeId);
  detailLoading.value = false;
  if (error) return;
  row.readState = 1;
  if (detailRow.value?.noticeId === row.noticeId) {
    detailRow.value = { ...detailRow.value, readState: 1 };
  }
  await noticeStore.fetchHeader();
}

async function handleReadAll() {
  readingAll.value = true;
  const { error } = await fetchReadAllUserNotices();
  readingAll.value = false;
  if (error) return;
  window.$message?.success($t('page.account.noticeReadAllSuccess'));
  await Promise.all([getData(), noticeStore.fetchHeader()]);
}

/** 从当前页或扩大拉取中找到目标公告 */
async function resolveNoticeById(noticeId: string) {
  const local = data.value.find(item => item.noticeId === noticeId);
  if (local) return local;

  const { data: rows, error } = await fetchGetUserInboxNoticeList({
    current: 1,
    size: 100,
    noticeTitle: null,
    noticeType: null,
    readState: null
  });
  if (error || !rows?.length) return null;
  return rows.find(item => item.noticeId === noticeId) || null;
}

async function openFromQuery(noticeId: string) {
  await getData();
  const target = await resolveNoticeById(noticeId);
  if (!target) {
    window.$message?.warning($t('page.account.noticeNotFound'));
    clearNoticeQuery();
    return;
  }
  await openDetail(target);
  clearNoticeQuery();
}

function clearNoticeQuery() {
  if (!route.query.noticeId) return;
  const query = { ...route.query };
  delete query.noticeId;
  router.replace({ query });
}

watch(
  () => route.query.noticeId,
  async noticeId => {
    if (!noticeId || typeof noticeId !== 'string') return;
    await nextTick();
    await openFromQuery(noticeId);
  },
  { immediate: true }
);
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <UserNoticeSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard
      :title="$t('page.account.noticeTitle')"
      :bordered="false"
      size="small"
      class="card-wrapper sm:flex-1-hidden"
    >
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default>
            <NButton size="small" ghost type="primary" :loading="readingAll" @click="handleReadAll">
              {{ $t('page.account.noticeReadAll') }}
            </NButton>
          </template>
        </TableHeaderOperation>
      </template>
      <NDataTable
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :scroll-x="scrollX"
        :loading="loading"
        remote
        paginate-single-page
        :row-key="row => row.noticeId"
        :pagination="mobilePagination"
        class="sm:h-full"
      />

      <NDrawer v-model:show="detailVisible" display-directive="if" :width="640">
        <NDrawerContent
          :title="$t('page.account.noticeDetail')"
          :native-scrollbar="false"
          closable
        >
          <NSpin :show="detailLoading">
            <!-- 标题、正文已在服务端按白名单清洗 -->
            <!-- eslint-disable-next-line vue/no-v-html -->
            <div class="notice-html text-center text-16px font-600" v-html="detailRow?.noticeTitle || ''"></div>
            <div class="mt-8px mb-12px flex-center flex-wrap gap-12px text-12px text-gray-400">
              <span>{{ detailRow?.noticeTypeName || '-' }}</span>
              <span>{{ detailRow?.sendTime || '-' }}</span>
            </div>
            <NDivider />
            <!-- 正文已在服务端按标签和样式白名单清洗 -->
            <!-- eslint-disable-next-line vue/no-v-html -->
            <div class="notice-html" v-html="detailRow?.noticeContent || ''"></div>
          </NSpin>
        </NDrawerContent>
      </NDrawer>
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
