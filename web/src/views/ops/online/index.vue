<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm } from 'naive-ui';
import { fetchOnlineUserList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { defaultTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import OnlineSearch from './modules/online-search.vue';

defineOptions({ name: 'OpsOnline' });

const appStore = useAppStore();

const searchParams = ref<Api.AutoboxScaffold.OnlineSearchParams>({
  current: 1,
  size: 10,
  userAccount: null,
  userName: null,
  loginIp: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchOnlineUserList(searchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    { key: 'userAccount', title: $t('page.autobox.online.userAccount'), align: 'center', minWidth: 120 },
    { key: 'userName', title: $t('page.autobox.online.userName'), align: 'center', minWidth: 100 },
    { key: 'loginIp', title: $t('page.autobox.online.loginIp'), align: 'center', width: 130 },
    { key: 'loginTime', title: $t('page.autobox.online.loginTime'), align: 'center', width: 170 },
    { key: 'browser', title: $t('page.autobox.online.browser'), align: 'center', width: 100 },
    { key: 'systemOs', title: $t('page.autobox.online.systemOs'), align: 'center', width: 100 },
    { key: 'timeoutText', title: $t('page.autobox.online.timeoutText'), align: 'center', width: 110 },
    { key: 'tokenDisplay', title: $t('page.autobox.online.tokenDisplay'), align: 'center', minWidth: 120 },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 100,
      render: row => (
        <NPopconfirm onPositiveClick={() => window.$message?.success($t('common.updateSuccess'))}>
          {{
            default: () => $t('common.confirm'),
            trigger: () => (
              <NButton type="error" ghost size="small" disabled={row.self === 1}>
                {$t('page.autobox.online.forceLogout')}
              </NButton>
            )
          }}
        </NPopconfirm>
      )
    }
  ]
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <OnlineSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard
      :title="$t('page.autobox.online.title')"
      :bordered="false"
      size="small"
      class="card-wrapper sm:flex-1-hidden"
    >
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
          <template #default />
        </TableHeaderOperation>
      </template>
      <NDataTable
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :scroll-x="1100"
        :loading="loading"
        remote
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
    </NCard>
  </div>
</template>
