<script setup lang="ts">
import { reactive, toRef, watch } from 'vue';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { fetchUpdateUserPwd } from '@/service/api';
import { $t } from '@/locales';

defineOptions({
  name: 'UserResetPwdModal'
});

interface Props {
  /** 当前操作用户 */
  rowData?: Api.SystemManage.User | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const { formRef, validate, restoreValidation } = useNaiveForm();
const { createRequiredRule, createConfirmPwdRule } = useFormRules();

const model = reactive({
  userPassword: '',
  confirmPassword: ''
});

/** 对齐后端 PasswordPolicyService：8～64 位，字母+数字+特殊字符 */
const passwordPolicyRule: App.Global.FormRule = {
  validator: (_rule, value: string) => {
    if (!value) {
      return new Error($t('page.manage.user.form.userPassword'));
    }
    if (value.length < 8 || value.length > 64) {
      return new Error($t('page.manage.user.passwordPolicy'));
    }
    if (!/[A-Za-z]/.test(value) || !/\d/.test(value) || !/[^A-Za-z0-9]/.test(value)) {
      return new Error($t('page.manage.user.passwordPolicy'));
    }
    return true;
  },
  trigger: ['input', 'blur']
};

const rules = {
  userPassword: [createRequiredRule($t('page.manage.user.form.userPassword')), passwordPolicyRule],
  confirmPassword: createConfirmPwdRule(toRef(model, 'userPassword'))
};

watch(visible, val => {
  if (val) {
    model.userPassword = '';
    model.confirmPassword = '';
    restoreValidation();
  }
});

function closeModal() {
  visible.value = false;
}

async function handleSubmit() {
  await validate();
  if (!props.rowData?.userId) {
    return;
  }
  // 管理员重置密码；成功后后端会踢掉该用户会话
  const { error } = await fetchUpdateUserPwd({
    userId: props.rowData.userId,
    userPassword: model.userPassword
  });
  if (error) return;
  window.$message?.success($t('common.updateSuccess'));
  closeModal();
  emit('submitted');
}
</script>

<template>
  <NModal
    v-model:show="visible"
    preset="card"
    :title="$t('page.manage.user.resetPwd')"
    class="w-480px"
    :mask-closable="false"
  >
    <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
      <NFormItem :label="$t('page.manage.user.userAccount')">
        <NInput :value="rowData?.userAccount || ''" disabled />
      </NFormItem>
      <NFormItem :label="$t('page.manage.user.userName')">
        <NInput :value="rowData?.userName || ''" disabled />
      </NFormItem>
      <NFormItem :label="$t('page.manage.user.userPassword')" path="userPassword">
        <NInput
          v-model:value="model.userPassword"
          type="password"
          show-password-on="click"
          :placeholder="$t('page.manage.user.form.userPassword')"
        />
      </NFormItem>
      <NFormItem :label="$t('page.manage.user.confirmPassword')" path="confirmPassword">
        <NInput
          v-model:value="model.confirmPassword"
          type="password"
          show-password-on="click"
          :placeholder="$t('page.manage.user.form.confirmPassword')"
        />
      </NFormItem>
    </NForm>
    <template #footer>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
