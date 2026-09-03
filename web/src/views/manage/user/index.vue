<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NSwitch, NTag } from 'naive-ui';
import { enabledFlagRecord, lockFlagRecord, userSexRecord } from '@/constants/business';
import { fetchDeleteUser, fetchGetUserList, fetchUpdateUserEnabled, fetchUpdateUserLock } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import UserOperateDrawer from './modules/user-operate-drawer.vue';
import UserSearch from './modules/user-search.vue';

const appStore = useAppStore();
const { hasAuth, guardAuth } = useAuth();

const searchParams = ref<Api.SystemManage.UserSearchParams>({
  current: 1,
  size: 10,
  userAccount: null,
  isEnabled: null,
  isLock: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () =>
    hasAuth('system:user:select')
      ? fetchGetUserList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.User>()),
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
      align: 'center',
      width: 64,
      render: (_, index) => index + 1
    },
    {
      key: 'userAccount',
      title: $t('page.manage.user.userAccount'),
      align: 'center',
      minWidth: 110
    },
    {
      key: 'userName',
      title: $t('page.manage.user.userName'),
      align: 'center',
      minWidth: 100
    },
    {
      key: 'userSex',
      title: $t('page.manage.user.userSex'),
      align: 'center',
      width: 80,
      render: row => {
        if (row.userSex == null || !(row.userSex in userSexRecord)) {
          return row.userSexName || null;
        }
        const tagMap: Record<Api.SystemManage.UserSex, NaiveUI.ThemeColor> = {
          '0': 'primary',
          '1': 'error'
        };
        return <NTag type={tagMap[row.userSex]}>{$t(userSexRecord[row.userSex])}</NTag>;
      }
    },
    {
      key: 'userPhone',
      title: $t('page.manage.user.userPhone'),
      align: 'center',
      width: 120
    },
    {
      key: 'userEmail',
      title: $t('page.manage.user.userEmail'),
      align: 'center',
      minWidth: 160
    },
    {
      key: 'deptName',
      title: $t('page.manage.user.deptName'),
      align: 'center',
      minWidth: 100
    },
    {
      key: 'roleNames',
      title: $t('page.manage.user.userRole'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'isLock',
      title: $t('page.manage.user.userLock'),
      align: 'center',
      width: 100,
      render: row => (
        // 开=正常、关=锁定；无权限仍展示，点击时拦截
        <NSwitch
          value={row.isLock !== 1}
          rubberBand={false}
          onUpdateValue={value => handleUpdateLock(row, !value)}
        >
          {{
            checked: () => $t(lockFlagRecord[0]),
            unchecked: () => $t(lockFlagRecord[1])
          }}
        </NSwitch>
      )
    },
    {
      key: 'isEnabled',
      title: $t('page.manage.user.userStatus'),
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
          {hasAuth('system:user:update') && (
            <NButton type="primary" ghost size="small" onClick={() => edit(row.userId)}>
              {$t('common.edit')}
            </NButton>
          )}
          {hasAuth('system:user:delete') && (
            <NPopconfirm onPositiveClick={() => handleDelete(row.userId)}>
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
} = useTableOperate(data, 'userId', getData);

async function handleBatchDelete() {
  const { error } = await fetchDeleteUser(checkedRowKeys.value.join(','));
  if (error) return;
  onBatchDeleted();
}

async function handleDelete(userId: string) {
  const { error } = await fetchDeleteUser(userId);
  if (error) return;
  onDeleted();
}

/** 列表开关启停；无密码时后端会拒绝启用 */
async function handleUpdateEnabled(row: Api.SystemManage.User, checked: boolean) {
  if (!guardAuth('system:user:updateEnabled')) {
    return;
  }
  const isEnabled: Api.SystemManage.EnabledFlag = checked ? 1 : 0;
  const { error } = await fetchUpdateUserEnabled({ userId: row.userId, isEnabled });
  if (error) {
    await getData();
    return;
  }
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
}

/** 列表开关锁定：参数 locked=true 表示锁定 */
async function handleUpdateLock(row: Api.SystemManage.User, locked: boolean) {
  if (!guardAuth('system:user:unlock')) {
    return;
  }
  const isLock: Api.SystemManage.EnabledFlag = locked ? 1 : 0;
  const { error } = await fetchUpdateUserLock({ userId: row.userId, isLock });
  if (error) {
    await getData();
    return;
  }
  row.isLock = isLock;
  window.$message?.success($t('common.updateSuccess'));
}

function edit(userId: string) {
  handleEdit(userId);
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <UserSearch v-model:model="searchParams" @search="getDataByPage" />
    <NCard :title="$t('page.manage.user.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :disabled-delete="checkedRowKeys.length === 0"
          :loading="loading"
          :show-add="hasAuth('system:user:insert')"
          :show-delete="hasAuth('system:user:delete')"
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
        :scroll-x="1200"
        :loading="loading"
        remote
        :row-key="row => row.userId"
        :pagination="mobilePagination"
        class="sm:h-full"
      >
        <template #empty>
          <NEmpty
            :description="
              hasAuth('system:user:select') ? $t('common.noData') : $t('common.noPermission')
            "
          />
        </template>
      </NDataTable>
      <UserOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
