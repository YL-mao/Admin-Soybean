<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { jsonClone } from '@sa/utils';
import { enableStatusOptions } from '@/constants/business';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { translateOptions } from '@/utils/common';

defineOptions({ name: 'FilterOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.AutoboxScaffold.Filter | null;
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
  props.operateType === 'add' ? $t('page.autobox.filter.addFilter') : $t('page.autobox.filter.editFilter')
);

type Model = Pick<Api.AutoboxScaffold.Filter, 'valueLabel' | 'filterDesc' | 'status'>;

const model = ref<Model>({ valueLabel: '', filterDesc: '', status: null });

const rules: Record<'valueLabel' | 'status', App.Global.FormRule> = {
  valueLabel: defaultRequiredRule,
  status: defaultRequiredRule
};

watch(visible, () => {
  if (visible.value) {
    model.value = { valueLabel: '', filterDesc: '', status: null };
    if (props.operateType === 'edit' && props.rowData) {
      Object.assign(model.value, jsonClone(props.rowData));
    }
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
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="90">
        <NFormItem :label="$t('page.autobox.filter.valueLabel')" path="valueLabel">
          <NInput v-model:value="model.valueLabel" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.filter.filterDesc')" path="filterDesc">
          <NInput v-model:value="model.filterDesc" type="textarea" />
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
