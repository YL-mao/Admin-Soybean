<script setup lang="tsx">
import { ref } from 'vue';
import type { Ref } from 'vue';
import { NButton, NPopconfirm, NSwitch } from 'naive-ui';
import { useBoolean } from '@sa/hooks';
import { enabledFlagRecord } from '@/constants/business';
import { fetchDeleteDept, fetchGetDeptList, fetchUpdateDeptEnabled } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { emptyAuthListResponse, useNaiveTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import DeptOperateDrawer, { type DeptOperateType } from './modules/dept-operate-drawer.vue';
import DeptSearch from './modules/dept-search.vue';
import { buildDeptTree } from './modules/shared';

defineOptions({ name: 'OrgDept' });

const appStore = useAppStore();
const { hasAuth, guardAuth } = useAuth();
const { bool: visible, setTrue: openDrawer } = useBoolean();

const searchParams = ref<Api.SystemManage.DeptSearchParams>({
  deptName: null,
  parentId: null
});

const { columns, columnChecks, data, loading, getData } = useNaiveTable({
  api: () =>
    hasAuth('system:dept:select')
      ? fetchGetDeptList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.Dept>()),
  transform: response => {
    if (response.error || !response.data) {
      return [];
    }
    return buildDeptTree(response.data);
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    {
      key: 'deptName',
      title: $t('page.org.dept.deptName'),
      align: 'left',
      minWidth: 160
    },
    {
      key: 'orderNum',
      title: $t('page.org.dept.orderNum'),
      align: 'center',
      width: 80
    },
    {
      key: 'deptLeader',
      title: $t('page.org.dept.deptLeader'),
      align: 'center',
      width: 100
    },
    {
      key: 'leaderPhone',
      title: $t('page.org.dept.leaderPhone'),
      align: 'center',
      width: 120
    },
    {
      key: 'leaderEmail',
      title: $t('page.org.dept.leaderEmail'),
      align: 'center',
      minWidth: 140
    },
    {
      key: 'isEnabled',
      title: $t('page.manage.common.status.enable'),
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
      width: 230,
      render: row => (
        <div class="flex-center justify-end gap-8px">
          {hasAuth('system:dept:insert') && (
            <NButton type="primary" ghost size="small" onClick={() => handleAddChild(row)}>
              {$t('page.org.dept.addChildDept')}
            </NButton>
          )}
          {hasAuth('system:dept:update') && (
            <NButton type="primary" ghost size="small" onClick={() => handleEdit(row)}>
              {$t('common.edit')}
            </NButton>
          )}
          {hasAuth('system:dept:delete') && (
            <NPopconfirm onPositiveClick={() => handleDelete(row.deptId)}>
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

const { checkedRowKeys, onBatchDeleted, onDeleted } = useTableOperate(data, 'deptId', getData);

const operateType = ref<DeptOperateType>('add');
const editingData: Ref<Api.SystemManage.Dept | null> = ref(null);

function handleAdd() {
  operateType.value = 'add';
  editingData.value = null;
  openDrawer();
}

function handleEdit(item: Api.SystemManage.Dept) {
  operateType.value = 'edit';
  editingData.value = { ...item };
  openDrawer();
}

function handleAddChild(item: Api.SystemManage.Dept) {
  operateType.value = 'addChild';
  editingData.value = { ...item };
  openDrawer();
}

async function handleBatchDelete() {
  const { error } = await fetchDeleteDept(checkedRowKeys.value.join(','));
  if (error) return;
  onBatchDeleted();
}

async function handleDelete(deptId: string) {
  const { error } = await fetchDeleteDept(deptId);
  if (error) return;
  onDeleted();
}

async function handleUpdateEnabled(row: Api.SystemManage.Dept, checked: boolean) {
  if (!guardAuth('system:dept:updateEnabled')) {
    return;
  }
  const isEnabled: Api.SystemManage.EnabledFlag = checked ? 1 : 0;
  const { error } = await fetchUpdateDeptEnabled({ deptId: row.deptId, isEnabled });
  if (error) {
    await getData();
    return;
  }
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
}

function handleSearch() {
  void getData();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <DeptSearch v-model:model="searchParams" @search="handleSearch" />
    <NCard :title="$t('page.org.dept.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :disabled-delete="checkedRowKeys.length === 0"
          :loading="loading"
          :show-add="hasAuth('system:dept:insert')"
          :show-delete="hasAuth('system:dept:delete')"
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
        children-key="children"
        default-expand-all
        :flex-height="!appStore.isMobile"
        :scroll-x="1100"
        :loading="loading"
        :row-key="row => row.deptId"
        class="sm:h-full"
      >
        <template #empty>
          <NEmpty
            :description="
              hasAuth('system:dept:select') ? $t('common.noData') : $t('common.noPermission')
            "
          />
        </template>
      </NDataTable>
      <DeptOperateDrawer
        v-model:visible="visible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getData"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
