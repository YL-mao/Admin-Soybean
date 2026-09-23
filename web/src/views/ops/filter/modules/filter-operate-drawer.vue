<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { enabledFlagOptions } from '@/constants/business';
import {
  fetchCreateFilter,
  fetchUpdateFilter
} from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { translateOptions } from '@/utils/common';

defineOptions({ name: 'FilterOperateDrawer' });

/** 与后端 FilterCodes.PERMANENT_EXPIRE 一致 */
const PERMANENT_EXPIRE = '9999-12-31 23:59:59';

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.SystemManage.Filter | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', { default: false });

const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const submitting = ref(false);
const permanent = ref(true);

const title = computed(() =>
  props.operateType === 'add' ? $t('page.autobox.filter.addFilter') : $t('page.autobox.filter.editFilter')
);

type Model = {
  filterType: string;
  filterValue: string;
  filterDesc: string;
  policyMode: string;
  expireTime: string | null;
  isEnabled: Api.SystemManage.EnabledFlag;
};

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    filterType: 'IP',
    filterValue: '',
    filterDesc: '',
    policyMode: 'BLACK',
    expireTime: null,
    // 表单默认禁用，仍允许手动改启用
    isEnabled: 0
  };
}

const rules: Record<string, App.Global.FormRule | App.Global.FormRule[]> = {
  filterType: defaultRequiredRule,
  filterValue: defaultRequiredRule,
  policyMode: defaultRequiredRule,
  isEnabled: defaultRequiredRule,
  expireTime: [
    {
      trigger: ['blur', 'change'],
      validator: () => {
        if (permanent.value) return true;
        if (!model.value.expireTime) {
          return new Error($t('page.autobox.filter.form.expireTime'));
        }
        if (new Date(model.value.expireTime.replace(/-/g, '/')).getTime() <= Date.now()) {
          return new Error($t('page.autobox.filter.form.expireTimePast'));
        }
        return true;
      }
    }
  ]
};

const filterTypeOptions = computed(() => {
  const all = [
    { label: $t('page.autobox.filter.typeIp'), value: 'IP' },
    { label: $t('page.autobox.filter.typeUser'), value: 'USER_ID' }
  ];
  // 白名单仅 IP
  if (model.value.policyMode === 'WHITE') {
    return all.filter(item => item.value === 'IP');
  }
  return all;
});

const policyModeOptions = computed(() => [
  { label: $t('page.autobox.filter.modeBlack'), value: 'BLACK' },
  { label: $t('page.autobox.filter.modeWhite'), value: 'WHITE' }
]);

watch(
  () => model.value.policyMode,
  mode => {
    if (mode === 'WHITE' && model.value.filterType !== 'IP') {
      model.value.filterType = 'IP';
    }
  }
);

watch(visible, () => {
  if (!visible.value) return;
  if (props.operateType === 'edit' && props.rowData) {
    const row = props.rowData;
    permanent.value = row.permanent === 1;
    model.value = {
      filterType: row.filterType,
      filterValue: row.filterValue,
      filterDesc: row.filterDesc || '',
      policyMode: row.policyMode,
      expireTime: row.permanent === 1 ? null : row.expireTime,
      isEnabled: row.isEnabled
    };
  } else {
    model.value = createDefaultModel();
    permanent.value = true;
  }
  restoreValidation();
});

/** 禁用今天之前的日期（过期时间须晚于当前） */
function isExpireDateDisabled(ts: number) {
  const start = new Date();
  start.setHours(0, 0, 0, 0);
  return ts < start.getTime();
}

async function handleSubmit() {
  await validate();
  submitting.value = true;
  const expireTime = permanent.value ? PERMANENT_EXPIRE : model.value.expireTime!;
  const payload = {
    filterType: model.value.filterType,
    filterValue: model.value.filterValue.trim(),
    filterDesc: model.value.filterDesc || null,
    policyMode: model.value.policyMode,
    expireTime,
    isEnabled: model.value.isEnabled
  };
  const { error } =
    props.operateType === 'add'
      ? await fetchCreateFilter(payload)
      : await fetchUpdateFilter({ ...payload, filterId: props.rowData!.filterId });
  submitting.value = false;
  if (error) return;
  window.$message?.success(
    props.operateType === 'add' ? $t('common.addSuccess') : $t('common.updateSuccess')
  );
  visible.value = false;
  emit('submitted');
}
</script>

<template>
  <NDrawer v-model:show="visible" :width="420">
    <NDrawerContent :title="title" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="90">
        <NFormItem :label="$t('page.autobox.filter.policyMode')" path="policyMode">
          <NSelect v-model:value="model.policyMode" :options="policyModeOptions" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.filter.filterType')" path="filterType">
          <NSelect v-model:value="model.filterType" :options="filterTypeOptions" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.filter.valueLabel')" path="filterValue">
          <NInput
            v-model:value="model.filterValue"
            :placeholder="
              model.filterType === 'USER_ID'
                ? $t('page.autobox.filter.form.filterValueUser')
                : $t('page.autobox.filter.form.filterValue')
            "
          />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.filter.permanent')">
          <NSwitch v-model:value="permanent" />
        </NFormItem>
        <NFormItem
          v-if="!permanent"
          :label="$t('page.autobox.filter.expireTime')"
          path="expireTime"
        >
          <NDatePicker
            v-model:formatted-value="model.expireTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            clearable
            class="w-full"
            :is-date-disabled="isExpireDateDisabled"
          />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.filter.filterDesc')" path="filterDesc">
          <NInput v-model:value="model.filterDesc" type="textarea" :rows="2" />
        </NFormItem>
        <NFormItem :label="$t('page.manage.common.status.enable')" path="isEnabled">
          <NRadioGroup v-model:value="model.isEnabled">
            <NRadio
              v-for="item in translateOptions(enabledFlagOptions)"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </NRadio>
          </NRadioGroup>
        </NFormItem>
      </NForm>
      <template #footer>
        <NButton type="primary" :loading="submitting" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </NButton>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
