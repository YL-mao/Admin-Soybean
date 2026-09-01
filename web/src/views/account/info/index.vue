<script setup lang="ts">
import { ref } from 'vue';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';

defineOptions({ name: 'AccountInfo' });

const { formRef, validate } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const model = ref({
  userName: '管理员',
  userAccount: 'admin',
  userEmail: 'admin@example.com',
  userPhone: '13800000000',
  deptName: '总部',
  postName: '开发工程师'
});

const rules = {
  userName: defaultRequiredRule,
  userEmail: defaultRequiredRule
};

async function handleSubmit() {
  await validate();
  window.$message?.success($t('common.updateSuccess'));
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px">
    <NCard
      :title="$t('page.autobox.account.infoTitle')"
      :bordered="false"
      size="small"
      class="card-wrapper max-w-640px"
    >
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
        <NFormItem :label="$t('page.autobox.account.userAccount')">
          <NInput :value="model.userAccount" disabled />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.account.userName')" path="userName">
          <NInput v-model:value="model.userName" :placeholder="$t('page.autobox.account.form.userName')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.account.userEmail')" path="userEmail">
          <NInput v-model:value="model.userEmail" :placeholder="$t('page.autobox.account.form.userEmail')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.account.userPhone')" path="userPhone">
          <NInput v-model:value="model.userPhone" :placeholder="$t('page.autobox.account.form.userPhone')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.account.deptName')">
          <NInput :value="model.deptName" disabled />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.account.postName')">
          <NInput :value="model.postName" disabled />
        </NFormItem>
        <NFormItem>
          <NButton type="primary" @click="handleSubmit">{{ $t('common.update') }}</NButton>
        </NFormItem>
      </NForm>
    </NCard>
  </div>
</template>
