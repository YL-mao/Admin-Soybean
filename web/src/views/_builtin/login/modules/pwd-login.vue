<script setup lang="ts">
import { computed, onMounted, reactive, ref, useTemplateRef } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { localStg } from '@/utils/storage';
import { $t } from '@/locales';
import ImageCaptcha from '@/components/custom/image-captcha.vue';

defineOptions({
  name: 'PwdLogin'
});

const authStore = useAuthStore();
const { formRef, validate } = useNaiveForm();
const imageCaptchaRef = useTemplateRef<InstanceType<typeof ImageCaptcha>>('imageCaptchaRef');
const rememberMe = ref(false);

interface FormModel {
  userAccount: string;
  userPassword: string;
  captcha: string;
}

const model: FormModel = reactive({
  userAccount: '',
  userPassword: '',
  captcha: ''
});

const rules = computed<Record<keyof FormModel, App.Global.FormRule[]>>(() => {
  const { formRules, createRequiredRule } = useFormRules();

  return {
    userAccount: formRules.userName,
    // 登录只校验非空，密码复杂度在设置/修改密码处校验
    userPassword: [createRequiredRule($t('form.pwd.required'))],
    captcha: [createRequiredRule($t('page.login.pwdLogin.imageCodePlaceholder'))]
  };
});

onMounted(() => {
  const remembered = localStg.get('loginRemember');
  if (remembered?.userAccount) {
    model.userAccount = remembered.userAccount;
    rememberMe.value = true;
  }
});

async function handleSubmit() {
  await validate();

  const pass = await authStore.login(model.userAccount, model.userPassword, model.captcha);

  if (pass) {
    // 记住我：只存本地账号，不存密码
    if (rememberMe.value) {
      localStg.set('loginRemember', { userAccount: model.userAccount });
    } else {
      localStg.remove('loginRemember');
    }
  } else {
    model.captcha = '';
    imageCaptchaRef.value?.refresh();
  }
}
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" size="large" :show-label="false" @keyup.enter="handleSubmit">
    <NFormItem path="userAccount">
      <NInput v-model:value="model.userAccount" :placeholder="$t('page.login.common.userAccountPlaceholder')" />
    </NFormItem>
    <NFormItem path="userPassword">
      <NInput
        v-model:value="model.userPassword"
        type="password"
        show-password-on="click"
        :placeholder="$t('page.login.common.passwordPlaceholder')"
      />
    </NFormItem>
    <NFormItem path="captcha">
      <ImageCaptcha ref="imageCaptchaRef" v-model:value="model.captcha" />
    </NFormItem>
    <NSpace vertical :size="24">
      <NCheckbox v-model:checked="rememberMe">{{ $t('page.login.pwdLogin.rememberMe') }}</NCheckbox>
      <NButton type="primary" size="large" round block :loading="authStore.loginLoading" @click="handleSubmit">
        {{ $t('common.confirm') }}
      </NButton>
    </NSpace>
  </NForm>
</template>

<style scoped></style>
