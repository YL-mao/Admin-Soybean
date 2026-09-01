<script setup lang="tsx">

import { ref } from 'vue';

import { NButton, NPopconfirm, NTag } from 'naive-ui';

import { enableStatusRecord } from '@/constants/business';

import { fetchPostList } from '@/service/api';

import { useAppStore } from '@/store/modules/app';

import { defaultTransform, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';

import { $t } from '@/locales';

import PostSearch from './modules/post-search.vue';

import PostOperateDrawer from './modules/post-operate-drawer.vue';



defineOptions({ name: 'OrgPost' });



const appStore = useAppStore();



const searchParams = ref<Api.AutoboxScaffold.PostSearchParams>({

  current: 1,

  size: 10,

  postName: null,

  postCode: null,

  status: null

});



const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({

  api: () => fetchPostList(searchParams.value),

  transform: response => defaultTransform(response),

  onPaginationParamsChange: params => {

    searchParams.value.current = params.page;

    searchParams.value.size = params.pageSize;

  },

  columns: () => [

    { type: 'selection', align: 'center', width: 48 },

    {

      key: 'index',

      title: $t('common.index'),

      align: 'center',

      width: 64,

      render: (_, index) => index + 1

    },

    {

      key: 'postName',

      title: $t('page.autobox.post.postName'),

      align: 'center',

      minWidth: 120

    },

    {

      key: 'postCode',

      title: $t('page.autobox.post.postCode'),

      align: 'center',

      minWidth: 120

    },

    {

      key: 'postTypeName',

      title: $t('page.autobox.post.postType'),

      align: 'center',

      width: 100

    },

    {

      key: 'orderNum',

      title: $t('page.autobox.post.orderNum'),

      align: 'center',

      width: 80

    },

    {

      key: 'status',

      title: $t('page.manage.common.status.enable'),

      align: 'center',

      width: 100,

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

      width: 130,

      render: row => (

        <div class="flex-center gap-8px">

          <NButton type="primary" ghost size="small" onClick={() => edit(row.id)}>

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



const {

  drawerVisible,

  operateType,

  editingData,

  handleAdd,

  handleEdit,

  checkedRowKeys,

  onBatchDeleted,

  onDeleted

} = useTableOperate(data, 'id', getData);



async function handleBatchDelete() {

  onBatchDeleted();

}



function handleDelete(_id: number) {

  onDeleted();

}



function edit(id: number) {

  handleEdit(id);

}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <PostSearch v-model:model="searchParams" @search="getDataByPage" />

    <NCard :title="$t('page.autobox.post.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
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
        :scroll-x="962"
        :loading="loading"
        remote
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />

      <PostOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
