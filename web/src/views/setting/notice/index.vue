<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NTag } from 'naive-ui';
import { enableStatusRecord } from '@/constants/business';
import { fetchNoticeList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { defaultTransform, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigGroupDrawer from '@/components/custom/config-group-drawer.vue';
import NoticeOperateDrawer from './modules/notice-operate-drawer.vue';
import NoticeSearch from './modules/notice-search.vue';

defineOptions({ name: 'SettingNotice' });

const appStore = useAppStore();
const { hasAuth } = useAuth();
const noticeConfigVisible = ref(false);

const searchParams = ref<Api.AutoboxScaffold.NoticeSearchParams>({
  current: 1,
  size: 10,
  noticeTitle: null,
  status: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchNoticeList(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    { key: 'noticeTitle', title: $t('page.autobox.notice.noticeTitle'), align: 'center', minWidth: 180 },
    { key: 'noticeTypeName', title: $t('page.autobox.notice.noticeType'), align: 'center', width: 100 },
    { key: 'orderNum', title: $t('page.autobox.dict.orderNum'), align: 'center', width: 80 },
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
    { key: 'sendTime', title: $t('page.autobox.notice.sendTime'), align: 'center', width: 170 },
    { key: 'expireTime', title: $t('page.autobox.notice.expireTime'), align: 'center', width: 170 },
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
    <NoticeSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard
      :title="$t('page.autobox.notice.title')"
      :bordered="false"
      size="small"
      class="card-wrapper sm:flex-1-hidden"
    >
      <template #header-extra>
        <NSpace>
          <NButton
            v-if="hasAuth('system:config:notice')"
            size="small"
            ghost
            type="primary"
            @click="noticeConfigVisible = true"
          >
            {{ $t('page.autobox.notice.noticeConfig') }}
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
        :scroll-x="1000"
        :loading="loading"
        remote
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <NoticeOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
      <ConfigGroupDrawer
        v-model:visible="noticeConfigVisible"
        config-group="notice"
        perm-code="system:config:notice"
        :title="$t('page.autobox.notice.noticeConfig')"
      />
    </NCard>
  </div>
</template>
