<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NSwitch, NTag } from 'naive-ui';
import { enabledFlagRecord } from '@/constants/business';
import { fetchGetJobList, fetchRunJob, fetchUpdateJobEnabled } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigGroupDrawer from '@/components/custom/config-group-drawer.vue';
import JobLogDrawer from './modules/job-log-drawer.vue';
import JobSearch from './modules/job-search.vue';

defineOptions({ name: 'OpsJob' });

const appStore = useAppStore();
const { hasAuth } = useAuth();
const jobConfigVisible = ref(false);

const logDrawerVisible = ref(false);
const logJobId = ref('');
const logJobName = ref('');
const logJobCode = ref('');

const searchParams = ref<Api.SystemManage.JobSearchParams>({
  current: 1,
  size: 10,
  jobName: null,
  jobCode: null,
  isEnabled: null
});

function runStatusTagType(runStatus: string | null): NaiveUI.ThemeColor {
  if (runStatus === 'SUCCESS') return 'success';
  if (runStatus === 'FAILED') return 'error';
  if (runStatus === 'SKIPPED') return 'warning';
  return 'default';
}

function openLogDrawer(row: Api.SystemManage.Job) {
  logJobId.value = row.jobId;
  logJobName.value = row.jobName;
  logJobCode.value = row.jobCode;
  logDrawerVisible.value = true;
}

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () =>
    hasAuth('system:job:select')
      ? fetchGetJobList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.Job>()),
  transform: response =>
    backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { key: 'jobName', title: $t('page.ops.job.jobName'), align: 'center', minWidth: 140 },
    { key: 'jobCode', title: $t('page.ops.job.jobCode'), align: 'center', minWidth: 140 },
    { key: 'jobCronDesc', title: $t('page.ops.job.jobCronDesc'), align: 'center', minWidth: 120 },
    { key: 'jobDesc', title: $t('page.ops.job.jobDesc'), align: 'center', minWidth: 180 },
    {
      key: 'isEnabled',
      title: $t('page.manage.common.status.enable'),
      align: 'center',
      width: 100,
      render: row => {
        if (!hasAuth('system:job:updateEnabled')) {
          return (
            <NTag type={row.isEnabled === 1 ? 'success' : 'warning'}>
              {$t(enabledFlagRecord[row.isEnabled])}
            </NTag>
          );
        }
        return (
          <NSwitch
            value={row.isEnabled === 1}
            rubberBand={false}
            onUpdateValue={value => handleUpdateEnabled(row, value)}
          >
            {{
              checked: () => $t(enabledFlagRecord[1]),
              unchecked: () => $t(enabledFlagRecord[0])
            }}
          </NSwitch>
        );
      }
    },
    { key: 'lastRunTime', title: $t('page.ops.job.lastRunTime'), align: 'center', minWidth: 160 },
    {
      key: 'runStatusName',
      title: $t('page.ops.job.runStatus'),
      align: 'center',
      width: 100,
      render: row => <NTag type={runStatusTagType(row.runStatus)}>{row.runStatusName}</NTag>
    },
    { key: 'nextRunTime', title: $t('page.ops.job.nextRunTime'), align: 'center', minWidth: 160 },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 200,
      fixed: 'right',
      render: row => (
        <div class="flex-center gap-8px">
          {hasAuth('system:job:log') && (
            <NButton size="small" ghost type="info" onClick={() => openLogDrawer(row)}>
              {$t('page.ops.job.viewLog')}
            </NButton>
          )}
          {hasAuth('system:job:run') && (
            <NPopconfirm onPositiveClick={() => handleRunOnce(row)}>
              {{
                default: () => $t('page.ops.job.runOnceConfirm'),
                trigger: () => (
                  <NButton size="small" ghost type="primary">
                    {$t('page.ops.job.runOnce')}
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

async function handleUpdateEnabled(row: Api.SystemManage.Job, checked: boolean) {
  const isEnabled = checked ? 1 : 0;
  // 启停立即通知调度器取消/重建触发。
  const { error } = await fetchUpdateJobEnabled({ jobId: row.jobId, isEnabled });
  if (error) return;
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
  getData();
}

async function handleRunOnce(row: Api.SystemManage.Job) {
  // 手动执行不受启停限制，与后端约定一致。
  const { error } = await fetchRunJob({ jobId: row.jobId });
  if (error) return;
  window.$message?.success($t('page.ops.job.runOnceSuccess'));
  getData();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <JobSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard :title="$t('page.ops.job.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
      <template #header-extra>
        <NSpace>
          <NButton
            v-if="hasAuth('system:config:job')"
            size="small"
            ghost
            type="primary"
            @click="jobConfigVisible = true"
          >
            {{ $t('page.ops.job.jobConfig') }}
          </NButton>
          <TableHeaderOperation
            v-model:columns="columnChecks"
            :show-add="false"
            :show-delete="false"
            :loading="loading"
            @refresh="getData"
          />
        </NSpace>
      </template>
      <NDataTable
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :scroll-x="1300"
        :loading="loading"
        remote
        :row-key="row => row.jobId"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <JobLogDrawer
        v-model:visible="logDrawerVisible"
        :job-id="logJobId"
        :job-name="logJobName"
        :job-code="logJobCode"
      />
      <ConfigGroupDrawer
        v-model:visible="jobConfigVisible"
        config-group="job"
        perm-code="system:config:job"
        :title="$t('page.ops.job.jobConfig')"
      />
    </NCard>
  </div>
</template>
