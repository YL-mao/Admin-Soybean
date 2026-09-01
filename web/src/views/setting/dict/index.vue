<script setup lang="tsx">
import { onMounted, ref, toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { NButton, NTag } from 'naive-ui';
import { yesOrNoRecord } from '@/constants/common';
import { enableStatusRecord } from '@/constants/business';
import { fetchDictDataList, fetchDictTypeList } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { defaultTransform, useNaivePaginatedTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';

defineOptions({ name: 'SettingDict' });

const appStore = useAppStore();
const selectedTypeId = ref<string | null>(null);

const typeSearchParams = ref<Api.AutoboxScaffold.DictTypeSearchParams>({
  current: 1,
  size: 10,
  dictTypeName: null,
  dictTypeCode: null,
  status: null
});

const dataSearchParams = ref<Api.AutoboxScaffold.DictDataSearchParams>({
  current: 1,
  size: 10,
  dictTypeId: null,
  dictDataLabel: null,
  dictDataValue: null,
  status: null
});

const defaultTypeSearchParams = jsonClone(toRaw(typeSearchParams.value));
const defaultDataSearchParams = jsonClone(toRaw(dataSearchParams.value));

const {
  columns: typeColumns,
  columnChecks: typeColumnChecks,
  data: typeData,
  loading: typeLoading,
  getData: getTypeData,
  getDataByPage: getTypeDataByPage,
  mobilePagination: typePagination
} = useNaivePaginatedTable({
  api: () => fetchDictTypeList(typeSearchParams.value),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    typeSearchParams.value.current = params.page;
    typeSearchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    { key: 'dictTypeName', title: $t('page.autobox.dict.dictTypeName'), align: 'center', minWidth: 120 },
    { key: 'dictTypeCode', title: $t('page.autobox.dict.dictTypeCode'), align: 'center', minWidth: 120 },
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
    { key: 'dictTypeDesc', title: $t('page.autobox.dict.dictTypeDesc'), align: 'center', minWidth: 120 },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 200,
      render: row => (
        <div class="flex-center gap-8px">
          <NButton size="small" type="info" ghost onClick={() => selectType(row)}>
            {$t('page.autobox.dict.selectType')}
          </NButton>
          <NButton size="small" type="primary" ghost onClick={() => handleTypeEdit(row.id)}>
            {$t('common.edit')}
          </NButton>
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
  api: () =>
    fetchDictDataList({
      ...dataSearchParams.value,
      dictTypeId: selectedTypeId.value ?? dataSearchParams.value.dictTypeId
    }),
  transform: response => defaultTransform(response),
  onPaginationParamsChange: params => {
    dataSearchParams.value.current = params.page;
    dataSearchParams.value.size = params.pageSize;
  },
  columns: () => [
    { type: 'selection', align: 'center', width: 48 },
    { key: 'dictDataLabel', title: $t('page.autobox.dict.dictDataLabel'), align: 'center', minWidth: 100 },
    { key: 'dictDataValue', title: $t('page.autobox.dict.dictDataValue'), align: 'center', minWidth: 100 },
    { key: 'orderNum', title: $t('page.autobox.dict.orderNum'), align: 'center', width: 80 },
    {
      key: 'isDefault',
      title: $t('page.autobox.dict.isDefault'),
      align: 'center',
      width: 80,
      render: row => $t(yesOrNoRecord[row.isDefault])
    },
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
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 130,
      render: row => (
        <NButton size="small" type="primary" ghost onClick={() => handleDataEdit(row.id)}>
          {$t('common.edit')}
        </NButton>
      )
    }
  ]
});

const typeOperate = useTableOperate(typeData, 'id', getTypeData);
const dataOperate = useTableOperate(dataData, 'id', getDataData);
const { drawerVisible: typeDrawerVisible, closeDrawer: closeTypeDrawer } = typeOperate;
const { drawerVisible: dataDrawerVisible, closeDrawer: closeDataDrawer } = dataOperate;

function resetTypeSearch() {
  Object.assign(typeSearchParams.value, defaultTypeSearchParams);
  getTypeDataByPage();
}

function resetDataSearch() {
  Object.assign(dataSearchParams.value, defaultDataSearchParams);
  dataSearchParams.value.dictTypeId = selectedTypeId.value;
  getDataDataByPage();
}

function handleTypeEdit(id: number) {
  typeOperate.handleEdit(id);
}

function handleDataEdit(id: number) {
  dataOperate.handleEdit(id);
}

function selectType(row: Api.AutoboxScaffold.DictType) {
  selectedTypeId.value = row.dictTypeId;
  dataSearchParams.value.dictTypeId = row.dictTypeId;
  getDataDataByPage();
}

onMounted(() => {
  getTypeData();
  getDataData();
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NCard :bordered="false" size="small" class="card-wrapper">
      <NCollapse :default-expanded-names="['dict-type-search']">
        <NCollapseItem :title="$t('common.search')" name="dict-type-search">
          <NForm label-placement="left" :label-width="80">
            <NGrid responsive="screen" item-responsive>
              <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.dict.dictTypeName')" class="pr-24px">
                <NInput v-model:value="typeSearchParams.dictTypeName" />
              </NFormItemGi>
              <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.dict.dictTypeCode')" class="pr-24px">
                <NInput v-model:value="typeSearchParams.dictTypeCode" />
              </NFormItemGi>
              <NFormItemGi span="24" class="pr-24px" :show-label="false" :show-feedback="false">
                <TableSearchActions @reset="resetTypeSearch" @search="getTypeDataByPage" />
              </NFormItemGi>
            </NGrid>
          </NForm>
        </NCollapseItem>
      </NCollapse>
    </NCard>

    <NCard :title="$t('page.autobox.dict.typeTitle')" :bordered="false" size="small" class="card-wrapper">
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="typeColumnChecks"
          :loading="typeLoading"
          @add="typeOperate.handleAdd"
          @delete="typeOperate.onBatchDeleted"
          @refresh="getTypeData"
        />
      </template>
      <NDataTable
        :columns="typeColumns"
        :data="typeData"
        size="small"
        :loading="typeLoading"
        remote
        :row-key="row => row.id"
        :pagination="typePagination"
        :scroll-x="900"
        class="sm:h-280px"
      />
    </NCard>

    <NCard :bordered="false" size="small" class="card-wrapper">
      <NCollapse :default-expanded-names="['dict-data-search']">
        <NCollapseItem :title="$t('common.search')" name="dict-data-search">
          <NForm label-placement="left" :label-width="80">
            <NGrid responsive="screen" item-responsive>
              <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.dict.dictDataLabel')" class="pr-24px">
                <NInput v-model:value="dataSearchParams.dictDataLabel" />
              </NFormItemGi>
              <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.dict.dictDataValue')" class="pr-24px">
                <NInput v-model:value="dataSearchParams.dictDataValue" />
              </NFormItemGi>
              <NFormItemGi span="24" class="pr-24px" :show-label="false" :show-feedback="false">
                <TableSearchActions @reset="resetDataSearch" @search="getDataDataByPage" />
              </NFormItemGi>
            </NGrid>
          </NForm>
        </NCollapseItem>
      </NCollapse>
    </NCard>

    <NCard
      :title="$t('page.autobox.dict.dataTitle')"
      :bordered="false"
      size="small"
      class="card-wrapper sm:flex-1-hidden"
    >
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="dataColumnChecks"
          :loading="dataLoading"
          @add="dataOperate.handleAdd"
          @delete="dataOperate.onBatchDeleted"
          @refresh="getDataData"
        />
      </template>
      <NDataTable
        :columns="dataColumns"
        :data="dataData"
        size="small"
        :flex-height="!appStore.isMobile"
        :loading="dataLoading"
        remote
        :row-key="row => row.id"
        :pagination="dataPagination"
        :scroll-x="800"
        class="sm:h-full"
      />
    </NCard>

    <NDrawer v-model:show="typeDrawerVisible" :width="360">
      <NDrawerContent :title="$t('page.autobox.dict.editDictType')" closable>
        <NButton type="primary" @click="closeTypeDrawer">{{ $t('common.confirm') }}</NButton>
      </NDrawerContent>
    </NDrawer>
    <NDrawer v-model:show="dataDrawerVisible" :width="360">
      <NDrawerContent :title="$t('page.autobox.dict.editDictData')" closable>
        <NButton type="primary" @click="closeDataDrawer">{{ $t('common.confirm') }}</NButton>
      </NDrawerContent>
    </NDrawer>
  </div>
</template>
