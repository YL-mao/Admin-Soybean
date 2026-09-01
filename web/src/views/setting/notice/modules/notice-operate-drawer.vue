<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { jsonClone } from '@sa/utils';
import { enableStatusOptions } from '@/constants/business';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { translateOptions } from '@/utils/common';

defineOptions({ name: 'NoticeOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.AutoboxScaffold.Notice | null;
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
  props.operateType === 'add' ? $t('page.autobox.notice.addNotice') : $t('page.autobox.notice.editNotice')
);

type Model = Pick<Api.AutoboxScaffold.Notice, 'noticeTitle' | 'status'>;

const model = ref<Model>({ noticeTitle: '', status: null });

const rules: Record<'noticeTitle' | 'status', App.Global.FormRule> = {
  noticeTitle: defaultRequiredRule,
  status: defaultRequiredRule
};

watch(visible, () => {
  if (visible.value) {
    model.value = { noticeTitle: '', status: null };
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
  <NDrawer v-model:show="visible" :width="360">
    <NDrawerContent :title="title" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="90">
        <NFormItem :label="$t('page.autobox.notice.noticeTitle')" path="noticeTitle">
          <NInput v-model:value="model.noticeTitle" :placeholder="$t('page.autobox.notice.form.noticeTitle')" />
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
