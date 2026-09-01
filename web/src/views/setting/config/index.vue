<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NTag } from 'naive-ui';
import { enableStatusRecord } from '@/constants/business';
import { fetchConfigItemList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { defaultTransform, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigOperateDrawer from './modules/config-operate-drawer.vue';
import ConfigSearch from './modules/config-search.vue';

defineOptions({ name: 'SettingConfig' });

const appStore = useAppStore();

const searchParams = ref<Api.AutoboxScaffold.ConfigSearchParams>({
  current: 1,
  size: 10,
  configGroup: null,
  configName: null,
  configCode: null,
  status: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchConfigItemList(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    { key: 'configGroup', title: $t('page.autobox.config.configGroup'), align: 'center', width: 100 },
    { key: 'configName', title: $t('page.autobox.config.configName'), align: 'center', minWidth: 120 },
    { key: 'configCode', title: $t('page.autobox.config.configCode'), align: 'center', minWidth: 160 },
    { key: 'configValue', title: $t('page.autobox.config.configValue'), align: 'center', minWidth: 120 },
    { key: 'valueType', title: $t('page.autobox.config.valueType'), align: 'center', width: 90 },
    {
      key: 'isBuiltin',
      title: $t('page.autobox.config.isBuiltin'),
      align: 'center',
      width: 80,
      render: row => (row.isBuiltin === 1 ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
    },
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
    { key: 'orderNum', title: $t('page.autobox.config.orderNum'), align: 'center', width: 70 },
    { key: 'configDesc', title: $t('page.autobox.config.configDesc'), align: 'center', minWidth: 140 },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 100,
      render: row => (
        <NButton type="primary" ghost size="small" onClick={() => edit(row.id)}>
          {$t('common.edit')}
        </NButton>
      )
    }
  ]
});

const { drawerVisible, operateType, editingData, handleEdit, checkedRowKeys, onBatchDeleted } = useTableOperate(
  data,
  'id',
  getData
);

function edit(id: number) {
  handleEdit(id);
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ConfigSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard
      :title="$t('page.autobox.config.title')"
      :bordered="false"
      size="small"
      class="card-wrapper sm:flex-1-hidden"
    >
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :disabled-delete="checkedRowKeys.length === 0"
          :loading="loading"
          @delete="onBatchDeleted"
          @refresh="getData"
        >
          <template #default />
        </TableHeaderOperation>
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
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <ConfigOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
    </NCard>
  </div>
</template>
