<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue';
import type { SelectOption } from 'naive-ui';
import {
  fetchCreateNotice,
  fetchGetDeptOptions,
  fetchGetDictOptions,
  fetchGetRoleOptions,
  fetchSearchUser,
  fetchUpdateNotice
} from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import WangEditor from '@/components/custom/wang-editor.vue';

defineOptions({ name: 'NoticeOperateDrawer' });

interface Props {
  operateType: NaiveUI.TableOperateType;
  rowData?: Api.SystemManage.Notice | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', { default: false });

const { hasAuth } = useAuth();
const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() =>
  props.operateType === 'add' ? $t('page.autobox.notice.addNotice') : $t('page.autobox.notice.editNotice')
);

type Model = {
  noticeTitle: string;
  noticeContent: string;
  noticeType: number | null;
  receiverType: number | null;
  receiverIdList: string[];
  noticeDesc: string;
  isSend: 0 | 1;
  orderNum: number;
  expireTime: string | null;
};

const model = ref<Model>(createDefaultModel());
const typeOptions = ref<SelectOption[]>([]);
const receiverTypeOptions = ref<SelectOption[]>([]);
const roleOptions = ref<SelectOption[]>([]);
const deptOptions = ref<SelectOption[]>([]);
const userOptions = ref<SelectOption[]>([]);
const submitting = ref(false);
/** 回填表单时忽略接收范围变更，避免把已选对象清掉 */
const syncingModel = ref(false);
/** 等选项和回填完成后再挂载编辑器，避免先闪旧内容 */
const editorReady = ref(false);

function createDefaultModel(): Model {
  return {
    noticeTitle: '',
    noticeContent: '',
    noticeType: null,
    receiverType: 1,
    receiverIdList: [],
    noticeDesc: '',
    // 默认草稿，仍可手动改为发布
    isSend: 0,
    orderNum: 0,
    expireTime: null
  };
}

const sendOptions = [
  { label: $t('page.autobox.notice.draft'), value: 0 },
  { label: $t('page.autobox.notice.published'), value: 1 }
];

const rules = {
  noticeTitle: defaultRequiredRule,
  noticeContent: defaultRequiredRule,
  noticeType: defaultRequiredRule,
  receiverType: defaultRequiredRule,
  orderNum: defaultRequiredRule,
  isSend: defaultRequiredRule
};

function toOptions(list: Api.SystemManage.DictOption[]) {
  return list.map(item => ({
    label: item.dictDataLabel,
    value: Number(item.dictDataValue)
  }));
}

async function loadOptions() {
  const [typeRes, receiverRes, roleRes, deptRes] = await Promise.all([
    fetchGetDictOptions('sys_notice_type'),
    fetchGetDictOptions('sys_notice_receiver_type'),
    fetchGetRoleOptions(),
    fetchGetDeptOptions()
  ]);
  typeOptions.value = typeRes.data ? toOptions(typeRes.data) : [];
  receiverTypeOptions.value = receiverRes.data ? toOptions(receiverRes.data) : [];
  roleOptions.value = (roleRes.data || []).map(item => ({ label: item.roleName, value: item.roleId }));
  deptOptions.value = (deptRes.data || []).map(item => ({ label: item.deptName, value: item.deptId }));
}

function splitIds(raw?: string | null) {
  if (!raw) return [];
  return raw.split(',').map(item => item.trim()).filter(Boolean);
}

async function handleInitModel() {
  syncingModel.value = true;
  model.value = createDefaultModel();
  userOptions.value = [];
  if (props.operateType === 'edit' && props.rowData) {
    const row = props.rowData;
    const receiverIdList = splitIds(row.receiverIds);
    model.value = {
      noticeTitle: row.noticeTitle,
      noticeContent: row.noticeContent,
      noticeType: row.noticeType,
      receiverType: row.receiverType,
      receiverIdList,
      noticeDesc: row.noticeDesc || '',
      isSend: row.isSend ?? 0,
      orderNum: row.orderNum ?? 0,
      expireTime: row.expireTime
    };
    // 指定个人回显：按用户 ID 检索一次
    if (row.receiverType === 4 && receiverIdList[0]) {
      await searchUser(receiverIdList[0]);
    }
  }
  await nextTick();
  syncingModel.value = false;
}

/** 切换接收范围时清空已选对象，避免角色/部门/用户 ID 串用 */
function onReceiverTypeChange() {
  if (syncingModel.value) return;
  model.value.receiverIdList = [];
  userOptions.value = [];
}

async function searchUser(keyword: string) {
  if (!keyword) {
    userOptions.value = [];
    return;
  }
  if (!hasAuth('system:user:search')) {
    userOptions.value = [];
    return;
  }
  const { data, error } = await fetchSearchUser(keyword);
  if (error || !data) return;
  userOptions.value = data.map(item => ({
    label: item.label || `${item.userName}(${item.userAccount})`,
    value: item.userId
  }));
}

function buildPayload(): Api.SystemManage.NoticeInsert {
  const receiverType = model.value.receiverType ?? 1;
  const receiverIds = receiverType === 1 ? null : model.value.receiverIdList.join(',');
  return {
    noticeTitle: model.value.noticeTitle,
    noticeContent: model.value.noticeContent,
    noticeType: model.value.noticeType as number,
    receiverType,
    receiverIds,
    noticeDesc: model.value.noticeDesc || null,
    isSend: model.value.isSend,
    orderNum: model.value.orderNum,
    expireTime: model.value.expireTime || null
  };
}

async function handleSubmit() {
  await validate();
  if ((model.value.receiverType ?? 1) !== 1 && !model.value.receiverIdList.length) {
    window.$message?.error($t('page.autobox.notice.form.receiverIds'));
    return;
  }
  submitting.value = true;
  const payload = buildPayload();
  const { error } =
    props.operateType === 'add'
      ? await fetchCreateNotice(payload)
      : await fetchUpdateNotice({ ...payload, noticeId: props.rowData!.noticeId });
  submitting.value = false;
  if (error) return;
  window.$message?.success(props.operateType === 'add' ? $t('common.addSuccess') : $t('common.updateSuccess'));
  visible.value = false;
  emit('submitted');
}

watch(visible, async val => {
  if (!val) {
    editorReady.value = false;
    return;
  }
  // 选项加载和回填期间，接收范围变更不要清空已选对象
  syncingModel.value = true;
  editorReady.value = false;
  await loadOptions();
  await handleInitModel();
  restoreValidation();
  editorReady.value = true;
});
</script>

<template>
  <NDrawer v-model:show="visible" :width="900">
    <NDrawerContent :title="title" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="90">
        <NFormItem :label="$t('page.autobox.notice.noticeTitle')" path="noticeTitle">
          <NInput v-model:value="model.noticeTitle" :placeholder="$t('page.autobox.notice.form.noticeTitle')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.notice.noticeContent')" path="noticeContent">
          <!-- 抽屉打开后再挂载，避免隐藏态初始化异常；key 保证增改切换时重建 -->
          <WangEditor
            v-if="visible && editorReady"
            :key="`${operateType}-${rowData?.noticeId || 'new'}`"
            v-model:value="model.noticeContent"
            :height="280"
            :placeholder="$t('page.autobox.notice.form.noticeContent')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.notice.noticeType')" path="noticeType">
          <NSelect
            v-model:value="model.noticeType"
            :options="typeOptions"
            :placeholder="$t('page.autobox.notice.form.noticeType')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.notice.receiverType')" path="receiverType">
          <NSelect
            v-model:value="model.receiverType"
            :options="receiverTypeOptions"
            :placeholder="$t('page.autobox.notice.form.receiverType')"
            @update:value="onReceiverTypeChange"
          />
        </NFormItem>
        <NFormItem v-if="model.receiverType === 2" :label="$t('page.autobox.notice.form.receiverIds')">
          <NSelect v-model:value="model.receiverIdList" multiple :options="roleOptions" />
        </NFormItem>
        <NFormItem v-else-if="model.receiverType === 3" :label="$t('page.autobox.notice.form.receiverIds')">
          <NSelect v-model:value="model.receiverIdList" multiple :options="deptOptions" />
        </NFormItem>
        <NFormItem v-else-if="model.receiverType === 4" :label="$t('page.autobox.notice.form.receiverIds')">
          <NSelect
            :value="model.receiverIdList[0] ?? null"
            filterable
            remote
            clearable
            :options="userOptions"
            :placeholder="$t('page.autobox.notice.form.receiverIds')"
            @update:value="(value: string | null) => (model.receiverIdList = value ? [value] : [])"
            @search="searchUser"
          />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.notice.noticeDesc')">
          <NInput v-model:value="model.noticeDesc" :placeholder="$t('page.autobox.notice.form.noticeDesc')" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.dict.orderNum')" path="orderNum">
          <NInputNumber v-model:value="model.orderNum" :min="0" class="w-full" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.notice.expireTime')">
          <NDatePicker
            v-model:formatted-value="model.expireTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            clearable
            class="w-full"
          />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.notice.publishStatus')" path="isSend">
          <NRadioGroup v-model:value="model.isSend">
            <NRadio v-for="item in sendOptions" :key="item.value" :value="item.value">{{ item.label }}</NRadio>
          </NRadioGroup>
        </NFormItem>
      </NForm>
      <template #footer>
        <NButton type="primary" :loading="submitting" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
