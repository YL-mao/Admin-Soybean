<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import type { TreeSelectOption } from 'naive-ui';
import { enabledFlagOptions } from '@/constants/business';
import {
  fetchCheckDeptNameUnique,
  fetchCreateDept,
  fetchGetDeptParentOptions,
  fetchUpdateDept
} from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { buildDeptOptionTree } from './shared';

defineOptions({ name: 'DeptOperateDrawer' });

export type DeptOperateType = NaiveUI.TableOperateType | 'addChild';

interface Props {
  operateType: DeptOperateType;
  rowData?: Api.SystemManage.Dept | null;
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

type Model = {
  parentId: string;
  deptName: string;
  deptLeader: string;
  leaderPhone: string;
  leaderEmail: string;
  orderNum: number;
  isEnabled: Api.SystemManage.EnabledFlag;
};

const model = ref(createDefaultModel());
const parentOptions = ref<TreeSelectOption[]>([]);

function createDefaultModel(): Model {
  return {
    parentId: '0',
    deptName: '',
    deptLeader: '',
    leaderPhone: '',
    leaderEmail: '',
    orderNum: 0,
    isEnabled: 0
  };
}

const rules: Record<'parentId' | 'deptName' | 'orderNum' | 'isEnabled', App.Global.FormRule> = {
  parentId: defaultRequiredRule,
  deptName: defaultRequiredRule,
  orderNum: defaultRequiredRule,
  isEnabled: defaultRequiredRule
};

function mapParentTreeOptions(nodes: Api.SystemManage.DeptOption[]): TreeSelectOption[] {
  return nodes.map(node => ({
    key: node.deptId,
    label: node.deptName,
    children: node.children?.length ? mapParentTreeOptions(node.children) : undefined
  }));
}

async function loadParentOptions() {
  const { error, data } = await fetchGetDeptParentOptions();
  if (error || !data) {
    parentOptions.value = [];
    return;
  }
  // 顶级「无上级」
  const tree = buildDeptOptionTree(data);
  parentOptions.value = [
    { key: '0', label: $t('page.autobox.dept.parentDept') },
    ...mapParentTreeOptions(tree)
  ];
}

function handleInitModel() {
  model.value = createDefaultModel();

  if (props.operateType === 'addChild' && props.rowData) {
    model.value.parentId = props.rowData.deptId;
  }

  if (props.operateType === 'edit' && props.rowData) {
    const row = props.rowData;
    model.value = {
      parentId: row.parentId || '0',
      deptName: row.deptName || '',
      deptLeader: row.deptLeader || '',
      leaderPhone: row.leaderPhone || '',
      leaderEmail: row.leaderEmail || '',
      orderNum: row.orderNum ?? 0,
      isEnabled: row.isEnabled ?? 0
    };
  }
}

async function handleSubmit() {
  await validate();

  const { data: nameOk, error: nameErr } = await fetchCheckDeptNameUnique({
    parentId: model.value.parentId || '0',
    deptName: model.value.deptName
  });
  if (nameErr) return;
  if (
    nameOk === false &&
    !(
      props.operateType === 'edit' &&
      props.rowData?.deptName === model.value.deptName &&
      (props.rowData.parentId || '0') === (model.value.parentId || '0')
    )
  ) {
    window.$message?.error($t('page.autobox.dept.form.deptName'));
    return;
  }

  const body: Api.SystemManage.DeptInsert = {
    parentId: model.value.parentId || '0',
    deptName: model.value.deptName,
    orderNum: model.value.orderNum,
    deptLeader: model.value.deptLeader || null,
    leaderPhone: model.value.leaderPhone || null,
    leaderEmail: model.value.leaderEmail || null,
    isEnabled: model.value.isEnabled
  };

  if (props.operateType === 'edit' && props.rowData) {
    const { error } = await fetchUpdateDept({ ...body, deptId: props.rowData.deptId });
    if (error) return;
    window.$message?.success($t('common.updateSuccess'));
  } else {
    const { error } = await fetchCreateDept(body);
    if (error) return;
    window.$message?.success($t('common.addSuccess'));
  }

  visible.value = false;
  emit('submitted');
}

watch(visible, async () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
    await loadParentOptions();
  }
});
</script>

<template>
  <NDrawer v-model:show="visible" display-directive="show" :width="360">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
        <NFormItem :label="$t('page.autobox.dept.parentDept')" path="parentId">
          <NTreeSelect
            v-model:value="model.parentId"
            :options="parentOptions"
            key-field="key"
            label-field="label"
            default-expand-all
            :disabled="operateType === 'addChild'"
          />
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
