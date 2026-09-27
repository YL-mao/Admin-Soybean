<script setup lang="tsx">
import { ref } from 'vue';
import { NButton, NPopconfirm, NSwitch } from 'naive-ui';
import { enabledFlagRecord } from '@/constants/business';
import {
  fetchDeleteDictData,
  fetchDeleteDictType,
  fetchGetDictDataList,
  fetchGetDictTypeList,
  fetchRefreshDictCache,
  fetchUpdateDictDataDefault,
  fetchUpdateDictDataEnabled,
  fetchUpdateDictTypeEnabled
} from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import DictDataOperateDrawer from './modules/dict-data-operate-drawer.vue';
import DictDataSearch from './modules/dict-data-search.vue';
import DictTypeOperateDrawer from './modules/dict-type-operate-drawer.vue';
import DictTypeSearch from './modules/dict-type-search.vue';

defineOptions({ name: 'SettingDict' });

const appStore = useAppStore();
const { hasAuth, guardAuth } = useAuth();

const typeSearchParams = ref<Api.SystemManage.DictTypeSearchParams>({
  current: 1,
  size: 10,
  dictTypeName: null
});

const dataSearchParams = ref<Api.SystemManage.DictDataSearchParams>({
  current: 1,
  size: 10,
  dictTypeCode: null,
  dictDataLabel: null
});

/** 当前选中的字典类型；未选时右侧不查数据 */
const selectedType = ref<Api.SystemManage.DictType | null>(null);
const refreshing = ref(false);

const {
  columns: typeColumns,
  columnChecks: typeColumnChecks,
  data: typeData,
  loading: typeLoading,
  getData: getTypeData,
  getDataByPage: getTypeDataByPage,
  mobilePagination: typePagination
} = useNaivePaginatedTable({
  api: () =>
    hasAuth('system:dictType:select')
      ? fetchGetDictTypeList(typeSearchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.DictType>()),
  transform: response =>
    backendPageTransform(response, typeSearchParams.value.current || 1, typeSearchParams.value.size || 10),
  onPaginationParamsChange: params => {
    typeSearchParams.value.current = params.page;
    typeSearchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 40 },
    {
      key: 'dictTypeName',
      title: $t('page.setting.dict.dictTypeName'),
      align: 'left',
      ellipsis: { tooltip: true },
      minWidth: 100
    },
    {
      key: 'dictTypeCode',
      title: $t('page.setting.dict.dictTypeCode'),
      align: 'left',
      ellipsis: { tooltip: true },
      minWidth: 100
    },
    {
      key: 'isEnabled',
      title: $t('page.manage.common.status.enable'),
      align: 'center',
      width: 90,
      render: row => (
        <div onClick={(e: MouseEvent) => e.stopPropagation()}>
          <NSwitch
            value={row.isEnabled === 1}
            rubberBand={false}
            onUpdateValue={value => handleTypeEnabled(row, value)}
          >
            {{
              checked: () => $t(enabledFlagRecord[1]),
              unchecked: () => $t(enabledFlagRecord[0])
            }}
          </NSwitch>
        </div>
      )
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 130,
      render: row => (
        <div class="flex-center gap-8px" onClick={(e: MouseEvent) => e.stopPropagation()}>
          {hasAuth('system:dictType:update') && (
            <NButton size="small" type="primary" ghost onClick={() => editType(row.dictTypeId)}>
              {$t('common.edit')}
            </NButton>
          )}
          {hasAuth('system:dictType:delete') && (
            <NPopconfirm onPositiveClick={() => handleTypeDelete(row.dictTypeId)}>
              {{
                default: () => $t('common.confirmDelete'),
                trigger: () => (
                  <NButton size="small" type="error" ghost>
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
  columns: dataColumns,
  columnChecks: dataColumnChecks,
  data: dataData,
  loading: dataLoading,
  getData: getDataData,
  getDataByPage: getDataDataByPage,
  mobilePagination: dataPagination
} = useNaivePaginatedTable({
  api: () => {
    if (!hasAuth('system:dictData:select') || !selectedType.value) {
      return Promise.resolve(emptyAuthListResponse<Api.SystemManage.DictData>());
    }
    return fetchGetDictDataList({
      ...dataSearchParams.value,
      dictTypeCode: selectedType.value.dictTypeCode
    });
  },
  transform: response =>
    backendPageTransform(response, dataSearchParams.value.current || 1, dataSearchParams.value.size || 10),
  onPaginationParamsChange: params => {
    dataSearchParams.value.current = params.page;
    dataSearchParams.value.size = params.pageSize;
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
    { key: 'dictDataLabel', title: $t('page.setting.dict.dictDataLabel'), align: 'center', minWidth: 100 },
    { key: 'dictDataValue', title: $t('page.setting.dict.dictDataValue'), align: 'center', minWidth: 100 },
    { key: 'orderNum', title: $t('page.setting.dict.orderNum'), align: 'center', width: 80 },
    {
      key: 'isDefault',
      title: $t('page.setting.dict.isDefault'),
      align: 'center',
      width: 90,
      render: row => (
        <NSwitch
          value={row.isDefault === '1'}
          rubberBand={false}
          onUpdateValue={value => handleDataDefault(row, value)}
        />
      )
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
          onUpdateValue={value => handleDataEnabled(row, value)}
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
      width: 150,
      render: row => (
        <div class="flex-center gap-8px">
          {hasAuth('system:dictData:update') && (
            <NButton size="small" type="primary" ghost onClick={() => editData(row.dictDataId)}>
              {$t('common.edit')}
            </NButton>
          )}
          {hasAuth('system:dictData:delete') && (
            <NPopconfirm onPositiveClick={() => handleDataDelete(row.dictDataId)}>
              {{
                default: () => $t('common.confirmDelete'),
                trigger: () => (
                  <NButton size="small" type="error" ghost>
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
  drawerVisible: typeDrawerVisible,
  operateType: typeOperateType,
  editingData: typeEditingData,
  handleAdd: handleTypeAdd,
  handleEdit: handleTypeEdit,
  checkedRowKeys: typeCheckedRowKeys,
  onBatchDeleted: onTypeBatchDeleted,
  onDeleted: onTypeDeleted
} = useTableOperate(typeData, 'dictTypeId', getTypeData);

const {
  drawerVisible: dataDrawerVisible,
  operateType: dataOperateType,
  editingData: dataEditingData,
  handleAdd: handleDataAdd,
  handleEdit: handleDataEdit,
  checkedRowKeys: dataCheckedRowKeys,
  onBatchDeleted: onDataBatchDeleted,
  onDeleted: onDataDeleted
} = useTableOperate(dataData, 'dictDataId', getDataData);

function selectType(row: Api.SystemManage.DictType) {
  selectedType.value = row;
  dataSearchParams.value.dictTypeCode = row.dictTypeCode;
  getDataDataByPage();
}

/** 左侧类型行点击选中，开关与操作列已 stopPropagation */
function typeRowProps(row: Api.SystemManage.DictType) {
  return {
    style: 'cursor: pointer;',
    onClick: () => selectType(row)
  };
}

function typeRowClassName(row: Api.SystemManage.DictType) {
  return selectedType.value?.dictTypeId === row.dictTypeId ? 'dict-type-row--active' : '';
}

function editType(dictTypeId: string) {
  handleTypeEdit(dictTypeId);
}

function editData(dictDataId: string) {
  handleDataEdit(dictDataId);
}

function openDataAdd() {
  if (!selectedType.value) {
    window.$message?.warning($t('page.setting.dict.selectTypeFirst'));
    return;
  }
  handleDataAdd();
}

/** 类型保存后同步当前选中行，编码变更时右侧跟着刷新 */
async function handleTypeSubmitted() {
  await getTypeData();
  if (selectedType.value) {
    const found = typeData.value.find(item => item.dictTypeId === selectedType.value?.dictTypeId) || null;
    selectedType.value = found;
    dataSearchParams.value.dictTypeCode = found?.dictTypeCode ?? null;
  }
  await getDataData();
}

async function handleTypeBatchDelete() {
  const ids = typeCheckedRowKeys.value;
  const { error } = await fetchDeleteDictType(ids.join(','));
  if (error) return;
  if (selectedType.value && ids.includes(selectedType.value.dictTypeId)) {
    selectedType.value = null;
    dataSearchParams.value.dictTypeCode = null;
  }
  onTypeBatchDeleted();
  await getDataData();
}

async function handleTypeDelete(dictTypeId: string) {
  const { error } = await fetchDeleteDictType(dictTypeId);
  if (error) return;
  if (selectedType.value?.dictTypeId === dictTypeId) {
    selectedType.value = null;
    dataSearchParams.value.dictTypeCode = null;
  }
  onTypeDeleted();
  await getDataData();
}

async function handleDataBatchDelete() {
  const { error } = await fetchDeleteDictData(dataCheckedRowKeys.value.join(','));
  if (error) return;
  onDataBatchDeleted();
}

async function handleDataDelete(dictDataId: string) {
  const { error } = await fetchDeleteDictData(dictDataId);
  if (error) return;
  onDataDeleted();
}

/** 列表开关启停字典类型 */
async function handleTypeEnabled(row: Api.SystemManage.DictType, checked: boolean) {
  if (!guardAuth('system:dictType:updateEnabled')) {
    return;
  }
  const isEnabled: Api.SystemManage.EnabledFlag = checked ? 1 : 0;
  const { error } = await fetchUpdateDictTypeEnabled({ dictTypeId: row.dictTypeId, isEnabled });
  if (error) {
    await getTypeData();
    return;
  }
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
}

/** 列表开关启停字典数据 */
async function handleDataEnabled(row: Api.SystemManage.DictData, checked: boolean) {
  if (!guardAuth('system:dictData:updateEnabled')) {
    return;
  }
  const isEnabled: Api.SystemManage.EnabledFlag = checked ? 1 : 0;
  const { error } = await fetchUpdateDictDataEnabled({ dictDataId: row.dictDataId, isEnabled });
  if (error) {
    await getDataData();
    return;
  }
  row.isEnabled = isEnabled;
  window.$message?.success($t('common.updateSuccess'));
}

/** 同一类型只保留一个默认项，成功后刷新列表 */
async function handleDataDefault(row: Api.SystemManage.DictData, checked: boolean) {
  if (!guardAuth('system:dictData:updateDefault')) {
    return;
  }
  const isDefault: Api.SystemManage.DictDefaultFlag = checked ? '1' : '0';
  const { error } = await fetchUpdateDictDataDefault({ dictDataId: row.dictDataId, isDefault });
  if (error) {
    await getDataData();
    return;
  }
  window.$message?.success($t('common.updateSuccess'));
  await getDataData();
}

async function handleRefreshCache() {
  if (!selectedType.value) {
    window.$message?.warning($t('page.setting.dict.selectTypeFirst'));
    return;
  }
  if (!guardAuth('system:dictData:update')) {
    return;
  }
  refreshing.value = true;
  const { error } = await fetchRefreshDictCache(selectedType.value.dictTypeCode);
  refreshing.value = false;
  if (error) return;
  window.$message?.success($t('page.setting.dict.refreshSuccess'));
}

function handleDataSearch() {
  if (!selectedType.value) {
    window.$message?.warning($t('page.setting.dict.selectTypeFirst'));
    return;
  }
  getDataDataByPage();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <div class="flex-1-hidden flex gap-16px overflow-hidden lt-sm:flex-col lt-sm:overflow-auto">
      <!-- 左侧：字典类型 -->
      <div class="flex-1 flex-col-stretch gap-16px overflow-hidden lt-sm:w-full">
        <DictTypeSearch v-model:model="typeSearchParams" @search="getTypeDataByPage" />
        <NCard
          :title="$t('page.setting.dict.typeTitle')"
          :bordered="false"
          size="small"
          class="card-wrapper sm:flex-1-hidden"
        >
          <template #header-extra>
            <TableHeaderOperation
              v-model:columns="typeColumnChecks"
              :disabled-delete="typeCheckedRowKeys.length === 0"
              :loading="typeLoading"
              :show-add="hasAuth('system:dictType:insert')"
              :show-delete="hasAuth('system:dictType:delete')"
              @add="handleTypeAdd"
              @delete="handleTypeBatchDelete"
              @refresh="getTypeData"
            />
          </template>
          <NDataTable
            v-model:checked-row-keys="typeCheckedRowKeys"
            :columns="typeColumns"
            :data="typeData"
            size="small"
            :flex-height="!appStore.isMobile"
            :loading="typeLoading"
            remote
            :row-key="row => row.dictTypeId"
            :row-props="typeRowProps"
            :row-class-name="typeRowClassName"
            :pagination="typePagination"
            :scroll-x="480"
            class="sm:h-full"
          >
            <template #empty>
              <NEmpty
                :description="hasAuth('system:dictType:select') ? $t('common.noData') : $t('common.noPermission')"
              />
            </template>
          </NDataTable>
        </NCard>
      </div>

      <!-- 右侧：字典数据 -->
      <div class="flex-1 flex-col-stretch gap-16px overflow-hidden lt-sm:w-full lt-sm:min-h-420px">
        <DictDataSearch
          v-model:model="dataSearchParams"
          :disabled="!selectedType"
          @search="handleDataSearch"
        />
        <NCard
          :title="
            selectedType
              ? `${$t('page.setting.dict.dataTitle')} - ${selectedType.dictTypeName}`
              : $t('page.setting.dict.dataTitle')
          "
          :bordered="false"
          size="small"
          class="card-wrapper sm:flex-1-hidden"
        >
          <template #header-extra>
            <TableHeaderOperation
              v-model:columns="dataColumnChecks"
              :disabled-delete="dataCheckedRowKeys.length === 0"
              :loading="dataLoading"
              :show-add="hasAuth('system:dictData:insert')"
              :show-delete="hasAuth('system:dictData:delete')"
              @add="openDataAdd"
              @delete="handleDataBatchDelete"
              @refresh="getDataData"
            >
              <template #prefix>
                <NButton
                  v-if="hasAuth('system:dictData:update')"
                  size="small"
                  type="primary"
                  ghost
                  :loading="refreshing"
                  @click="handleRefreshCache"
                >
                  {{ $t('page.setting.dict.refreshCache') }}
                </NButton>
              </template>
            </TableHeaderOperation>
          </template>
          <NDataTable
            v-model:checked-row-keys="dataCheckedRowKeys"
            :columns="dataColumns"
            :data="dataData"
            size="small"
            :flex-height="!appStore.isMobile"
            :loading="dataLoading"
            remote
            :row-key="row => row.dictDataId"
            :pagination="dataPagination"
            :scroll-x="800"
            class="sm:h-full"
          >
            <template #empty>
              <NEmpty
                :description="
                  !hasAuth('system:dictData:select')
                    ? $t('common.noPermission')
                    : selectedType
                      ? $t('common.noData')
                      : $t('page.setting.dict.selectTypeFirst')
                "
              />
            </template>
          </NDataTable>
        </NCard>
      </div>
    </div>

    <DictTypeOperateDrawer
      v-model:visible="typeDrawerVisible"
      :operate-type="typeOperateType"
      :row-data="typeEditingData"
      @submitted="handleTypeSubmitted"
    />
    <DictDataOperateDrawer
      v-model:visible="dataDrawerVisible"
      :operate-type="dataOperateType"
      :row-data="dataEditingData"
      :dict-type="selectedType"
      @submitted="getDataData"
    />
  </div>
</template>

<style scoped>
:deep(.dict-type-row--active td) {
  background-color: var(--n-merged-color-hover);
}
</style>
