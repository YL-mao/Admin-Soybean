<script setup lang="ts">
import { computed, ref, watch } from 'vue';

import { jsonClone } from '@sa/utils';

import { enableStatusOptions } from '@/constants/business';

import { useFormRules, useNaiveForm } from '@/hooks/common/form';

import { $t } from '@/locales';

import { translateOptions } from '@/utils/common';

defineOptions({ name: 'DeptOperateDrawer' });

export type DeptOperateType = NaiveUI.TableOperateType | 'addChild';

interface Props {
  operateType: DeptOperateType;

  rowData?: Api.AutoboxScaffold.Dept | null;
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
  const titles: Record<DeptOperateType, string> = {
    add: $t('page.autobox.dept.addDept'),

    edit: $t('page.autobox.dept.editDept'),

    addChild: $t('page.autobox.dept.addChildDept')
  };

  return titles[props.operateType];
});

const parentLabel = computed(() => {
  if (props.operateType === 'addChild' && props.rowData) {
    return props.rowData.deptName;
  }

  if (props.operateType === 'edit' && props.rowData) {
    return props.rowData.deptName;
  }

  return $t('page.autobox.dept.parentDept');
});

type Model = Pick<
  Api.AutoboxScaffold.Dept,
  'deptName' | 'deptLeader' | 'leaderPhone' | 'leaderEmail' | 'orderNum' | 'status'
>;

const model = ref<Model>(createDefaultModel());

function createDefaultModel(): Model {
  return {
    deptName: '',

    deptLeader: '',

    leaderPhone: '',

    leaderEmail: '',

    orderNum: 1,

    status: null
  };
}

const rules: Record<'deptName' | 'status', App.Global.FormRule> = {
  deptName: defaultRequiredRule,

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

  window.$message?.success(props.operateType === 'edit' ? $t('common.updateSuccess') : $t('common.addSuccess'));

  visible.value = false;

  emit('submitted');
}
</script>

<template>
  <NDrawer v-model:show="visible" display-directive="show" :width="360">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
        <NFormItem :label="$t('page.autobox.dept.parentDept')">
          <NInput :value="parentLabel" readonly />
        </NFormItem>

        <NFormItem :label="$t('page.autobox.dept.deptName')" path="deptName">
          <NInput v-model:value="model.deptName" :placeholder="$t('page.autobox.dept.form.deptName')" />
        </NFormItem>

        <NFormItem :label="$t('page.autobox.dept.deptLeader')" path="deptLeader">
          <NInput v-model:value="model.deptLeader" :placeholder="$t('page.autobox.dept.form.deptLeader')" />
        </NFormItem>

        <NFormItem :label="$t('page.autobox.dept.leaderPhone')" path="leaderPhone">
          <NInput v-model:value="model.leaderPhone" :placeholder="$t('page.autobox.dept.form.leaderPhone')" />
        </NFormItem>

        <NFormItem :label="$t('page.autobox.dept.leaderEmail')" path="leaderEmail">
          <NInput v-model:value="model.leaderEmail" :placeholder="$t('page.autobox.dept.form.leaderEmail')" />
        </NFormItem>

        <NFormItem :label="$t('page.autobox.dept.orderNum')" path="orderNum">
          <NInputNumber v-model:value="model.orderNum" class="w-full" :min="0" />
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
