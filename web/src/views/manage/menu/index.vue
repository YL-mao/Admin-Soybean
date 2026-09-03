<script setup lang="tsx">
import { computed, ref } from 'vue';
import type { Ref } from 'vue';
import { NButton, NPopconfirm, NSwitch, NTag } from 'naive-ui';
import { useBoolean } from '@sa/hooks';
import { enabledFlagRecord, menuTypeRecord } from '@/constants/business';
import { fetchDeleteMenu, fetchGetMenuList, fetchUpdateMenuEnabled } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useNaiveTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import SvgIcon from '@/components/custom/svg-icon.vue';
import MenuOperateModal, { type OperateType } from './modules/menu-operate-modal.vue';
import { buildMenuTree, collectMenuIconifyIcons } from './modules/shared';

const appStore = useAppStore();

const { bool: visible, setTrue: openModal } = useBoolean();

const wrapperRef = ref<HTMLElement | null>(null);

const { columns, columnChecks, data, loading, getData } = useNaiveTable({
  api: () => fetchGetMenuList(),
  // 后端返回平铺列表，前端按 parentId 组树
  transform: response => {
    if (response.error || !response.data) {
      return [];
    }
    return buildMenuTree(response.data);
  },
  columns: () => [
    {
      type: 'selection',
      align: 'center',
      width: 48
    },
    {
      key: 'menuName',
      title: $t('page.manage.menu.menuName'),
      align: 'left',
      minWidth: 160,
      render: row => {
        const { i18nKey, menuName } = row;
        const label = i18nKey ? $t(i18nKey as App.I18n.I18nKey) : menuName;
        return <span>{label}</span>;
      }
    },
    {
      key: 'menuType',
      title: $t('page.manage.menu.menuType'),
      align: 'center',
      width: 80,
      render: row => {
        const tagMap: Record<Api.SystemManage.MenuType, NaiveUI.ThemeColor> = {
          0: 'default',
          1: 'primary',
          2: 'info'
        };
        return <NTag type={tagMap[row.menuType]}>{$t(menuTypeRecord[row.menuType])}</NTag>;
      }
    },
    {
      key: 'menuIcon',
      title: $t('page.manage.menu.icon'),
      align: 'center',
      width: 60,
      render: row => {
        if (!row.menuIcon) {
          return null;
        }
        const icon = row.iconType === 1 ? row.menuIcon : undefined;
        const localIcon = row.iconType === 2 ? row.menuIcon : undefined;
        return (
          <div class="flex-center">
            <SvgIcon icon={icon} localIcon={localIcon} class="text-icon" />
          </div>
        );
      }
    },
    {
      key: 'routeName',
      title: $t('page.manage.menu.routeName'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'routePath',
      title: $t('page.manage.menu.routePath'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'permCode',
      title: $t('page.manage.menu.permCode'),
      align: 'center',
      minWidth: 140
    },
    {
      key: 'isEnabled',
      title: $t('page.manage.menu.menuStatus'),
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
      key: 'isShow',
      title: $t('page.manage.menu.hideInMenu'),
      align: 'center',
      width: 90,
      render: row => {
        // isShow=1 显示侧栏；0 相当于隐藏菜单
        const hide = row.isShow === 0;
        return <NTag type={hide ? 'error' : 'default'}>{hide ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no')}</NTag>;
      }
    },
    {
      key: 'orderNum',
      title: $t('page.manage.menu.order'),
      align: 'center',
      width: 60
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 230,
      render: row => (
        <div class="flex-center justify-end gap-8px">
          {row.menuType !== 2 && (
            <NButton type="primary" ghost size="small" onClick={() => handleAddChildMenu(row)}>
              {$t('page.manage.menu.addChildMenu')}
            </NButton>
          )}
          <NButton type="primary" ghost size="small" onClick={() => handleEdit(row)}>
            {$t('common.edit')}
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.menuId)}>
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

const { checkedRowKeys, onBatchDeleted, onDeleted } = useTableOperate(data, 'menuId', getData);

const operateType = ref<OperateType>('add');

function handleAdd() {
  operateType.value = 'add';
  editingData.value = null;
  openModal();
}

async function handleBatchDelete() {
  const { error } = await fetchDeleteMenu(checkedRowKeys.value.join(','));
  if (error) return;
  onBatchDeleted();
}

async function handleDelete(menuId: string) {
  const { error } = await fetchDeleteMenu(menuId);
  if (error) return;
  onDeleted();
}

/** 列表开关直接启停，走 /menu/updateEnabled */
async function handleUpdateEnabled(row: Api.SystemManage.Menu, checked: boolean) {
  const isEnabled: Api.SystemManage.EnabledFlag = checked ? 1 : 0;
  const { error } = await fetchUpdateMenuEnabled({ menuId: row.menuId, isEnabled });
  if (error) {
    // 失败时拉回真实状态，避免开关停在错误位置
    await getData();
    return;
  }
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
}

/** the edit menu data or the parent menu data when adding a child menu */
const editingData: Ref<Api.SystemManage.Menu | null> = ref(null);

function handleEdit(item: Api.SystemManage.Menu) {
  operateType.value = 'edit';
  editingData.value = { ...item };
  openModal();
}

function handleAddChildMenu(item: Api.SystemManage.Menu) {
  operateType.value = 'addChild';
  editingData.value = { ...item };
  openModal();
}

/** 下拉候选：当前菜单树已用过的 Iconify */
const usedIconifyIcons = computed(() => collectMenuIconifyIcons(data.value));
</script>

<template>
  <div ref="wrapperRef" class="flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NCard :title="$t('page.manage.menu.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
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
        :scroll-x="1200"
        :loading="loading"
        :row-key="row => row.menuId"
        class="sm:h-full"
      />
      <MenuOperateModal
        v-model:visible="visible"
        :operate-type="operateType"
        :row-data="editingData"
        :iconify-icons="usedIconifyIcons"
        @submitted="getData"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
