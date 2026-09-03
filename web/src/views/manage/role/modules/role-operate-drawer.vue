<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { jsonClone } from '@sa/utils';
import { useBoolean } from '@sa/hooks';
import { enabledFlagOptions } from '@/constants/business';
import {
  fetchCheckRoleCodeUnique,
  fetchCheckRoleNameUnique,
  fetchCreateRole,
  fetchSaveRoleMenu,
  fetchUpdateRole
} from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { useAuthStore } from '@/store/modules/auth';
import { useRouteStore } from '@/store/modules/route';
import { $t } from '@/locales';
import MenuAuthModal from './menu-auth-modal.vue';

defineOptions({
  name: 'RoleOperateDrawer'
});

interface Props {
  /** the type of operation */
  operateType: NaiveUI.TableOperateType;
  /** the edit row data */
  rowData?: Api.SystemManage.Role | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const authStore = useAuthStore();
const routeStore = useRouteStore();
const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();
const { bool: menuAuthVisible, setTrue: openMenuAuthModal } = useBoolean();

const title = computed(() => {
  const titles: Record<NaiveUI.TableOperateType, string> = {
    add: $t('page.manage.role.addRole'),
    edit: $t('page.manage.role.editRole')
  };
  return titles[props.operateType];
});

type Model = {
  roleName: string;
  roleCode: string;
  orderNum: number;
  isEnabled: Api.SystemManage.EnabledFlag;
};

const model = ref(createDefaultModel());
/** 菜单授权草稿；null 表示未改，提交时不写菜单 */
const draftMenuIds = ref<string[] | null>(null);

function createDefaultModel(): Model {
  return {
    roleName: '',
    roleCode: '',
    orderNum: 0,
    // 表单默认禁用，仍允许手动改启用
    isEnabled: 0
  };
}

const rules: Record<keyof Model, App.Global.FormRule | App.Global.FormRule[]> = {
  roleName: defaultRequiredRule,
  roleCode: [
    defaultRequiredRule,
    {
      // 字母数字下划线连字符
      pattern: /^[a-zA-Z0-9_-]+$/,
      message: $t('page.manage.role.form.roleCode'),
      trigger: 'blur'
    }
  ],
  orderNum: defaultRequiredRule,
  isEnabled: defaultRequiredRule
};

const roleId = computed(() => props.rowData?.roleId || '');

const isEdit = computed(() => props.operateType === 'edit');

function handleInitModel() {
  model.value = createDefaultModel();
  draftMenuIds.value = null;

  if (props.operateType === 'edit' && props.rowData) {
    const row = jsonClone(props.rowData);
    model.value = {
      roleName: row.roleName,
      roleCode: row.roleCode,
      orderNum: row.orderNum ?? 0,
      isEnabled: row.isEnabled ?? 0
    };
  }
}

function closeDrawer() {
  visible.value = false;
}

function handleMenuAuthConfirm(menuIds: string[]) {
  draftMenuIds.value = menuIds;
}

async function handleSubmit() {
  await validate();

  const { data: nameOk, error: nameErr } = await fetchCheckRoleNameUnique({ roleName: model.value.roleName });
  if (nameErr) return;
  if (nameOk === false && !(props.operateType === 'edit' && props.rowData?.roleName === model.value.roleName)) {
    window.$message?.error($t('page.manage.role.form.roleName'));
    return;
  }

  const { data: codeOk, error: codeErr } = await fetchCheckRoleCodeUnique({ roleCode: model.value.roleCode });
  if (codeErr) return;
  if (codeOk === false && !(props.operateType === 'edit' && props.rowData?.roleCode === model.value.roleCode)) {
    window.$message?.error($t('page.manage.role.form.roleCode'));
    return;
  }

  const body: Api.SystemManage.RoleInsert = {
    roleName: model.value.roleName,
    roleCode: model.value.roleCode,
    orderNum: model.value.orderNum,
    isEnabled: model.value.isEnabled
  };

  if (props.operateType === 'edit' && props.rowData) {
    const { error } = await fetchUpdateRole({ ...body, roleId: props.rowData.roleId });
    if (error) return;

    // 菜单授权随角色确认一并落库
    if (draftMenuIds.value !== null) {
      const { error: menuErr } = await fetchSaveRoleMenu({
        roleId: props.rowData.roleId,
        menuIds: draftMenuIds.value.join(',')
      });
      if (menuErr) return;

      await authStore.refreshUserInfo();
      await routeStore.reloadAuthRoute();
    }

    window.$message?.success($t('common.updateSuccess'));
  } else {
    const { error } = await fetchCreateRole(body);
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
  }
});
</script>

<template>
  <NDrawer v-model:show="visible" display-directive="show" :width="360">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <NForm ref="formRef" :model="model" :rules="rules">
        <NFormItem :label="$t('page.manage.role.roleName')" path="roleName">
          <NInput v-model:value="model.roleName" :placeholder="$t('page.manage.role.form.roleName')" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.role.roleCode')" path="roleCode">
          <NInput v-model:value="model.roleCode" :placeholder="$t('page.manage.role.form.roleCode')" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.role.orderNum')" path="orderNum">
          <NInputNumber v-model:value="model.orderNum" class="w-full" :placeholder="$t('page.manage.role.form.orderNum')" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.role.roleStatus')" path="isEnabled">
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
      <NSpace v-if="isEdit">
        <NButton @click="openMenuAuthModal">{{ $t('page.manage.role.menuAuth') }}</NButton>
        <MenuAuthModal
          v-model:visible="menuAuthVisible"
          :role-id="roleId"
          :draft-menu-ids="draftMenuIds"
          @confirm="handleMenuAuthConfirm"
        />
      </NSpace>
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
