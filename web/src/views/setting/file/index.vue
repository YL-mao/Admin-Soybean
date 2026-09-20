<script setup lang="ts">
import { ref } from 'vue';
import { fetchFileList } from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { defaultTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigGroupDrawer from '@/components/custom/config-group-drawer.vue';
import FileSearch from './modules/file-search.vue';
import FileCard from './modules/file-card.vue';

defineOptions({ name: 'SettingFile' });

const { hasAuth } = useAuth();
const uploadConfigVisible = ref(false);

const searchParams = ref<Api.AutoboxScaffold.FileSearchParams>({
  current: 1,
  size: 10,
  fileName: null,
  sceneName: null
});

const { data, getData, getDataByPage, loading, mobilePagination } = useNaivePaginatedTable({
  api: () => fetchFileList(searchParams.value),
  transform: response => defaultTransform<Api.AutoboxScaffold.FileItem>(response),
  paginationProps: {
    pageSizes: [8, 12, 16, 24]
  },
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: (): NaiveUI.TableColumn<Api.AutoboxScaffold.FileItem>[] => [
    { key: 'fileName', title: $t('page.autobox.file.fileName') }
  ]
});

function handleDelete(_id: number) {
  window.$message?.success($t('common.deleteSuccess'));
  getData();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <FileSearch v-model:model="searchParams" @search="getDataByPage" />

    <NCard :title="$t('page.autobox.file.title')" :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
      <template #header-extra>
        <NSpace wrap justify="end" class="lt-sm:w-200px">
          <NButton
            v-if="hasAuth('system:config:upload')"
            size="small"
            ghost
            type="primary"
            @click="uploadConfigVisible = true"
          >
            {{ $t('page.autobox.file.uploadConfig') }}
          </NButton>
          <NButton size="small" ghost type="primary">
            <template #icon>
              <icon-ic-round-upload class="text-icon" />
            </template>
            {{ $t('common.add') }}
          </NButton>
          <NButton size="small" @click="getData">
            <template #icon>
              <icon-mdi-refresh class="text-icon" :class="{ 'animate-spin': loading }" />
            </template>
            {{ $t('common.refresh') }}
          </NButton>
        </NSpace>
      </template>

      <div class="h-full flex-col-stretch gap-16px overflow-hidden">
        <NSpin :show="loading" class="flex-1-hidden">
          <div class="h-full overflow-auto">
            <NEmpty v-if="!data.length" class="h-full flex-center" :description="$t('common.noData')" />
            <NGrid v-else cols="s:1 m:2 l:3 xl:4" responsive="screen" :x-gap="16" :y-gap="16">
              <NGi v-for="item in data" :key="item.id">
                <FileCard :item="item" @delete="handleDelete" />
              </NGi>
            </NGrid>
          </div>
        </NSpin>

        <div class="flex justify-end">
          <NPagination v-bind="mobilePagination" />
        </div>
      </div>
    </NCard>

    <ConfigGroupDrawer
      v-model:visible="uploadConfigVisible"
      config-group="upload"
      perm-code="system:config:upload"
      :title="$t('page.autobox.file.uploadConfig')"
    />
  </div>
</template>
