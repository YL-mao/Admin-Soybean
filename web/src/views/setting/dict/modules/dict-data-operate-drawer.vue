<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { enabledFlagOptions } from '@/constants/business';
import {
  fetchCheckDictDataLabelUnique,
  fetchCheckDictDataValueUnique,
  fetchCreateDictData,
  fetchUpdateDictData
} from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';

defineOptions({ name: 'DictDataOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.SystemManage.DictData | null;
  /** 当前选中的字典类型，新增时带上编码 */
  dictType?: Api.SystemManage.DictType | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', { default: false });

const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() => {
  const titles: Record<NaiveUI.TableOperateType, string> = {
    add: $t('page.autobox.dict.addDictData'),
    edit: $t('page.autobox.dict.editDictData')
  };
  return titles[props.operateType];
});

const dictTypeCode = computed(() => props.rowData?.dictTypeCode || props.dictType?.dictTypeCode || '');

type Model = {
  dictDataLabel: string;
  dictDataValue: string;
  orderNum: number;
  isEnabled: Api.SystemManage.EnabledFlag;
  dictDataDesc: string;
};

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    dictDataLabel: '',
    dictDataValue: '',
    orderNum: 0,
    isEnabled: 0,
    dictDataDesc: ''
  };
}

const rules: Record<'dictDataLabel' | 'dictDataValue' | 'orderNum' | 'isEnabled', App.Global.FormRule> = {
  dictDataLabel: defaultRequiredRule,
  dictDataValue: defaultRequiredRule,
  orderNum: defaultRequiredRule,
  isEnabled: defaultRequiredRule
};

function handleInitModel() {
  model.value = createDefaultModel();
  if (props.operateType === 'edit' && props.rowData) {
    const row = props.rowData;
    model.value = {
      dictDataLabel: row.dictDataLabel,
      dictDataValue: row.dictDataValue,
      orderNum: row.orderNum ?? 0,
      isEnabled: row.isEnabled ?? 0,
      dictDataDesc: row.dictDataDesc || ''
    };
  }
}

async function handleSubmit() {
  await validate();
  if (!dictTypeCode.value) {
    window.$message?.warning($t('page.autobox.dict.selectTypeFirst'));
    return;
  }

  const { data: labelOk, error: labelErr } = await fetchCheckDictDataLabelUnique({
    dictTypeCode: dictTypeCode.value,
    dictDataLabel: model.value.dictDataLabel
  });
  if (labelErr) return;
  if (
    labelOk === false &&
    !(props.operateType === 'edit' && props.rowData?.dictDataLabel === model.value.dictDataLabel)
  ) {
    window.$message?.error($t('page.autobox.dict.labelExists'));
    return;
  }

  const { data: valueOk, error: valueErr } = await fetchCheckDictDataValueUnique({
    dictTypeCode: dictTypeCode.value,
    dictDataValue: model.value.dictDataValue
  });
  if (valueErr) return;
  if (
    valueOk === false &&
    !(props.operateType === 'edit' && props.rowData?.dictDataValue === model.value.dictDataValue)
  ) {
    window.$message?.error($t('page.autobox.dict.valueExists'));
    return;
  }

  const body: Api.SystemManage.DictDataInsert = {
    dictTypeCode: dictTypeCode.value,
    dictDataLabel: model.value.dictDataLabel,
    dictDataValue: model.value.dictDataValue,
    orderNum: model.value.orderNum,
    isEnabled: model.value.isEnabled,
    dictDataDesc: model.value.dictDataDesc || null
  };

  if (props.operateType === 'edit' && props.rowData) {
    const { error } = await fetchUpdateDictData({ ...body, dictDataId: props.rowData.dictDataId });
    if (error) return;
    window.$message?.success($t('common.updateSuccess'));
  } else {
    const { error } = await fetchCreateDictData(body);
    if (error) return;
    window.$message?.success($t('common.addSuccess'));
  }

  visible.value = false;
  emit('submitted');
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});
</script>

<template>
  <NDrawer v-model:show="visible" display-directive="show" :width="360">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="90">
        <NFormItem :label="$t('page.autobox.dict.dictTypeCode')">
          <NInput :value="dictTypeCode" disabled />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.dict.dictDataLabel')" path="dictDataLabel">
          <NInput v-model:value="model.dictDataLabel" :placeholder="$t('page.autobox.dict.form.dictDataLabel')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.dict.dictDataValue')" path="dictDataValue">
          <NInput v-model:value="model.dictDataValue" :placeholder="$t('page.autobox.dict.form.dictDataValue')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.dict.orderNum')" path="orderNum">
          <NInputNumber v-model:value="model.orderNum" class="w-full" :placeholder="$t('page.autobox.dict.form.orderNum')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.dict.dictDataDesc')" path="dictDataDesc">
          <NInput
            v-model:value="model.dictDataDesc"
            type="textarea"
            :placeholder="$t('page.autobox.dict.form.dictDataDesc')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.manage.common.status.enable')" path="isEnabled">
          <NRadioGroup v-model:value="model.isEnabled">
            <NRadio v-for="item in enabledFlagOptions" :key="item.value" :value="item.value" :label="$t(item.label)" />
          </NRadioGroup>
        </NFormItem>
      </NForm>
      <template #footer>
        <NSpace justify="end" :size="16">
          <NButton @click="visible = false">{{ $t('common.cancel') }}</NButton>
          <NButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
        </NSpace>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
