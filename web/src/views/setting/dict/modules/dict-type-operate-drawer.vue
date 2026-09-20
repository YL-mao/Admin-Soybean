<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { enabledFlagOptions } from '@/constants/business';
import { fetchCheckDictTypeCodeUnique, fetchCreateDictType, fetchUpdateDictType } from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';

defineOptions({ name: 'DictTypeOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.SystemManage.DictType | null;
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
    add: $t('page.autobox.dict.addDictType'),
    edit: $t('page.autobox.dict.editDictType')
  };
  return titles[props.operateType];
});

type Model = {
  dictTypeName: string;
  dictTypeCode: string;
  orderNum: number;
  isEnabled: Api.SystemManage.EnabledFlag;
  dictTypeDesc: string;
};

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    dictTypeName: '',
    dictTypeCode: '',
    orderNum: 0,
    // 表单默认禁用，仍允许手动改启用
    isEnabled: 0,
    dictTypeDesc: ''
  };
}

const rules: Record<'dictTypeName' | 'dictTypeCode' | 'orderNum' | 'isEnabled', App.Global.FormRule | App.Global.FormRule[]> = {
  dictTypeName: defaultRequiredRule,
  dictTypeCode: [
    defaultRequiredRule,
    {
      // 字母数字下划线连字符
      pattern: /^[a-zA-Z0-9_-]+$/,
      message: $t('page.autobox.dict.form.dictTypeCodeInvalid'),
      trigger: 'blur'
    }
  ],
  orderNum: defaultRequiredRule,
  isEnabled: defaultRequiredRule
};

function handleInitModel() {
  model.value = createDefaultModel();
  if (props.operateType === 'edit' && props.rowData) {
    const row = props.rowData;
    model.value = {
      dictTypeName: row.dictTypeName,
      dictTypeCode: row.dictTypeCode,
      orderNum: row.orderNum ?? 0,
      isEnabled: row.isEnabled ?? 0,
      dictTypeDesc: row.dictTypeDesc || ''
    };
  }
}

async function handleSubmit() {
  await validate();

  const { data: codeOk, error: codeErr } = await fetchCheckDictTypeCodeUnique({
    dictTypeCode: model.value.dictTypeCode
  });
  if (codeErr) return;
  if (codeOk === false && !(props.operateType === 'edit' && props.rowData?.dictTypeCode === model.value.dictTypeCode)) {
    window.$message?.error($t('page.autobox.dict.codeExists'));
    return;
  }

  const body: Api.SystemManage.DictTypeInsert = {
    dictTypeName: model.value.dictTypeName,
    dictTypeCode: model.value.dictTypeCode,
    orderNum: model.value.orderNum,
    isEnabled: model.value.isEnabled,
    dictTypeDesc: model.value.dictTypeDesc || null
  };

  if (props.operateType === 'edit' && props.rowData) {
    const { error } = await fetchUpdateDictType({ ...body, dictTypeId: props.rowData.dictTypeId });
    if (error) return;
    window.$message?.success($t('common.updateSuccess'));
  } else {
    const { error } = await fetchCreateDictType(body);
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
        <NFormItem :label="$t('page.autobox.dict.dictTypeName')" path="dictTypeName">
          <NInput v-model:value="model.dictTypeName" :placeholder="$t('page.autobox.dict.form.dictTypeName')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.dict.dictTypeCode')" path="dictTypeCode">
          <NInput v-model:value="model.dictTypeCode" :placeholder="$t('page.autobox.dict.form.dictTypeCode')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.dict.orderNum')" path="orderNum">
          <NInputNumber v-model:value="model.orderNum" class="w-full" :placeholder="$t('page.autobox.dict.form.orderNum')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.dict.dictTypeDesc')" path="dictTypeDesc">
          <NInput
            v-model:value="model.dictTypeDesc"
            type="textarea"
            :placeholder="$t('page.autobox.dict.form.dictTypeDesc')"
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
