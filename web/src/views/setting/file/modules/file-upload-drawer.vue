<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import type { UploadFileInfo } from 'naive-ui';
import {
  fetchCheckFileNameUnique,
  fetchGetFileUploadRules,
  fetchUploadFile
} from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { FILE_ROOT_FOLDER_ID } from './shared';

defineOptions({ name: 'FileUploadDrawer' });

interface Props {
  /** 当前左侧选中目录；根目录上传时传 0 */
  folderId: string;
  folderLabel: string;
  /** 父级探活成功后传入，打开时不再二次请求，避免连点竞态先开后关 */
  rules?: Api.SystemManage.FileUploadRules | null;
}

const props = withDefaults(defineProps<Props>(), {
  rules: null
});

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', { default: false });

const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

type Model = {
  fileScene: string;
  needLogin: 0 | 1;
};

const model = ref<Model>({
  fileScene: 'image',
  needLogin: 1
});

const fileList = ref<UploadFileInfo[]>([]);
const submitting = ref(false);
const accept = ref('');
const maxMb = ref(10);
const cachedRules = ref<Api.SystemManage.FileUploadRules | null>(null);

const sceneOptions = computed(() => [
  { label: $t('page.autobox.file.sceneImage'), value: 'image' },
  { label: $t('page.autobox.file.sceneDocument'), value: 'document' },
  { label: $t('page.autobox.file.sceneExcel'), value: 'excel' }
]);

const folderTip = computed(() =>
  props.folderId === FILE_ROOT_FOLDER_ID
    ? $t('page.autobox.file.uploadToUnclassified')
    : props.folderLabel
);

const rules = {
  fileScene: defaultRequiredRule,
  needLogin: defaultRequiredRule
};

function applyRules(data: Api.SystemManage.FileUploadRules) {
  cachedRules.value = data;
  maxMb.value = data.maxFileSizeMb || 10;
  applyAccept(model.value.fileScene, data);
}

async function loadRules() {
  const { data, error } = await fetchGetFileUploadRules();
  // 抽屉已关时丢弃迟到响应，避免把后续打开又关掉
  if (!visible.value) return;
  if (error || !data) {
    visible.value = false;
    return;
  }
  applyRules(data);
}

function applyAccept(scene: string, rulesData?: Api.SystemManage.FileUploadRules | null) {
  if (!rulesData) return;
  const map: Record<string, string[]> = {
    image: rulesData.imageExtensions,
    document: rulesData.documentExtensions,
    excel: rulesData.excelExtensions
  };
  const exts = map[scene] || [];
  accept.value = exts.map(ext => `.${ext}`).join(',');
}

watch(
  () => model.value.fileScene,
  scene => {
    if (!cachedRules.value) return;
    applyAccept(scene, cachedRules.value);
    fileList.value = [];
  }
);

function handleBeforeUpload(options: { file: UploadFileInfo; fileList: UploadFileInfo[] }) {
  const raw = options.file.file;
  if (!raw) return false;
  if (raw.size > maxMb.value * 1024 * 1024) {
    window.$message?.error($t('page.autobox.file.fileTooLarge', { size: maxMb.value }));
    return false;
  }
  fileList.value = [options.file];
  return false;
}

async function doUpload(forceOverwrite: boolean) {
  const raw = fileList.value[0]?.file;
  if (!raw) {
    window.$message?.error($t('page.autobox.file.form.pickFile'));
    return;
  }
  submitting.value = true;
  const { error } = await fetchUploadFile(raw, {
    folderId: props.folderId,
    fileScene: model.value.fileScene,
    needLogin: model.value.needLogin,
    forceOverwrite
  });
  submitting.value = false;
  if (error) return;
  window.$message?.success($t('common.addSuccess'));
  visible.value = false;
  emit('submitted');
}

async function handleSubmit() {
  await validate();
  const raw = fileList.value[0]?.file;
  if (!raw) {
    window.$message?.error($t('page.autobox.file.form.pickFile'));
    return;
  }
  const { data: unique, error } = await fetchCheckFileNameUnique(props.folderId, raw.name);
  if (error) return;
  if (unique === false) {
    window.$dialog?.warning({
      title: $t('common.tip'),
      content: $t('page.autobox.file.overwriteConfirm'),
      positiveText: $t('common.confirm'),
      negativeText: $t('common.cancel'),
      onPositiveClick: () => doUpload(true)
    });
    return;
  }
  await doUpload(false);
}

watch(visible, async val => {
  if (!val) return;
  model.value = { fileScene: 'image', needLogin: 1 };
  fileList.value = [];
  cachedRules.value = null;
  restoreValidation();
  // 优先用父级已探活规则，避免二次请求与连点竞态
  if (props.rules) {
    applyRules(props.rules);
    return;
  }
  await loadRules();
});
</script>

<template>
  <NDrawer v-model:show="visible" :width="560">
    <NDrawerContent :title="$t('page.autobox.file.uploadFile')" closable>
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
        <NFormItem :label="$t('page.autobox.file.currentFolder')">
          <NText>{{ folderTip }}</NText>
        </NFormItem>
        <NFormItem :label="$t('page.autobox.file.fileScene')" path="fileScene">
          <NSelect v-model:value="model.fileScene" :options="sceneOptions" />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.file.needLogin')" path="needLogin">
          <NRadioGroup v-model:value="model.needLogin">
            <NRadio :value="1">{{ $t('common.yesOrNo.yes') }}</NRadio>
            <NRadio :value="0">{{ $t('common.yesOrNo.no') }}</NRadio>
          </NRadioGroup>
        </NFormItem>
        <NFormItem :label="$t('page.autobox.file.pickFile')">
          <NUpload
            :file-list="fileList"
            :max="1"
            :accept="accept"
            :default-upload="false"
            @before-upload="handleBeforeUpload"
            @remove="() => (fileList = [])"
          >
            <NButton>{{ $t('page.autobox.file.form.pickFile') }}</NButton>
          </NUpload>
        </NFormItem>
        <NText depth="3" class="text-12px">
          {{ $t('page.autobox.file.uploadHint', { size: maxMb, accept }) }}
        </NText>
      </NForm>
      <template #footer>
        <NButton type="primary" :loading="submitting" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
