<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { jsonClone } from '@sa/utils';
import { enableStatusOptions } from '@/constants/business';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { translateOptions } from '@/utils/common';

defineOptions({ name: 'ConfigOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.AutoboxScaffold.ConfigItem | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', { default: false });

const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() =>
  props.operateType === 'add' ? $t('page.autobox.config.editConfig') : $t('page.autobox.config.editConfig')
);

type Model = Pick<Api.AutoboxScaffold.ConfigItem, 'configName' | 'configCode' | 'configValue' | 'status'>;

const model = ref<Model>({ configName: '', configCode: '', configValue: '', status: null });

const rules: Record<'configValue' | 'status', App.Global.FormRule> = {
  configValue: defaultRequiredRule,
  status: defaultRequiredRule
};

watch(visible, () => {
  if (visible.value && props.rowData) {
    Object.assign(model.value, jsonClone(props.rowData));
    restoreValidation();
  }
});

async function handleSubmit() {
  await validate();
  window.$message?.success($t('common.updateSuccess'));
  visible.value = false;
  emit('submitted');
}
</script>

<template>
  <NDrawer v-model:show="visible" :width="360">
    <NDrawerContent :title="title" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
        <NFormItem :label="$t('page.autobox.config.configName')">
          <NInput :value="model.configName" disabled />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.config.configCode')">
          <NInput :value="model.configCode" disabled />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.config.configValue')" path="configValue">
          <NInput v-model:value="model.configValue" :placeholder="$t('page.autobox.config.form.configValue')" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.common.status.enable')" path="status">
          <NRadioGroup v-model:value="model.status">
            <NRadio v-for="item in translateOptions(enableStatusOptions)" :key="item.value" :value="item.value">
              {{ item.label }}
            </NRadio>
          </NRadioGroup>
        </NFormItem>
      </NForm>
      <template #footer>
        <NButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
