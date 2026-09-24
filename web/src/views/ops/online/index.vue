<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NTag } from 'naive-ui';
import { fetchGetOnlineUserList, fetchKickOnlineSession } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import OnlineSearch from './modules/online-search.vue';

defineOptions({ name: 'OpsOnline' });

const appStore = useAppStore();
const { hasAuth } = useAuth();

const searchParams = ref<Api.SystemManage.OnlineSearchParams>({
  current: 1,
  size: 10,
  userAccount: null,
  loginIp: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () =>
    hasAuth('system:online:select')
      ? fetchGetOnlineUserList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.OnlineUser>()),
  transform: response =>
    backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => [
    {
      key: 'userAccount',
      title: $t('page.autobox.online.userAccount'),
      align: 'center',
      minWidth: 120,
      render: row => (
        <div class="flex-center gap-8px">
          <span>{row.userAccount}</span>
          {row.self && <NTag size="small" type="info">{$t('page.autobox.online.currentSession')}</NTag>}
        </div>
      )
    },
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
      render: row => {
        if (!hasAuth('system:online:kick')) return null;
        // 本机会话禁止强退，与后端约定一致。
        if (row.self) {
          return (
            <NButton type="error" ghost size="small" disabled>
              {$t('page.autobox.online.forceLogout')}
            </NButton>
          );
        }
        return (
          <NPopconfirm onPositiveClick={() => handleKick(row)}>
            {{
              default: () => $t('page.autobox.online.forceLogoutConfirm'),
              trigger: () => (
                <NButton type="error" ghost size="small">
                  {$t('page.autobox.online.forceLogout')}
                </NButton>
              )
            }}
          </NPopconfirm>
        );
      }
    }
  ]
});

async function handleKick(row: Api.SystemManage.OnlineUser) {
  // 按 token 强退当前行会话；踢用户全部会话仍走用户管理。
  const { error } = await fetchKickOnlineSession({ tokenValue: row.tokenValue });
  if (error) return;
  window.$message?.success($t('page.autobox.online.forceLogoutSuccess'));
  getData();
}
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
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :show-add="false"
          :show-delete="false"
          :loading="loading"
          @refresh="getData"
        />
      </template>
      <NDataTable
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :scroll-x="1200"
        :loading="loading"
        remote
        :row-key="row => row.tokenValue"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <div v-if="!hasAuth('system:online:select')" class="py-24px text-center text-gray-400">
        {{ $t('common.noPermission') }}
      </div>
    </NCard>
  </div>
</template>
