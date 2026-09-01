<script setup lang="tsx">
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { NTag } from 'naive-ui';
import { fetchJobLogList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { defaultTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import JobLogSearch from './modules/job-log-search.vue';

defineOptions({ name: 'OpsJobLog' });

const route = useRoute();
const appStore = useAppStore();

const jobId = computed(() => String(route.query.jobId || ''));
const jobName = computed(() => String(route.query.jobName || ''));
const jobCode = computed(() => String(route.query.jobCode || ''));

const searchParams = ref<Api.AutoboxScaffold.JobLogSearchParams>({
  current: 1,
  size: 10,
  jobId: jobId.value || null,
  runStatus: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchJobLogList({ ...searchParams.value, jobId: jobId.value || searchParams.value.jobId }),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { key: 'startTime', title: $t('page.autobox.jobLog.startTime'), align: 'center', width: 170 },
    { key: 'endTime', title: $t('page.autobox.jobLog.endTime'), align: 'center', width: 170 },
    {
      key: 'runStatus',
      title: $t('page.autobox.job.runStatus'),
      align: 'center',
      width: 100,
      render: row => <NTag type="success">{row.runStatus}</NTag>
    },
    { key: 'triggerType', title: $t('page.autobox.jobLog.triggerType'), align: 'center', width: 100 },
    { key: 'costMs', title: $t('page.autobox.jobLog.costMs'), align: 'center', width: 100 },
    { key: 'message', title: $t('page.autobox.jobLog.message'), align: 'center', minWidth: 200 }
  ]
});

const cardTitle = computed(() => {
  const base = $t('page.autobox.jobLog.title');
  return jobName.value ? `${base}（${jobName.value}）` : base;
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <JobLogSearch v-model:model="searchParams" :job-name="jobName" :job-code="jobCode" @search="getDataByPage" />
    <NCard :title="cardTitle" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
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
        :scroll-x="900"
        :loading="loading"
        remote
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
    </NCard>
  </div>
</template>
