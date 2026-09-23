<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import type { TreeOption } from 'naive-ui';
import {
  fetchCheckFileRef,
  fetchDeleteFile,
  fetchDeleteFolder,
  fetchGetFileList,
  fetchGetFileUploadRules,
  fetchGetFolderTree
} from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import ConfigGroupDrawer from '@/components/custom/config-group-drawer.vue';
import FileCard from './modules/file-card.vue';
import FileDetailDrawer from './modules/file-detail-drawer.vue';
import FileSearch from './modules/file-search.vue';
import FileUploadDrawer from './modules/file-upload-drawer.vue';
import FolderOperateDrawer, { type FolderOperateType } from './modules/folder-operate-drawer.vue';
import {
  FILE_ROOT_FOLDER_ID,
  mapFolderTreeOptions
} from './modules/shared';

defineOptions({ name: 'SettingFile' });

const { hasAuth } = useAuth();

const folderList = ref<Api.SystemManage.FolderOption[]>([]);
const treeOptions = ref<TreeOption[]>([]);
const treeLoading = ref(false);
const selectedKeys = ref<Array<string | number>>([FILE_ROOT_FOLDER_ID]);
const expandedKeys = ref<Array<string | number>>([FILE_ROOT_FOLDER_ID]);

const uploadConfigVisible = ref(false);
const folderDrawerVisible = ref(false);
const folderOperateType = ref<FolderOperateType>('add');
const folderRowData = ref<Api.SystemManage.FolderOption | null>(null);
const folderDefaultParentId = ref(FILE_ROOT_FOLDER_ID);

const uploadDrawerVisible = ref(false);
/** 上传探活进行中，防止连点并发请求 */
const uploadOpening = ref(false);
/** 探活成功的规则快照，交给抽屉避免二次请求竞态 */
const uploadRulesSnapshot = ref<Api.SystemManage.FileUploadRules | null>(null);
const detailVisible = ref(false);
const detailRow = ref<Api.SystemManage.FileResource | null>(null);

const searchParams = ref<Api.SystemManage.FileSearchParams>({
  current: 1,
  size: 12,
  folderId: FILE_ROOT_FOLDER_ID,
  originalName: null
});

const selectedFolderId = computed(() => String(selectedKeys.value[0] ?? FILE_ROOT_FOLDER_ID));

const selectedFolder = computed(() => {
  if (selectedFolderId.value === FILE_ROOT_FOLDER_ID) return null;
  return folderList.value.find(item => item.folderId === selectedFolderId.value) || null;
});

const selectedFolderLabel = computed(() => {
  if (selectedFolderId.value === FILE_ROOT_FOLDER_ID) return $t('page.autobox.file.rootFolder');
  return selectedFolder.value?.folderName || $t('page.autobox.file.title');
});

/** 虚拟根与内置目录不可改删 */
const canEditSelectedFolder = computed(() => {
  if (!selectedFolder.value) return false;
  return selectedFolder.value.isBuiltin !== 1;
});

const fileCardTitle = computed(() => `${$t('page.autobox.file.title')} - ${selectedFolderLabel.value}`);

const { data, getData, getDataByPage, loading, mobilePagination, pagination } = useNaivePaginatedTable({
  api: () =>
    hasAuth('system:file:select')
      ? fetchGetFileList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.SystemManage.FileResource>()),
  transform: response =>
    backendPageTransform<Api.SystemManage.FileResource>(
      response,
      searchParams.value.current || 1,
      searchParams.value.size || 12
    ),
  paginationProps: {
    pageSizes: [12, 18, 24, 36]
  },
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  // 卡片墙不用表格列，占位满足 hook 约束并锁定行类型
  columns: (): NaiveUI.TableColumn<Api.SystemManage.FileResource>[] => [
    { key: 'fileId', title: 'fileId' },
    { key: 'originalName', title: $t('page.autobox.file.fileName') }
  ]
});

// hook 默认 pageSize=10，与本页 12 对齐，避免分页器与请求条数不一致
pagination.pageSize = 12;
async function loadFolderTree() {
  if (!hasAuth('system:file:tree')) {
    treeOptions.value = mapFolderTreeOptions([], $t('page.autobox.file.rootFolder'));
    return;
  }
  treeLoading.value = true;
  const { data: list, error } = await fetchGetFolderTree();
  treeLoading.value = false;
  if (error) return;
  folderList.value = list || [];
  treeOptions.value = mapFolderTreeOptions(folderList.value, $t('page.autobox.file.rootFolder'));
  if (!expandedKeys.value.includes(FILE_ROOT_FOLDER_ID)) {
    expandedKeys.value = [FILE_ROOT_FOLDER_ID, ...expandedKeys.value];
  }
}

function handleTreeSelect(keys: Array<string | number>) {
  // cancelable=false 时一般不会空；兜底保持当前选中，避免误跳回根目录
  if (!keys.length) return;
  const nextId = String(keys[0]);
  selectedKeys.value = [nextId];
  searchParams.value.folderId = nextId;
  getDataByPage();
}

function openFolderAdd() {
  folderOperateType.value = 'add';
  folderRowData.value = null;
  folderDefaultParentId.value =
    selectedFolderId.value === FILE_ROOT_FOLDER_ID ? FILE_ROOT_FOLDER_ID : selectedFolderId.value;
  folderDrawerVisible.value = true;
}

function openFolderEdit() {
  if (!canEditSelectedFolder.value || !selectedFolder.value) return;
  folderOperateType.value = 'edit';
  folderRowData.value = selectedFolder.value;
  folderDrawerVisible.value = true;
}

function handleFolderDelete() {
  if (!canEditSelectedFolder.value || !selectedFolder.value) return;
  window.$dialog?.warning({
    title: $t('common.tip'),
    content: $t('page.autobox.file.deleteFolderConfirm'),
    positiveText: $t('common.confirm'),
    negativeText: $t('common.cancel'),
    onPositiveClick: async () => {
      const { error } = await fetchDeleteFolder(selectedFolder.value!.folderId);
      if (error) return;
      window.$message?.success($t('common.deleteSuccess'));
      selectedKeys.value = [FILE_ROOT_FOLDER_ID];
      searchParams.value.folderId = FILE_ROOT_FOLDER_ID;
      await loadFolderTree();
      getDataByPage();
    }
  });
}

/** 先探活上传开关/规则；连点忽略，关闭时绝不打开抽屉 */
async function openUpload() {
  if (uploadOpening.value || uploadDrawerVisible.value) return;
  uploadOpening.value = true;
  try {
    const { data, error } = await fetchGetFileUploadRules();
    if (error || !data) {
      uploadRulesSnapshot.value = null;
      return;
    }
    uploadRulesSnapshot.value = data;
    uploadDrawerVisible.value = true;
  } finally {
    uploadOpening.value = false;
  }
}

function handleUploadDrawerVisible(val: boolean) {
  uploadDrawerVisible.value = val;
  if (!val) uploadRulesSnapshot.value = null;
}

function openDetail(item: Api.SystemManage.FileResource) {
  detailRow.value = item;
  detailVisible.value = true;
}

async function handleFileDelete(item: Api.SystemManage.FileResource) {
  if (!hasAuth('system:file:delete')) return;
  const { data: refResult, error: refError } = await fetchCheckFileRef(item.fileId);
  if (refError) return;
  if (refResult?.referenced) {
    window.$dialog?.warning({
      title: $t('common.tip'),
      content: refResult.message || $t('page.autobox.file.deleteReferenced'),
      positiveText: $t('common.confirm'),
      negativeText: $t('common.cancel'),
      onPositiveClick: async () => {
        const { error } = await fetchDeleteFile(item.fileId);
        if (error) return;
        window.$message?.success($t('common.deleteSuccess'));
        getData();
      }
    });
    return;
  }
  const { error } = await fetchDeleteFile(item.fileId);
  if (error) return;
  window.$message?.success($t('common.deleteSuccess'));
  getData();
}

function handleFolderSubmitted() {
  loadFolderTree();
}

function handleFileSubmitted() {
  getData();
}

function handleFileUpdated(row: Api.SystemManage.FileResource) {
  detailRow.value = row;
}

onMounted(() => {
  loadFolderTree();
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <div class="flex-1-hidden flex gap-16px overflow-hidden lt-sm:flex-col lt-sm:overflow-auto">
      <!-- 左侧：目录树（点选；增改删在标题栏） -->
      <div class="w-280px flex-col-stretch gap-16px overflow-hidden lt-sm:w-full">
        <NCard
          :title="$t('page.autobox.file.folderTitle')"
          :bordered="false"
          size="small"
          class="card-wrapper sm:flex-1-hidden"
        >
          <template #header-extra>
            <NSpace :size="8" wrap justify="end">
              <NButton
                v-if="hasAuth('system:file:folderInsert')"
                size="tiny"
                ghost
                type="primary"
                @click="openFolderAdd"
              >
                {{ $t('common.add') }}
              </NButton>
              <NButton
                v-if="hasAuth('system:file:folderUpdate')"
                size="tiny"
                ghost
                type="primary"
                :disabled="!canEditSelectedFolder"
                @click="openFolderEdit"
              >
                {{ $t('common.edit') }}
              </NButton>
              <NButton
                v-if="hasAuth('system:file:folderDelete')"
                size="tiny"
                ghost
                type="error"
                :disabled="!canEditSelectedFolder"
                @click="handleFolderDelete"
              >
                {{ $t('common.delete') }}
              </NButton>
              <NButton size="tiny" :loading="treeLoading" @click="loadFolderTree">
                <template #icon>
                  <icon-mdi-refresh class="text-icon" :class="{ 'animate-spin': treeLoading }" />
                </template>
              </NButton>
            </NSpace>
          </template>

          <NSpin :show="treeLoading" class="h-full">
            <NEmpty
              v-if="!hasAuth('system:file:tree')"
              class="h-full flex-center"
              :description="$t('common.noPermission')"
            />
            <NTree
              v-else
              block-line
              selectable
              :cancelable="false"
              :selected-keys="selectedKeys"
              :expanded-keys="expandedKeys"
              :data="treeOptions"
              key-field="key"
              label-field="label"
              class="h-full overflow-auto"
              @update:selected-keys="handleTreeSelect"
              @update:expanded-keys="keys => (expandedKeys = keys)"
            />
          </NSpin>
        </NCard>
      </div>

      <!-- 右侧：文件卡片墙 -->
      <div class="min-w-0 flex-1 flex-col-stretch gap-16px overflow-hidden lt-sm:w-full lt-sm:min-h-420px">
        <FileSearch v-model:model="searchParams" @search="getDataByPage" />

        <!-- flex + content/footer：网格区可滚动，分页固定在底部不被裁切 -->
        <NCard
          :title="fileCardTitle"
          :bordered="false"
          size="small"
          class="card-wrapper sm:flex-1-hidden flex flex-col"
          content-class="min-h-0 flex-1 overflow-hidden"
          :content-style="{ display: 'flex', flexDirection: 'column', paddingBottom: '8px' }"
          footer-class="shrink-0"
        >
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
              <NButton
                size="small"
                ghost
                type="primary"
                :loading="uploadOpening"
                :disabled="uploadOpening"
                @click="openUpload"
              >
                <template #icon>
                  <icon-ic-round-upload class="text-icon" />
                </template>
                {{ $t('page.autobox.file.uploadFile') }}
              </NButton>
              <NButton size="small" @click="getData">
                <template #icon>
                  <icon-mdi-refresh class="text-icon" :class="{ 'animate-spin': loading }" />
                </template>
                {{ $t('common.refresh') }}
              </NButton>
            </NSpace>
          </template>

          <div class="min-h-0 flex-1 overflow-auto">
            <NSpin :show="loading">
              <NEmpty
                v-if="!hasAuth('system:file:select')"
                class="h-280px flex-center"
                :description="$t('common.noPermission')"
              />
              <NEmpty
                v-else-if="!data.length"
                class="h-280px flex-center"
                :description="$t('common.noData')"
              />
              <!-- 多列紧凑墙：对齐 Layui col-md2 密度 -->
              <NGrid
                v-else
                cols="2 s:3 m:4 l:5 xl:6 2xl:8"
                responsive="screen"
                :x-gap="12"
                :y-gap="12"
              >
                <NGi v-for="item in data" :key="item.fileId">
                  <FileCard
                    :item="item"
                    @detail="openDetail(item)"
                    @delete="handleFileDelete(item)"
                  />
                </NGi>
              </NGrid>
            </NSpin>
          </div>

          <template #footer>
            <div class="flex justify-end">
              <NPagination v-bind="mobilePagination" />
            </div>
          </template>
        </NCard>
      </div>
    </div>

    <ConfigGroupDrawer
      v-model:visible="uploadConfigVisible"
      config-group="upload"
      perm-code="system:config:upload"
      :title="$t('page.autobox.file.uploadConfig')"
    />

    <FolderOperateDrawer
      v-model:visible="folderDrawerVisible"
      :operate-type="folderOperateType"
      :folder-list="folderList"
      :row-data="folderRowData"
      :default-parent-id="folderDefaultParentId"
      @submitted="handleFolderSubmitted"
    />

    <FileUploadDrawer
      :visible="uploadDrawerVisible"
      :folder-id="selectedFolderId"
      :folder-label="selectedFolderLabel"
      :rules="uploadRulesSnapshot"
      @update:visible="handleUploadDrawerVisible"
      @submitted="handleFileSubmitted"
    />

    <FileDetailDrawer
      v-model:visible="detailVisible"
      :row-data="detailRow"
      @submitted="handleFileSubmitted"
      @updated="handleFileUpdated"
    />
  </div>
</template>
