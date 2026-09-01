<script setup lang="tsx">

import { ref } from 'vue';

import type { Ref } from 'vue';

import { NButton, NPopconfirm, NTag } from 'naive-ui';

import { useBoolean } from '@sa/hooks';

import { enableStatusRecord } from '@/constants/business';

import { fetchDeptTreeList } from '@/service/api';

import { useAppStore } from '@/store/modules/app';

import { defaultTransform, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';

import { $t } from '@/locales';

import DeptOperateDrawer, { type DeptOperateType } from './modules/dept-operate-drawer.vue';

import DeptSearch from './modules/dept-search.vue';



defineOptions({ name: 'OrgDept' });



const appStore = useAppStore();

const { bool: visible, setTrue: openDrawer } = useBoolean();



const searchParams = ref<Api.AutoboxScaffold.DeptSearchParams>({

  current: 1,

  size: 100,

  deptName: null,

  status: null

});



const { columns, columnChecks, data, loading, getData, getDataByPage } = useNaivePaginatedTable({

  api: () => fetchDeptTreeList(searchParams.value),

  transform: response => defaultTransform(response),

  columns: () => [

    { type: 'selection', align: 'center', width: 48 },

    {

      key: 'deptName',

      title: $t('page.autobox.dept.deptName'),

      align: 'left',

      minWidth: 160

    },

    {

      key: 'orderNum',

      title: $t('page.autobox.dept.orderNum'),

      align: 'center',

      width: 80

    },

    {

      key: 'deptLeader',

      title: $t('page.autobox.dept.deptLeader'),

      align: 'center',

      width: 100

    },

    {

      key: 'leaderPhone',

      title: $t('page.autobox.dept.leaderPhone'),

      align: 'center',

      width: 120

    },

    {

      key: 'leaderEmail',

      title: $t('page.autobox.dept.leaderEmail'),

      align: 'center',

      minWidth: 140

    },

    {

      key: 'status',

      title: $t('page.manage.common.status.enable'),

      align: 'center',

      width: 90,

      render: row => {

        if (row.status === null) return null;

        const tagMap: Record<Api.Common.EnableStatus, NaiveUI.ThemeColor> = {

          '1': 'success',

          '2': 'warning'

        };

        return <NTag type={tagMap[row.status]}>{$t(enableStatusRecord[row.status])}</NTag>;

      }

    },

    {

      key: 'operate',

      title: $t('common.operate'),

      align: 'center',

      width: 230,

      render: row => (

        <div class="flex-center justify-end gap-8px">

          <NButton type="primary" ghost size="small" onClick={() => handleAddChild(row)}>

            {$t('page.autobox.dept.addChildDept')}

          </NButton>

          <NButton type="primary" ghost size="small" onClick={() => handleEdit(row)}>

            {$t('common.edit')}

          </NButton>

          <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>

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



const { checkedRowKeys, onBatchDeleted, onDeleted } = useTableOperate(data, 'id', getData);



const operateType = ref<DeptOperateType>('add');

const editingData: Ref<Api.AutoboxScaffold.Dept | null> = ref(null);



function handleAdd() {

  operateType.value = 'add';

  editingData.value = null;

  openDrawer();

}



function handleEdit(item: Api.AutoboxScaffold.Dept) {

  operateType.value = 'edit';

  editingData.value = { ...item };

  openDrawer();

}



function handleAddChild(item: Api.AutoboxScaffold.Dept) {

  operateType.value = 'addChild';

  editingData.value = { ...item };

  openDrawer();

}



async function handleBatchDelete() {

  onBatchDeleted();

}



function handleDelete(_id: number) {

  onDeleted();

}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <DeptSearch v-model:model="searchParams" @search="getDataByPage" />

    <NCard :title="$t('page.autobox.dept.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
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
        children-key="children"
        default-expand-all
        :flex-height="!appStore.isMobile"
        :scroll-x="1100"
        :loading="loading"
        :row-key="row => row.id"
        class="sm:h-full"
      />

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
