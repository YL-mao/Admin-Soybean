<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { userSexOptions } from '@/constants/business';
import {
  fetchCheckUserAccountUnique,
  fetchCreateUser,
  fetchGetRoleOptions,
  fetchUpdateUser
} from '@/service/api';
import { $t } from '@/locales';

defineOptions({
  name: 'UserOperateDrawer'
});

interface Props {
  /** the type of operation */
  operateType: NaiveUI.TableOperateType;
  /** the edit row data */
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
const { defaultRequiredRule } = useFormRules();

const title = computed(() => {
  const titles: Record<NaiveUI.TableOperateType, string> = {
    add: $t('page.manage.user.addUser'),
    edit: $t('page.manage.user.editUser')
  };
  return titles[props.operateType];
});

type Model = {
  userAccount: string;
  userName: string;
  userSex: Api.SystemManage.UserSex | null;
  userPhone: string;
  userEmail: string;
  roleIdList: string[];
};

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    userAccount: '',
    userName: '',
    userSex: null,
    userPhone: '',
    userEmail: '',
    roleIdList: []
  };
}

const rules: Record<'userAccount' | 'userName', App.Global.FormRule> = {
  userAccount: defaultRequiredRule,
  userName: defaultRequiredRule
};

const roleOptions = ref<CommonType.Option<string>[]>([]);

async function getRoleOptions() {
  const { error, data } = await fetchGetRoleOptions();
  if (error || !data) {
    roleOptions.value = [];
    return;
  }
  roleOptions.value = data.map(item => ({
    label: item.roleName,
    value: item.roleId
  }));
}

function parseRoleIds(roleIds?: string | null) {
  if (!roleIds) return [];
  return roleIds
    .split(',')
    .map(id => id.trim())
    .filter(Boolean);
}

function handleInitModel() {
  model.value = createDefaultModel();

  if (props.operateType === 'edit' && props.rowData) {
    const row = props.rowData;
    model.value = {
      userAccount: row.userAccount || '',
      userName: row.userName || '',
      userSex: row.userSex,
      userPhone: row.userPhone || '',
      userEmail: row.userEmail || '',
      roleIdList: parseRoleIds(row.roleIds)
    };
  }
}

function closeDrawer() {
  visible.value = false;
}

function buildBody(): Api.SystemManage.UserInsert {
  return {
    userAccount: model.value.userAccount,
    userName: model.value.userName,
    userSex: model.value.userSex,
    userEmail: model.value.userEmail || null,
    userPhone: model.value.userPhone || null,
    deptId: props.operateType === 'edit' ? props.rowData?.deptId || null : null,
    postId: props.operateType === 'edit' ? props.rowData?.postId || null : null,
    roleIds: model.value.roleIdList.join(',')
  };
}

async function handleSubmit() {
  await validate();

  const { data: accountOk, error: accountErr } = await fetchCheckUserAccountUnique({
    userAccount: model.value.userAccount
  });
  if (accountErr) return;
  if (
    accountOk === false &&
    !(props.operateType === 'edit' && props.rowData?.userAccount === model.value.userAccount)
  ) {
    window.$message?.error($t('page.manage.user.form.userAccount'));
    return;
  }

  const body = buildBody();
  if (props.operateType === 'edit' && props.rowData) {
    const { error } = await fetchUpdateUser({ ...body, userId: props.rowData.userId });
    if (error) return;
    window.$message?.success($t('common.updateSuccess'));
  } else {
    const { error } = await fetchCreateUser(body);
    if (error) return;
    window.$message?.success($t('common.addSuccess'));
  }

  closeDrawer();
  emit('submitted');
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
    void getRoleOptions();
  }
});
</script>

<template>
  <NDrawer v-model:show="visible" display-directive="show" :width="360">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <NForm ref="formRef" :model="model" :rules="rules">
        <NFormItem :label="$t('page.manage.user.userAccount')" path="userAccount">
          <NInput v-model:value="model.userAccount" :placeholder="$t('page.manage.user.form.userAccount')" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.user.userName')" path="userName">
          <NInput v-model:value="model.userName" :placeholder="$t('page.manage.user.form.userName')" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.user.userSex')" path="userSex">
          <NRadioGroup v-model:value="model.userSex">
            <NRadio v-for="item in userSexOptions" :key="item.value" :value="item.value" :label="$t(item.label)" />
          </NRadioGroup>
        </NFormItem>
        <NFormItem :label="$t('page.manage.user.userPhone')" path="userPhone">
          <NInput v-model:value="model.userPhone" :placeholder="$t('page.manage.user.form.userPhone')" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.user.userEmail')" path="userEmail">
          <NInput v-model:value="model.userEmail" :placeholder="$t('page.manage.user.form.userEmail')" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.user.userRole')" path="roleIdList">
          <NSelect
            v-model:value="model.roleIdList"
            multiple
            filterable
            :options="roleOptions"
            :placeholder="$t('page.manage.user.form.userRole')"
          />
        </NFormItem>
      </NForm>
      <template #footer>
        <NSpace :size="16">
          <NButton @click="closeDrawer">{{ $t('common.cancel') }}</NButton>
          <NButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
        </NSpace>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>

<style scoped></style>
