<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue';
import { useAuth } from '@/hooks/business/auth';
import { $t } from '@/locales';
import { resolveBackendAssetUrl } from '@/utils/service';
import { fetchFileBlobUrl, formatFileSize } from './shared';

defineOptions({ name: 'FileCard' });

interface Props {
  item: Api.SystemManage.FileResource;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'detail'): void;
  (e: 'delete'): void;
}

const emit = defineEmits<Emits>();
const { hasAuth } = useAuth();

const thumbUrl = ref('');
const thumbLoading = ref(false);
/** 仅 blob URL 需要 revoke；公开直链不释放 */
const blobOwned = ref(false);

const isImage = computed(() => {
  if (props.item.fileScene === 'image') return true;
  if (props.item.contentType?.startsWith('image/')) return true;
  const suffix = (props.item.fileSuffix || '').toLowerCase().replace(/^\./, '');
  return ['png', 'jpg', 'jpeg', 'gif', 'webp', 'bmp', 'svg'].includes(suffix);
});

const fileIcon = computed(() => {
  if (isImage.value) return 'mdi:file-image-outline';
  const ext = (props.item.fileSuffix || props.item.originalName.split('.').pop() || '')
    .toLowerCase()
    .replace(/^\./, '');
  const iconMap: Record<string, string> = {
    pdf: 'mdi:file-pdf-box',
    doc: 'mdi:file-word-outline',
    docx: 'mdi:file-word-outline',
    xls: 'mdi:file-excel-outline',
    xlsx: 'mdi:file-excel-outline',
    zip: 'mdi:folder-zip-outline',
    rar: 'mdi:folder-zip-outline',
    '7z': 'mdi:folder-zip-outline'
  };
  return iconMap[ext] || 'mdi:file-document-outline';
});

const iconClass = computed(() => {
  if (isImage.value) return 'text-primary';
  const ext = (props.item.fileSuffix || '').toLowerCase().replace(/^\./, '');
  if (ext === 'pdf') return 'text-error';
  if (ext === 'doc' || ext === 'docx') return 'text-info';
  if (ext === 'xls' || ext === 'xlsx') return 'text-success';
  return 'text-#999';
});

const sceneLabel = computed(() => {
  const scene = props.item.fileScene;
  if (scene === 'image') return $t('page.autobox.file.sceneImage');
  if (scene === 'document') return $t('page.autobox.file.sceneDocument');
  if (scene === 'excel') return $t('page.autobox.file.sceneExcel');
  return scene || '-';
});

function revokeThumb() {
  if (blobOwned.value && thumbUrl.value) {
    URL.revokeObjectURL(thumbUrl.value);
  }
  thumbUrl.value = '';
  blobOwned.value = false;
}

async function loadThumb() {
  revokeThumb();
  if (!isImage.value || !props.item.accessUrl) return;

  // 公开文件可直链；需登录的文件必须带头拉取（本仓库关闭 Cookie 读 token）
  if (props.item.needLogin !== 1) {
    thumbUrl.value = resolveBackendAssetUrl(props.item.accessUrl);
    blobOwned.value = false;
    return;
  }

  thumbLoading.value = true;
  try {
    thumbUrl.value = await fetchFileBlobUrl(props.item.accessUrl);
    blobOwned.value = true;
  } catch {
    thumbUrl.value = '';
  } finally {
    thumbLoading.value = false;
  }
}

function handleThumbError() {
  revokeThumb();
}

watch(
  () => [props.item.fileId, props.item.accessUrl, props.item.needLogin, isImage.value] as const,
  () => {
    void loadThumb();
  },
  { immediate: true }
);

onBeforeUnmount(revokeThumb);
</script>

<template>
  <!-- 紧凑卡片：图片直接预览，其它类型用图标 -->
  <div
    class="flex-col cursor-pointer overflow-hidden border border-solid border-#efeff5 rd-6px bg-container transition-all duration-200 dark:border-#ffffff17 hover:border-primary hover:shadow-sm"
    @click="emit('detail')"
  >
    <div class="aspect-square relative flex-center overflow-hidden bg-#fafafc dark:bg-#ffffff08">
      <img
        v-if="thumbUrl"
        :src="thumbUrl"
        :alt="item.originalName"
        class="max-h-full max-w-full object-contain"
        @error="handleThumbError"
      />
      <SvgIcon v-else :icon="fileIcon" class="text-36px" :class="iconClass" />
      <div
        v-if="thumbLoading"
        class="absolute inset-0 flex-center bg-#fafafc/60 dark:bg-#00000040"
      >
        <NSpin :show="true" size="small" />
      </div>
    </div>

    <div class="flex-col gap-4px p-8px">
      <div class="truncate text-13px font-medium leading-18px" :title="item.originalName">
        {{ item.originalName }}
      </div>

      <div class="flex items-center gap-6px">
        <NTag size="tiny" :bordered="false" type="primary">
          {{ sceneLabel }}
        </NTag>
        <span class="truncate text-12px text-#999">{{ formatFileSize(item.fileSize) }}</span>
      </div>

      <div class="flex items-center justify-between gap-4px">
        <span class="truncate text-12px text-#999">{{ item.createTime || '-' }}</span>
        <NPopconfirm v-if="hasAuth('system:file:delete')" @positive-click="emit('delete')">
          <template #trigger>
            <NButton type="error" ghost size="tiny" @click.stop>
              {{ $t('common.delete') }}
            </NButton>
          </template>
          {{ $t('common.confirmDelete') }}
        </NPopconfirm>
      </div>
    </div>
  </div>
</template>
