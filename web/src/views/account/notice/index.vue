<script setup lang="tsx">
import { ref } from 'vue';
import { fetchNoticeList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { defaultTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import NoticeSearch from '@/views/setting/notice/modules/notice-search.vue';
defineOptions({ name: 'AccountNotice' });

const appStore = useAppStore();

const searchParams = ref<Api.SystemManage.NoticeSearchParams>({
  current: 1,
  size: 10,
  noticeTitle: null,
  noticeType: null,
  isSend: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchNoticeList(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { key: 'noticeTitle', title: $t('page.autobox.notice.noticeTitle'), align: 'center', minWidth: 200 },
    { key: 'noticeTypeName', title: $t('page.autobox.notice.noticeType'), align: 'center', width: 100 },
    { key: 'sendTime', title: $t('page.autobox.notice.sendTime'), align: 'center', width: 170 },
    { key: 'expireTime', title: $t('page.autobox.notice.expireTime'), align: 'center', width: 170 }
  ]
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NoticeSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard
      :title="$t('page.autobox.account.noticeTitle')"
      :bordered="false"
      size="small"
      class="card-wrapper sm:flex-1-hidden"
    >
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default />
        </TableHeaderOperation>
      </template>
      <NDataTable
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :loading="loading"
        remote
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
    </NCard>
  </div>
</template>
