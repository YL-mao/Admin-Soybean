<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { enabledFlagOptions, postTypeOptions } from '@/constants/business';
import {
  fetchCheckPostCodeUnique,
  fetchCheckPostNameUnique,
  fetchCreatePost,
  fetchUpdatePost
} from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';

defineOptions({ name: 'PostOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.SystemManage.Post | null;
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

type Model = {
  postName: string;
  postCode: string;
  postType: Api.SystemManage.PostType | null;
  orderNum: number;
  isEnabled: Api.SystemManage.EnabledFlag;
};

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    postName: '',
    postCode: '',
    postType: null,
    orderNum: 0,
    // 表单默认禁用，仍允许手动改启用
    isEnabled: 0
  };
}

const rules: Record<'postName' | 'postCode' | 'postType' | 'orderNum' | 'isEnabled', App.Global.FormRule | App.Global.FormRule[]> = {
  postName: defaultRequiredRule,
  postCode: [
    defaultRequiredRule,
    {
      pattern: /^[a-zA-Z0-9_-]+$/,
      message: $t('page.autobox.post.form.postCode'),
      trigger: 'blur'
    }
  ],
  postType: defaultRequiredRule,
  orderNum: defaultRequiredRule,
  isEnabled: defaultRequiredRule
};

function handleInitModel() {
  model.value = createDefaultModel();
  if (props.operateType === 'edit' && props.rowData) {
    const row = props.rowData;
    model.value = {
      postName: row.postName,
      postCode: row.postCode,
      postType: row.postType,
      orderNum: row.orderNum ?? 0,
      isEnabled: row.isEnabled ?? 0
    };
  }
}

async function handleSubmit() {
  await validate();

  const { data: nameOk, error: nameErr } = await fetchCheckPostNameUnique({ postName: model.value.postName });
  if (nameErr) return;
  if (nameOk === false && !(props.operateType === 'edit' && props.rowData?.postName === model.value.postName)) {
    window.$message?.error($t('page.autobox.post.form.postName'));
    return;
  }

  const { data: codeOk, error: codeErr } = await fetchCheckPostCodeUnique({ postCode: model.value.postCode });
  if (codeErr) return;
  if (codeOk === false && !(props.operateType === 'edit' && props.rowData?.postCode === model.value.postCode)) {
    window.$message?.error($t('page.autobox.post.form.postCode'));
    return;
  }

  const body: Api.SystemManage.PostInsert = {
    postName: model.value.postName,
    postCode: model.value.postCode,
    postType: model.value.postType!,
    orderNum: model.value.orderNum,
    isEnabled: model.value.isEnabled
  };

  if (props.operateType === 'edit' && props.rowData) {
    const { error } = await fetchUpdatePost({ ...body, postId: props.rowData.postId });
    if (error) return;
    window.$message?.success($t('common.updateSuccess'));
  } else {
    const { error } = await fetchCreatePost(body);
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
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
        <NFormItem :label="$t('page.autobox.post.postName')" path="postName">
          <NInput v-model:value="model.postName" :placeholder="$t('page.autobox.post.form.postName')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.post.postCode')" path="postCode">
          <NInput v-model:value="model.postCode" :placeholder="$t('page.autobox.post.form.postCode')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.post.postType')" path="postType">
          <NSelect
            v-model:value="model.postType"
            :options="postTypeOptions.map(item => ({ value: item.value, label: $t(item.label) }))"
            :placeholder="$t('page.autobox.post.form.postType')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.post.orderNum')" path="orderNum">
          <NInputNumber v-model:value="model.orderNum" class="w-full" :min="0" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.common.status.enable')" path="isEnabled">
          <NRadioGroup v-model:value="model.isEnabled">
            <NRadio
              v-for="item in enabledFlagOptions"
              :key="item.value"
              :value="item.value"
              :label="$t(item.label)"
            />
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
