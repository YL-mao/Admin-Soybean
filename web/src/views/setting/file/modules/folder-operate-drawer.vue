<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import type { TreeSelectOption } from 'naive-ui';
import { fetchCreateFolder, fetchUpdateFolder } from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { FILE_ROOT_FOLDER_ID, mapFolderParentSelectOptions } from './shared';

defineOptions({ name: 'FolderOperateDrawer' });

export type FolderOperateType = 'add' | 'edit';

interface Props {
  operateType: FolderOperateType;
  /** 平铺目录列表，供上级树 */
  folderList: Api.SystemManage.FolderOption[];
  /** 新增时默认上级；编辑时为当前行 */
  rowData?: Api.SystemManage.FolderOption | null;
  /** 新增时预填的上级目录 */
  defaultParentId?: string;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', { default: false });

const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() =>
  props.operateType === 'add' ? $t('page.setting.file.addFolder') : $t('page.setting.file.editFolder')
);

type Model = {
  parentId: string;
  folderName: string;
  orderNum: number;
};

const model = ref<Model>(createDefaultModel());
const parentOptions = ref<TreeSelectOption[]>([]);
const submitting = ref(false);

function createDefaultModel(): Model {
  return {
    parentId: FILE_ROOT_FOLDER_ID,
    folderName: '',
    orderNum: 0
  };
}

const rules = {
  parentId: defaultRequiredRule,
  folderName: defaultRequiredRule,
  orderNum: defaultRequiredRule
};

function handleInitModel() {
  const excludeId = props.operateType === 'edit' ? props.rowData?.folderId : null;
  parentOptions.value = mapFolderParentSelectOptions(
    props.folderList,
    $t('page.setting.file.rootFolder'),
    excludeId
  );
  model.value = createDefaultModel();
  if (props.operateType === 'edit' && props.rowData) {
    model.value = {
      parentId: props.rowData.parentId || FILE_ROOT_FOLDER_ID,
      folderName: props.rowData.folderName,
      orderNum: props.rowData.orderNum ?? 0
    };
  } else if (props.defaultParentId) {
    model.value.parentId = props.defaultParentId;
  }
}

async function handleSubmit() {
  await validate();
  submitting.value = true;
  const payload = {
    parentId: model.value.parentId,
    folderName: model.value.folderName,
    orderNum: model.value.orderNum ?? 0
  };
  const { error } =
    props.operateType === 'add'
      ? await fetchCreateFolder(payload)
      : await fetchUpdateFolder({ ...payload, folderId: props.rowData!.folderId });
  submitting.value = false;
  if (error) return;
  window.$message?.success(props.operateType === 'add' ? $t('common.addSuccess') : $t('common.updateSuccess'));
  visible.value = false;
  emit('submitted');
}

watch(visible, val => {
  if (!val) return;
  handleInitModel();
  restoreValidation();
});
</script>

<template>
  <NDrawer v-model:show="visible" :width="480">
    <NDrawerContent :title="title" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="90">
        <NFormItem :label="$t('page.setting.file.parentFolder')" path="parentId">
          <NTreeSelect
            v-model:value="model.parentId"
            :options="parentOptions"
            key-field="key"
            label-field="label"
            default-expand-all
            :placeholder="$t('page.setting.file.form.parentFolder')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.setting.file.folderName')" path="folderName">
          <NInput v-model:value="model.folderName" :placeholder="$t('page.setting.file.form.folderName')" />
        </NFormItem>
        <NFormItem :label="$t('page.setting.file.orderNum')" path="orderNum">
          <NInputNumber v-model:value="model.orderNum" :min="0" class="w-full" />
        </NFormItem>
      </NForm>
      <template #footer>
        <div class="flex justify-end">
          <NButton type="primary" :loading="submitting" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
        </div>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
