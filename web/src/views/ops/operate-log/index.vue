<script setup lang="tsx">
import { ref } from 'vue';
import { useBoolean } from '@sa/hooks';
import { NButton, NTag } from 'naive-ui';
import { mockLoginLogs } from '@/mock/autobox/data';
import { fetchOperateLogList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { defaultTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import OperateLogSearch from './modules/operate-log-search.vue';

defineOptions({ name: 'OpsOperateLog' });

const appStore = useAppStore();
const activeTab = ref<'operate' | 'login' | 'config'>('operate');
const { bool: showDetail, setTrue: openDetail } = useBoolean();
const { bool: showLogConfig, setTrue: openLogConfig } = useBoolean();

const searchParams = ref<Api.AutoboxScaffold.OperateLogSearchParams>({
  current: 1,
  size: 10,
  operateTitle: null,
  businessType: null,
  operateName: null,
  operateIp: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchOperateLogList(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { key: 'operateTitle', title: $t('page.autobox.operateLog.operateTitle'), align: 'center', minWidth: 120 },
    { key: 'businessType', title: $t('page.autobox.operateLog.businessType'), align: 'center', width: 100 },
    { key: 'requestMethod', title: $t('page.autobox.operateLog.requestMethod'), align: 'center', width: 90 },
    { key: 'requestUri', title: $t('page.autobox.operateLog.requestUri'), align: 'center', minWidth: 160 },
    { key: 'browser', title: $t('page.autobox.operateLog.browser'), align: 'center', width: 90 },
    { key: 'operateIp', title: $t('page.autobox.operateLog.operateIp'), align: 'center', width: 120 },
    { key: 'systemOs', title: $t('page.autobox.operateLog.systemOs'), align: 'center', width: 100 },
    { key: 'operateTime', title: $t('page.autobox.operateLog.operateTime'), align: 'center', width: 170 },
    { key: 'operateName', title: $t('page.autobox.operateLog.operateName'), align: 'center', width: 90 },
    { key: 'costTime', title: $t('page.autobox.operateLog.costTime'), align: 'center', width: 90 },
    {
      key: 'status',
      title: $t('page.autobox.operateLog.visitStatus'),
      align: 'center',
      width: 100,
      render: row => (
        <NTag type={row.isSuccess === 1 ? 'success' : 'error'}>
          {row.isSuccess === 1 ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no')}
        </NTag>
      )
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 90,
      render: () => (
        <NButton size="small" ghost type="primary" onClick={() => openDetail()}>
          {$t('page.autobox.operateLog.detail')}
        </NButton>
      )
    }
  ]
});

const loginColumns = [
  { key: 'operateTitle', title: $t('page.autobox.operateLog.operateTitle'), align: 'center' as const },
  { key: 'requestMethod', title: $t('page.autobox.operateLog.requestMethod'), align: 'center' as const, width: 90 },
  { key: 'requestUri', title: $t('page.autobox.operateLog.requestUri'), align: 'center' as const, minWidth: 120 },
  { key: 'browser', title: $t('page.autobox.operateLog.browser'), align: 'center' as const, width: 90 },
  { key: 'operateIp', title: $t('page.autobox.operateLog.operateIp'), align: 'center' as const, width: 120 },
  { key: 'operateTime', title: $t('page.autobox.operateLog.operateTime'), align: 'center' as const, width: 170 },
  { key: 'operateName', title: $t('page.autobox.operateLog.operateName'), align: 'center' as const, width: 90 }
];

const configAuditRows = ref([
  {
    configCode: 'sys.name',
    configName: 'sys.name',
    action: 'UPDATE',
    operateName: 'admin',
    operateTime: '2026-06-20 10:00:00'
  }
]);
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <OperateLogSearch v-model:model="searchParams" @search="getDataByPage" />

    <NCard :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
      <template #header-extra>
        <NSpace>
          <NButton size="small" @click="openLogConfig">{{ $t('page.autobox.operateLog.logConfig') }}</NButton>
          <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
            <template #default />
          </TableHeaderOperation>
        </NSpace>
      </template>
      <NTabs v-model:value="activeTab" type="line">
        <NTabPane name="operate" :tab="$t('page.autobox.operateLog.tabOperate')">
          <NDataTable
            :columns="columns"
            :data="data"
            size="small"
            :scroll-x="1500"
            :flex-height="!appStore.isMobile"
            :loading="loading"
            remote
            :row-key="row => row.id"
            :pagination="mobilePagination"
            class="sm:h-420px"
          />
        </NTabPane>
        <NTabPane name="login" :tab="$t('page.autobox.operateLog.tabLogin')">
          <NDataTable :columns="loginColumns" :data="mockLoginLogs" size="small" :scroll-x="1100" class="sm:h-420px" />
        </NTabPane>
        <NTabPane name="config" :tab="$t('page.autobox.operateLog.tabConfig')">
          <NDataTable
            :columns="[
              { key: 'configCode', title: $t('page.autobox.config.configCode'), align: 'center' },
              { key: 'configName', title: $t('page.autobox.config.configName'), align: 'center' },
              { key: 'action', title: 'Action', align: 'center', width: 90 },
              { key: 'operateName', title: $t('page.autobox.operateLog.operateName'), align: 'center', width: 100 },
              { key: 'operateTime', title: $t('page.autobox.operateLog.operateTime'), align: 'center', width: 170 }
            ]"
            :data="configAuditRows"
            size="small"
            class="sm:h-420px"
          />
        </NTabPane>
      </NTabs>
    </NCard>

    <NModal v-model:show="showDetail" preset="card" :title="$t('page.autobox.operateLog.detailTitle')" class="w-640px">
      <NDescriptions bordered :column="1" label-placement="left">
        <NDescriptionsItem :label="$t('page.autobox.operateLog.operateTitle')">demo</NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.requestUri')">GET /user/listView</NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.operateName')">admin</NDescriptionsItem>
      </NDescriptions>
    </NModal>

    <NDrawer v-model:show="showLogConfig" :width="360">
      <NDrawerContent :title="$t('page.autobox.operateLog.logConfig')" closable>
        <NForm label-placement="left" :label-width="120">
          <NFormItem :label="$t('page.autobox.operateLog.tabLogin')"><NSwitch :default-value="true" /></NFormItem>
          <NFormItem :label="$t('page.autobox.operateLog.tabOperate')"><NSwitch :default-value="true" /></NFormItem>
        </NForm>
      </NDrawerContent>
    </NDrawer>
  </div>
</template>
