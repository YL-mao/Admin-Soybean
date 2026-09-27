<script setup lang="tsx">
import { computed, ref, watch } from 'vue';
import { NTag } from 'naive-ui';
import { fetchGetJobLogList } from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import JobLogSearch from './job-log-search.vue';

defineOptions({ name: 'JobLogDrawer' });

interface Props {
  jobId: string;
  jobName?: string;
  jobCode?: string;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', { default: false });

const { hasAuth } = useAuth();

const searchParams = ref<Api.SystemManage.JobLogSearchParams>({
  current: 1,
  size: 10,
  jobId: null,
  runStatus: null
});

function runStatusTagType(runStatus: string | null): NaiveUI.ThemeColor {
  if (runStatus === 'SUCCESS') return 'success';
  if (runStatus === 'FAILED') return 'error';
  if (runStatus === 'SKIPPED') return 'warning';
  return 'default';
}

const drawerTitle = computed(() => {
  const base = $t('page.ops.jobLog.title');
  return props.jobName ? `${base}（${props.jobName}）` : base;
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => {
    if (!visible.value || !props.jobId || !hasAuth('system:job:log')) {
      return Promise.resolve(emptyAuthListResponse<Api.SystemManage.JobLog>());
    }
    return fetchGetJobLogList({ ...searchParams.value, jobId: props.jobId });
  },
  transform: response =>
    backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { key: 'startTime', title: $t('page.ops.jobLog.startTime'), align: 'center', width: 170 },
    { key: 'endTime', title: $t('page.ops.jobLog.endTime'), align: 'center', width: 170 },
    {
      key: 'runStatusName',
      title: $t('page.ops.job.runStatus'),
      align: 'center',
      width: 100,
      render: row => <NTag type={runStatusTagType(row.runStatus)}>{row.runStatusName}</NTag>
    },
    { key: 'triggerTypeName', title: $t('page.ops.jobLog.triggerType'), align: 'center', width: 100 },
    { key: 'costMs', title: $t('page.ops.jobLog.costMs'), align: 'center', width: 100 },
    {
      key: 'message',
      title: $t('page.ops.jobLog.message'),
      align: 'center',
      minWidth: 180,
      ellipsis: { tooltip: true }
    }
  ]
});

watch(
  () => (visible.value ? props.jobId : ''),
  id => {
    if (!id) return;
    // 打开或切换任务时重置筛选并拉对应日志（抽屉不关直接点另一行也会刷新）。
    searchParams.value = {
      current: 1,
      size: 10,
      jobId: id,
      runStatus: null
    };
    getDataByPage();
  }
);
</script>

<template>
  <NDrawer v-model:show="visible" :width="960" display-directive="if">
    <NDrawerContent :title="drawerTitle" :native-scrollbar="false" closable body-content-style="padding: 0">
      <!-- 抽屉内复用 Soybean 列表布局：上搜索卡 + 下表格卡 -->
      <div class="h-[calc(100vh-108px)] flex-col-stretch gap-16px overflow-hidden p-16px">
        <JobLogSearch
          v-model:model="searchParams"
          :job-name="jobName"
          :job-code="jobCode"
          @search="getDataByPage"
        />
        <NCard
          :title="$t('page.ops.jobLog.title')"
          :bordered="false"
          size="small"
          class="card-wrapper sm:flex-1-hidden"
        >
          <template #header-extra>
            <TableHeaderOperation
              v-model:columns="columnChecks"
              :show-add="false"
              :show-delete="false"
              :loading="loading"
              @refresh="getData"
            />
          </template>
          <NDataTable
            :columns="columns"
            :data="data"
            size="small"
            flex-height
            :scroll-x="900"
            :loading="loading"
            remote
            :row-key="row => row.jobLogId"
            :pagination="mobilePagination"
            class="sm:h-full"
          />
        </NCard>
      </div>
    </NDrawerContent>
  </NDrawer>
</template>
