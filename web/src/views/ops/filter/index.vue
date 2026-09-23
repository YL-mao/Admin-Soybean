<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NSwitch, NTag } from 'naive-ui';
import { enabledFlagRecord } from '@/constants/business';
import {
  fetchDeleteFilter,
  fetchGetFilterList,
  fetchUpdateFilterEnabled
} from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigGroupDrawer from '@/components/custom/config-group-drawer.vue';
import FilterOperateDrawer from './modules/filter-operate-drawer.vue';
import FilterSearch from './modules/filter-search.vue';

defineOptions({ name: 'OpsFilter' });

const appStore = useAppStore();
const { hasAuth } = useAuth();
const securityConfigVisible = ref(false);

const searchParams = ref<Api.SystemManage.FilterSearchParams>({
  current: 1,
  size: 10,
  filterType: null,
  filterValue: null,
  filterSource: null,
  policyMode: null,
  isEnabled: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () =>
    hasAuth('system:filter:select')
      ? fetchGetFilterList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.Filter>()),
  transform: response =>
    backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    { key: 'policyModeName', title: $t('page.autobox.filter.policyMode'), align: 'center' },
    { key: 'filterTypeName', title: $t('page.autobox.filter.filterType'), align: 'center' },
    { key: 'valueLabel', title: $t('page.autobox.filter.valueLabel'), align: 'center' },
    { key: 'filterSourceName', title: $t('page.autobox.filter.filterSource'), align: 'center' },
    { key: 'filterDesc', title: $t('page.autobox.filter.filterDesc'), align: 'center' },
    {
      key: 'expireTime',
      title: $t('page.autobox.filter.expireTime'),
      align: 'center',
      render: row => (row.permanent === 1 ? $t('page.autobox.filter.permanent') : row.expireTime)
    },
    {
      key: 'isEnabled',
      title: $t('page.manage.common.status.enable'),
      align: 'center',
      render: row => {
        if (!hasAuth('system:filter:updateEnabled')) {
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
    { key: 'createTime', title: $t('page.autobox.filter.createTime'), align: 'center' },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 130,
      render: row => (
        <div class="flex-center gap-8px">
          {hasAuth('system:filter:update') && (
            <NButton type="primary" ghost size="small" onClick={() => edit(row.filterId)}>
              {$t('common.edit')}
            </NButton>
          )}
          {hasAuth('system:filter:delete') && (
            <NPopconfirm onPositiveClick={() => handleDelete(row.filterId)}>
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
  useTableOperate(data, 'filterId', getData);

function edit(filterId: string) {
  handleEdit(filterId);
}

async function handleBatchDelete() {
  const { error } = await fetchDeleteFilter(checkedRowKeys.value.join(','));
  if (error) return;
  onBatchDeleted();
}

async function handleDelete(filterId: string) {
  const { error } = await fetchDeleteFilter(filterId);
  if (error) return;
  onDeleted();
}

async function handleUpdateEnabled(row: Api.SystemManage.Filter, checked: boolean) {
  const isEnabled = checked ? 1 : 0;
  const { error } = await fetchUpdateFilterEnabled({ filterId: row.filterId, isEnabled });
  if (error) return;
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
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
            :show-add="hasAuth('system:filter:insert')"
            :show-delete="hasAuth('system:filter:delete')"
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
        :loading="loading"
        remote
        :row-key="row => row.filterId"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <div v-if="!hasAuth('system:filter:select')" class="py-24px text-center text-gray-400">
        {{ $t('common.noPermission') }}
      </div>
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
