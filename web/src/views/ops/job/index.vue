<script setup lang="tsx">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { NButton, NTag } from 'naive-ui';
import { enableStatusRecord } from '@/constants/business';
import { fetchJobList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { defaultTransform, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigGroupDrawer from '@/components/custom/config-group-drawer.vue';
import JobOperateDrawer from './modules/job-operate-drawer.vue';
import JobSearch from './modules/job-search.vue';

defineOptions({ name: 'OpsJob' });

const appStore = useAppStore();
const router = useRouter();
const { hasAuth } = useAuth();
const jobConfigVisible = ref(false);

const searchParams = ref<Api.AutoboxScaffold.JobSearchParams>({
  current: 1,
  size: 10,
  jobName: null,
  jobCode: null,
  status: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchJobList(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    { key: 'jobName', title: $t('page.autobox.job.jobName'), align: 'center', minWidth: 140 },
    { key: 'jobCode', title: $t('page.autobox.job.jobCode'), align: 'center', minWidth: 120 },
    { key: 'jobCronDesc', title: $t('page.autobox.job.jobCronDesc'), align: 'center', minWidth: 120 },
    { key: 'jobDesc', title: $t('page.autobox.job.jobDesc'), align: 'center', minWidth: 160 },
    {
      key: 'status',
      title: $t('page.manage.common.status.enable'),
      align: 'center',
      width: 90,
      render: row => {
        if (row.status === null) return null;
        const tagMap: Record<Api.Common.EnableStatus, NaiveUI.ThemeColor> = { '1': 'success', '2': 'warning' };
        return <NTag type={tagMap[row.status]}>{$t(enableStatusRecord[row.status])}</NTag>;
      }
    },
    { key: 'lastRunTime', title: $t('page.autobox.job.lastRunTime'), align: 'center', minWidth: 160 },
    {
      key: 'runStatus',
      title: $t('page.autobox.job.runStatus'),
      align: 'center',
      width: 100,
      render: row => <NTag type="success">{row.runStatus}</NTag>
    },
    { key: 'nextRunTime', title: $t('page.autobox.job.nextRunTime'), align: 'center', minWidth: 160 },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 220,
      fixed: 'right',
      render: row => (
        <div class="flex-center gap-8px">
          <NButton
            size="small"
            ghost
            type="info"
            onClick={() =>
              router.push({
                name: 'ops_job-log',
                query: { jobId: row.jobId, jobName: row.jobName, jobCode: row.jobCode }
              })
            }
          >
            {$t('page.autobox.job.viewLog')}
          </NButton>
          <NButton size="small" ghost type="primary">
            {$t('page.autobox.job.runOnce')}
          </NButton>
          <NButton size="small" ghost type="primary" onClick={() => edit(row.id)}>
            {$t('common.edit')}
          </NButton>
        </div>
      )
    }
  ]
});

const { drawerVisible, operateType, editingData, handleAdd, handleEdit, checkedRowKeys, onBatchDeleted } =
  useTableOperate(data, 'id', getData);

function edit(id: number) {
  handleEdit(id);
}

async function handleBatchDelete() {
  onBatchDeleted();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <JobSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard :title="$t('page.autobox.job.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
      <template #header-extra>
        <NSpace>
          <NButton
            v-if="hasAuth('system:config:job')"
            size="small"
            ghost
            type="primary"
            @click="jobConfigVisible = true"
          >
            {{ $t('page.autobox.job.jobConfig') }}
          </NButton>
          <TableHeaderOperation
            v-model:columns="columnChecks"
            :disabled-delete="checkedRowKeys.length === 0"
            :loading="loading"
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
        :scroll-x="1400"
        :loading="loading"
        remote
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <JobOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
      <ConfigGroupDrawer
        v-model:visible="jobConfigVisible"
        config-group="job"
        perm-code="system:config:job"
        :title="$t('page.autobox.job.jobConfig')"
      />
    </NCard>
  </div>
</template>
