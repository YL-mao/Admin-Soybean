<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue';
import {
  fetchOverwriteFile,
  fetchUpdateFile
} from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchFileBlobUrl, formatFileSize } from './shared';

defineOptions({ name: 'FileDetailDrawer' });

interface Props {
  rowData?: Api.SystemManage.FileResource | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
  (e: 'updated', row: Api.SystemManage.FileResource): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', { default: false });

const { hasAuth } = useAuth();
const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

type Model = {
  originalName: string;
  needLogin: 0 | 1;
};

const model = ref<Model>({ originalName: '', needLogin: 1 });
const submitting = ref(false);
const previewUrl = ref('');
const previewLoading = ref(false);
const overwriteInput = ref<HTMLInputElement | null>(null);

const rules = {
  originalName: defaultRequiredRule,
  needLogin: defaultRequiredRule
};

const canUpdate = computed(() => hasAuth('system:file:update'));

const isImage = computed(() => {
  const row = props.rowData;
  if (!row) return false;
  if (row.fileScene === 'image') return true;
  if (row.contentType?.startsWith('image/')) return true;
  const suffix = (row.fileSuffix || '').toLowerCase().replace(/^\./, '');
  return ['png', 'jpg', 'jpeg', 'gif', 'webp', 'bmp', 'svg'].includes(suffix);
});

const sceneLabel = computed(() => {
  const scene = props.rowData?.fileScene;
  if (scene === 'image') return $t('page.autobox.file.sceneImage');
  if (scene === 'document') return $t('page.autobox.file.sceneDocument');
  if (scene === 'excel') return $t('page.autobox.file.sceneExcel');
  return scene || '-';
});

function revokePreview() {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value);
    previewUrl.value = '';
  }
}

async function loadPreview() {
  revokePreview();
  if (!props.rowData?.accessUrl || !isImage.value) return;
  previewLoading.value = true;
  try {
    previewUrl.value = await fetchFileBlobUrl(props.rowData.accessUrl);
  } catch {
    window.$message?.error($t('page.autobox.file.previewFailed'));
  } finally {
    previewLoading.value = false;
  }
}

async function handleDownload() {
  if (!props.rowData?.accessUrl) return;
  try {
    const url = await fetchFileBlobUrl(props.rowData.accessUrl);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = props.rowData.originalName || 'download';
    anchor.click();
    URL.revokeObjectURL(url);
  } catch {
    window.$message?.error($t('page.autobox.file.downloadFailed'));
  }
}

async function handleSubmit() {
  if (!props.rowData || !canUpdate.value) return;
  await validate();
  submitting.value = true;
  // 本期不改目录，仍提交当前 folderId
  const { error } = await fetchUpdateFile({
    fileId: props.rowData.fileId,
    folderId: props.rowData.folderId,
    originalName: model.value.originalName,
    needLogin: model.value.needLogin
  });
  submitting.value = false;
  if (error) return;
  window.$message?.success($t('common.updateSuccess'));
  visible.value = false;
  emit('submitted');
}

function triggerOverwrite() {
  overwriteInput.value?.click();
}

async function handleOverwriteChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file || !props.rowData || !canUpdate.value) return;
  submitting.value = true;
  const { data, error } = await fetchOverwriteFile(props.rowData.fileId, file);
  submitting.value = false;
  if (error) return;
  window.$message?.success($t('common.updateSuccess'));
  // 覆盖后回写详情，避免预览/大小仍是旧值
  if (data) {
    model.value.originalName = data.originalName;
    model.value.needLogin = data.needLogin;
    emit('updated', data);
  }
  await loadPreview();
  emit('submitted');
}

watch(visible, val => {
  if (!val) {
    revokePreview();
    return;
  }
  model.value = {
    originalName: props.rowData?.originalName || '',
    needLogin: props.rowData?.needLogin ?? 1
  };
  restoreValidation();
  void loadPreview();
});

onBeforeUnmount(revokePreview);
</script>

<template>
  <NDrawer v-model:show="visible" :width="560">
    <NDrawerContent :title="$t('page.autobox.file.fileDetail')" closable>
      <div v-if="rowData" class="flex-col gap-16px">
        <div v-if="isImage" class="h-200px flex-center overflow-hidden rd-8px bg-#fafafc dark:bg-#ffffff08">
          <NSpin :show="previewLoading">
            <NImage v-if="previewUrl" :src="previewUrl" object-fit="contain" class="max-h-200px" />
            <NEmpty v-else-if="!previewLoading" :description="$t('page.autobox.file.previewFailed')" />
          </NSpin>
        </div>

        <NDescriptions label-placement="left" :column="1" size="small">
          <NDescriptionsItem :label="$t('page.autobox.file.fileName')">
            {{ rowData.originalName }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.autobox.file.fileSize')">
            {{ formatFileSize(rowData.fileSize) }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.autobox.file.sceneName')">
            {{ sceneLabel }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.autobox.file.createTime')">
            {{ rowData.createTime || '-' }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.autobox.file.accessUrl')">
            <NText depth="3" class="break-all text-12px">{{ rowData.accessUrl }}</NText>
          </NDescriptionsItem>
        </NDescriptions>

        <NForm
          v-if="canUpdate"
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="left"
          :label-width="90"
        >
          <NFormItem :label="$t('page.autobox.file.fileName')" path="originalName">
            <NInput v-model:value="model.originalName" :placeholder="$t('page.autobox.file.form.fileName')" />
          </NFormItem>
          <NFormItem :label="$t('page.autobox.file.needLogin')" path="needLogin">
            <NRadioGroup v-model:value="model.needLogin">
              <NRadio :value="1">{{ $t('common.yesOrNo.yes') }}</NRadio>
              <NRadio :value="0">{{ $t('common.yesOrNo.no') }}</NRadio>
            </NRadioGroup>
          </NFormItem>
        </NForm>

        <NSpace>
          <NButton size="small" @click="handleDownload">{{ $t('page.autobox.file.download') }}</NButton>
          <NButton v-if="canUpdate" size="small" @click="triggerOverwrite">
            {{ $t('page.autobox.file.overwrite') }}
          </NButton>
        </NSpace>
        <input ref="overwriteInput" type="file" class="hidden" @change="handleOverwriteChange" />
      </div>

      <template v-if="canUpdate" #footer>
        <div class="flex justify-end">
          <NButton type="primary" :loading="submitting" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
        </div>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
