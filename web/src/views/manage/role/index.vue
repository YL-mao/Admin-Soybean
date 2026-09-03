<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NSwitch } from 'naive-ui';
import { enabledFlagRecord } from '@/constants/business';
import { fetchDeleteRole, fetchGetRoleList, fetchUpdateRoleEnabled } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { backendPageTransform, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import RoleOperateDrawer from './modules/role-operate-drawer.vue';
import RoleSearch from './modules/role-search.vue';

const appStore = useAppStore();

const searchParams = ref<Api.SystemManage.RoleSearchParams>({
  current: 1,
  size: 10,
  roleName: null
});

const { columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchGetRoleList(searchParams.value),
  transform: response =>
    backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    {
      type: 'selection',
      align: 'center',
      width: 48
    },
    {
      key: 'index',
      title: $t('common.index'),
      width: 64,
      align: 'center',
      render: (_, index) => index + 1
    },
    {
      key: 'roleName',
      title: $t('page.manage.role.roleName'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'roleCode',
      title: $t('page.manage.role.roleCode'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'orderNum',
      title: $t('page.manage.role.orderNum'),
      align: 'center',
      width: 80
    },
    {
      key: 'isEnabled',
      title: $t('page.manage.role.roleStatus'),
      align: 'center',
      width: 100,
      render: row => (
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
      )
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 130,
      render: row => (
        <div class="flex-center gap-8px">
          <NButton type="primary" ghost size="small" onClick={() => edit(row.roleId)}>
            {$t('common.edit')}
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.roleId)}>
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

const {
  drawerVisible,
  operateType,
  editingData,
  handleAdd,
  handleEdit,
  checkedRowKeys,
  onBatchDeleted,
  onDeleted
} = useTableOperate(data, 'roleId', getData);

async function handleBatchDelete() {
  const { error } = await fetchDeleteRole(checkedRowKeys.value.join(','));
  if (error) return;
  onBatchDeleted();
}

async function handleDelete(roleId: string) {
  const { error } = await fetchDeleteRole(roleId);
  if (error) return;
  onDeleted();
}

/** 列表开关直接启停 */
async function handleUpdateEnabled(row: Api.SystemManage.Role, checked: boolean) {
  const isEnabled: Api.SystemManage.EnabledFlag = checked ? 1 : 0;
  const { error } = await fetchUpdateRoleEnabled({ roleId: row.roleId, isEnabled });
  if (error) {
    await getData();
    return;
  }
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
}

function edit(roleId: string) {
  handleEdit(roleId);
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <RoleSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard :title="$t('page.manage.role.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :disabled-delete="checkedRowKeys.length === 0"
          :loading="loading"
          @add="handleAdd"
          @delete="handleBatchDelete"
          @refresh="getData"
        />
      </template>
      <NDataTable
        v-model:checked-row-keys="checkedRowKeys"
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :scroll-x="702"
        :loading="loading"
        remote
        :row-key="row => row.roleId"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <RoleOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
