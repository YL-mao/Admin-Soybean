<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NTag } from 'naive-ui';
import { enableStatusRecord } from '@/constants/business';
import { fetchFilterList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { defaultTransform, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigGroupDrawer from '@/components/custom/config-group-drawer.vue';
import FilterOperateDrawer from './modules/filter-operate-drawer.vue';
import FilterSearch from './modules/filter-search.vue';

defineOptions({ name: 'OpsFilter' });

const appStore = useAppStore();
const { hasAuth } = useAuth();
const securityConfigVisible = ref(false);

const searchParams = ref<Api.AutoboxScaffold.FilterSearchParams>({
  current: 1,
  size: 10,
  valueLabel: null,
  filterTypeName: null,
  status: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchFilterList(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    { key: 'policyModeName', title: $t('page.autobox.filter.policyMode'), align: 'center', width: 100 },
    { key: 'filterTypeName', title: $t('page.autobox.filter.filterType'), align: 'center', width: 100 },
    { key: 'valueLabel', title: $t('page.autobox.filter.valueLabel'), align: 'center', minWidth: 140 },
    { key: 'filterSourceName', title: $t('page.autobox.filter.filterSource'), align: 'center', width: 100 },
    { key: 'filterDesc', title: $t('page.autobox.filter.filterDesc'), align: 'center', minWidth: 120 },
    { key: 'expireTime', title: $t('page.autobox.filter.expireTime'), align: 'center', width: 170 },
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
    { key: 'createTime', title: $t('page.autobox.filter.createTime'), align: 'center', width: 170 },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 130,
      render: row => (
        <div class="flex-center gap-8px">
          <NButton type="primary" ghost size="small" onClick={() => edit(row.id)}>
            {$t('common.edit')}
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDeleteRow()}>
            {{
              default: () => $t('common.confirmDelete'),
              trigger: () => (
                <NButton type="error" ghost size="small">
                  {$t('common.delete')}
                </NButton>
              )
            }}
          </NPopconfirm>
        </div>
      )
    }
  ]
});

const { drawerVisible, operateType, editingData, handleAdd, handleEdit, checkedRowKeys, onBatchDeleted, onDeleted } =
  useTableOperate(data, 'id', getData);

function edit(id: number) {
  handleEdit(id);
}

function handleDeleteRow() {
  onDeleted();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <FilterSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard
      :title="$t('page.autobox.filter.title')"
      :bordered="false"
      size="small"
      class="card-wrapper sm:flex-1-hidden"
    >
      <template #header-extra>
        <NSpace>
          <NButton
            v-if="hasAuth('system:config:security')"
            size="small"
            ghost
            type="primary"
            @click="securityConfigVisible = true"
          >
            {{ $t('page.autobox.filter.securityConfig') }}
          </NButton>
          <TableHeaderOperation
            v-model:columns="columnChecks"
            :disabled-delete="checkedRowKeys.length === 0"
            :loading="loading"
            @add="handleAdd"
            @delete="onBatchDeleted"
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
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <FilterOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
      <ConfigGroupDrawer
        v-model:visible="securityConfigVisible"
        config-group="security"
        perm-code="system:config:security"
        :title="$t('page.autobox.filter.securityConfig')"
      />
    </NCard>
  </div>
</template>
