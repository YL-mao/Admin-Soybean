<script setup lang="ts">
import { computed } from 'vue';
import { $t } from '@/locales';

defineOptions({ name: 'FileCard' });

interface Props {
  item: Api.AutoboxScaffold.FileItem;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'delete', id: number): void;
}

const emit = defineEmits<Emits>();

const fileIcon = computed(() => {
  if (props.item.isImage) return 'mdi:file-image-outline';

  const ext = props.item.fileName.split('.').pop()?.toLowerCase();
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

  return iconMap[ext || ''] || 'mdi:file-document-outline';
});

const iconClass = computed(() => {
  if (props.item.isImage) return 'text-primary';

  const ext = props.item.fileName.split('.').pop()?.toLowerCase();
  if (ext === 'pdf') return 'text-error';
  if (ext === 'doc' || ext === 'docx') return 'text-info';
  if (ext === 'xls' || ext === 'xlsx') return 'text-success';

  return 'text-#999';
});

function handleDelete() {
  emit('delete', props.item.id);
}
</script>

<template>
  <div
    class="flex-col overflow-hidden border border-solid border-#efeff5 rd-8px bg-container transition-all duration-300 dark:border-#ffffff17 hover:border-primary hover:shadow-sm"
  >
    <div class="h-120px flex-center bg-#fafafc dark:bg-#ffffff08">
      <SvgIcon :icon="fileIcon" class="text-48px" :class="iconClass" />
    </div>

    <div class="flex-col gap-8px p-12px">
      <div class="truncate text-14px font-medium" :title="item.fileName">
        {{ item.fileName }}
      </div>

      <div class="flex items-center gap-8px">
        <NTag size="small" :bordered="false" type="primary">
          {{ item.sceneName }}
        </NTag>
        <span class="text-12px text-#999">{{ item.fileSizeLabel }}</span>
      </div>

      <div class="flex items-center justify-between pt-4px">
        <span class="text-12px text-#999">{{ item.createTime }}</span>
        <NPopconfirm @positive-click="handleDelete">
          <template #trigger>
            <NButton type="error" ghost size="tiny">
              {{ $t('common.delete') }}
            </NButton>
          </template>
          {{ $t('common.confirmDelete') }}
        </NPopconfirm>
      </div>
    </div>
  </div>
</template>
