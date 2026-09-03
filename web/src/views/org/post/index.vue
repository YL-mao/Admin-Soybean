<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NSwitch } from 'naive-ui';
import { enabledFlagRecord } from '@/constants/business';
import { fetchDeletePost, fetchGetPostList, fetchUpdatePostEnabled } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import PostSearch from './modules/post-search.vue';
import PostOperateDrawer from './modules/post-operate-drawer.vue';

defineOptions({ name: 'OrgPost' });

const appStore = useAppStore();
const { hasAuth, guardAuth } = useAuth();

const searchParams = ref<Api.SystemManage.PostSearchParams>({
  current: 1,
  size: 10,
  postName: null,
  postCode: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () =>
    hasAuth('system:post:select')
      ? fetchGetPostList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.Post>()),
  transform: response =>
    backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
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
      width: 130,
      render: row => (
        <div class="flex-center gap-8px">
          {hasAuth('system:post:update') && (
            <NButton type="primary" ghost size="small" onClick={() => edit(row.postId)}>
              {$t('common.edit')}
            </NButton>
          )}
          {hasAuth('system:post:delete') && (
            <NPopconfirm onPositiveClick={() => handleDelete(row.postId)}>
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

const {
  drawerVisible,
  operateType,
  editingData,
  handleAdd,
  handleEdit,
  checkedRowKeys,
  onBatchDeleted,
  onDeleted
} = useTableOperate(data, 'postId', getData);

async function handleBatchDelete() {
  const { error } = await fetchDeletePost(checkedRowKeys.value.join(','));
  if (error) return;
  onBatchDeleted();
}

async function handleDelete(postId: string) {
  const { error } = await fetchDeletePost(postId);
  if (error) return;
  onDeleted();
}

async function handleUpdateEnabled(row: Api.SystemManage.Post, checked: boolean) {
  if (!guardAuth('system:post:updateEnabled')) {
    return;
  }
  const isEnabled: Api.SystemManage.EnabledFlag = checked ? 1 : 0;
  const { error } = await fetchUpdatePostEnabled({ postId: row.postId, isEnabled });
  if (error) {
    await getData();
    return;
  }
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
}

function edit(postId: string) {
  handleEdit(postId);
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
          :show-add="hasAuth('system:post:insert')"
          :show-delete="hasAuth('system:post:delete')"
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
        :row-key="row => row.postId"
        :pagination="mobilePagination"
        class="sm:h-full"
      >
        <template #empty>
          <NEmpty
            :description="
              hasAuth('system:post:select') ? $t('common.noData') : $t('common.noPermission')
            "
          />
        </template>
      </NDataTable>
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
