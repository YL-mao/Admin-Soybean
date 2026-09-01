<script setup lang="ts">
import { computed, ref, watch } from 'vue';

import { jsonClone } from '@sa/utils';

import { enableStatusOptions } from '@/constants/business';

import { useFormRules, useNaiveForm } from '@/hooks/common/form';

import { $t } from '@/locales';

import { translateOptions } from '@/utils/common';

defineOptions({ name: 'PostOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;

  rowData?: Api.AutoboxScaffold.Post | null;
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
    add: $t('page.autobox.post.addPost'),

    edit: $t('page.autobox.post.editPost')
  };

  return titles[props.operateType];
});

type Model = Pick<
  Api.AutoboxScaffold.Post,
  'postName' | 'postCode' | 'postTypeName' | 'orderNum' | 'status' | 'remark'
>;

const postTypeOptions = computed(() => [
  { label: $t('page.autobox.post.postTypeOptions.tech'), value: $t('page.autobox.post.postTypeOptions.tech') },

  { label: $t('page.autobox.post.postTypeOptions.func'), value: $t('page.autobox.post.postTypeOptions.func') }
]);

const model = ref<Model>(createDefaultModel());

function createDefaultModel(): Model {
  return {
    postName: '',

    postCode: '',

    postTypeName: '',

    orderNum: 1,

    status: null,

    remark: ''
  };
}

const rules: Record<'postName' | 'postCode' | 'status', App.Global.FormRule> = {
  postName: defaultRequiredRule,

  postCode: defaultRequiredRule,

  status: defaultRequiredRule
};

function handleInitModel() {
  model.value = createDefaultModel();

  if (props.operateType === 'edit' && props.rowData) {
    Object.assign(model.value, jsonClone(props.rowData));
  }
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();

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
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
        <NFormItem :label="$t('page.autobox.post.postName')" path="postName">
          <NInput v-model:value="model.postName" :placeholder="$t('page.autobox.post.form.postName')" />
        </NFormItem>

        <NFormItem :label="$t('page.autobox.post.postCode')" path="postCode">
          <NInput v-model:value="model.postCode" :placeholder="$t('page.autobox.post.form.postCode')" />
        </NFormItem>

        <NFormItem :label="$t('page.autobox.post.postType')" path="postTypeName">
          <NSelect
            v-model:value="model.postTypeName"
            :options="postTypeOptions"
            :placeholder="$t('page.autobox.post.form.postType')"
          />
        </NFormItem>

        <NFormItem :label="$t('page.autobox.post.orderNum')" path="orderNum">
          <NInputNumber v-model:value="model.orderNum" class="w-full" :min="0" />
        </NFormItem>

        <NFormItem :label="$t('page.manage.common.status.enable')" path="status">
          <NRadioGroup v-model:value="model.status">
            <NRadio v-for="item in translateOptions(enableStatusOptions)" :key="item.value" :value="item.value">
              {{ item.label }}
            </NRadio>
          </NRadioGroup>
        </NFormItem>

        <NFormItem :label="$t('page.autobox.post.remark')" path="remark">
          <NInput v-model:value="model.remark" type="textarea" :placeholder="$t('page.autobox.post.form.remark')" />
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
