<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { jsonClone } from '@sa/utils';
import { enableStatusOptions } from '@/constants/business';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { translateOptions } from '@/utils/common';

defineOptions({ name: 'JobOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.AutoboxScaffold.Job | null;
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
  props.operateType === 'add' ? $t('page.autobox.job.addJob') : $t('page.autobox.job.editJob')
);

type Model = Pick<Api.AutoboxScaffold.Job, 'jobName' | 'jobCode' | 'cronExpression' | 'jobDesc' | 'status'>;

const model = ref<Model>(createDefaultModel());

function createDefaultModel(): Model {
  return { jobName: '', jobCode: '', cronExpression: '', jobDesc: '', status: null };
}

const rules: Record<'jobName' | 'jobCode' | 'status', App.Global.FormRule> = {
  jobName: defaultRequiredRule,
  jobCode: defaultRequiredRule,
  status: defaultRequiredRule
};

watch(visible, () => {
  if (visible.value) {
    model.value = createDefaultModel();
    if (props.operateType === 'edit' && props.rowData) {
      Object.assign(model.value, jsonClone(props.rowData));
    }
    restoreValidation();
  }
});

async function handleSubmit() {
  await validate();
  window.$message?.success(props.operateType === 'add' ? $t('common.addSuccess') : $t('common.updateSuccess'));
  visible.value = false;
  emit('submitted');
}
</script>

<template>
  <NDrawer v-model:show="visible" display-directive="show" :width="360">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="110">
        <NFormItem :label="$t('page.autobox.job.jobName')" path="jobName">
          <NInput v-model:value="model.jobName" :placeholder="$t('page.autobox.job.form.jobName')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.job.jobCode')" path="jobCode">
          <NInput v-model:value="model.jobCode" :placeholder="$t('page.autobox.job.form.jobCode')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.job.cronExpression')" path="cronExpression">
          <NInput v-model:value="model.cronExpression" placeholder="0 0 2 * * ?" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.job.jobDesc')" path="jobDesc">
          <NInput v-model:value="model.jobDesc" type="textarea" />
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
        <NSpace justify="end">
          <NButton @click="visible = false">{{ $t('common.cancel') }}</NButton>
          <NButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
        </NSpace>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
