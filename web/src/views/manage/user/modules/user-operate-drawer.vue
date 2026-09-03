<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import type { TreeSelectOption } from 'naive-ui';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { userSexOptions } from '@/constants/business';
import {
  fetchCheckUserAccountUnique,
  fetchCreateUser,
  fetchGetDeptOptions,
  fetchGetPostOptions,
  fetchGetRoleOptions,
  fetchUpdateUser
} from '@/service/api';
import { $t } from '@/locales';
import { buildDeptOptionTree } from '@/views/org/dept/modules/shared';

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
  deptId: string | null;
  postId: string | null;
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
    deptId: null,
    postId: null,
    roleIdList: []
  };
}

const rules: Record<'userAccount' | 'userName', App.Global.FormRule> = {
  userAccount: defaultRequiredRule,
  userName: defaultRequiredRule
};

const roleOptions = ref<CommonType.Option<string>[]>([]);
const postOptions = ref<CommonType.Option<string>[]>([]);
const deptOptions = ref<TreeSelectOption[]>([]);

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

async function getPostOptions() {
  const { error, data } = await fetchGetPostOptions();
  if (error || !data) {
    postOptions.value = [];
    return;
  }
  postOptions.value = data.map(item => ({
    label: item.postName,
    value: item.postId
  }));
}

function mapDeptTreeOptions(nodes: Api.SystemManage.DeptOption[]): TreeSelectOption[] {
  return nodes.map(node => ({
    key: node.deptId,
    label: node.deptName,
    children: node.children?.length ? mapDeptTreeOptions(node.children) : undefined
  }));
}

async function getDeptOptions() {
  const { error, data } = await fetchGetDeptOptions();
  if (error || !data) {
    deptOptions.value = [];
    return;
  }
  deptOptions.value = mapDeptTreeOptions(buildDeptOptionTree(data));
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
      deptId: row.deptId || null,
      postId: row.postId || null,
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
    deptId: model.value.deptId || null,
    postId: model.value.postId || null,
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
    void Promise.all([getRoleOptions(), getDeptOptions(), getPostOptions()]);
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
        <NFormItem :label="$t('page.manage.user.deptName')" path="deptId">
          <NTreeSelect
            v-model:value="model.deptId"
            clearable
            filterable
            :options="deptOptions"
            key-field="key"
            label-field="label"
            default-expand-all
            :placeholder="$t('page.manage.user.form.deptId')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.manage.user.postName')" path="postId">
          <NSelect
            v-model:value="model.postId"
            clearable
            filterable
            :options="postOptions"
            :placeholder="$t('page.manage.user.form.postId')"
          />
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
