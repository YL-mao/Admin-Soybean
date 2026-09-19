<script setup lang="ts">
import { computed, ref, shallowRef, watch } from 'vue';
import type { TreeOption } from 'naive-ui';
import { fetchGetUserPermDetail } from '@/service/api';
import { $t } from '@/locales';

defineOptions({
  name: 'UserPermDetailModal'
});

interface Props {
  userId: string;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const loading = ref(false);
const detail = ref<Api.SystemManage.UserPermDetail | null>(null);
/** 默认收起；由「全部展开 / 全部收起」控制 */
const expandedKeys = shallowRef<string[]>([]);
const allExpandableKeys = shallowRef<string[]>([]);

/** 收集有子节点的 key，供全部展开 */
function collectExpandableKeys(nodes: TreeOption[], keys: string[] = []) {
  nodes.forEach(node => {
    if (node.children?.length) {
      keys.push(String(node.key));
      collectExpandableKeys(node.children, keys);
    }
  });
  return keys;
}

/** 平铺权限按 parentId 组只读树 */
function buildPermTree(perms: Api.SystemManage.UserPermDetail['perms']): TreeOption[] {
  const map = new Map<string, TreeOption & { parentId: string }>();
  perms.forEach(item => {
    map.set(item.permId, {
      key: item.permId,
      label: item.permName,
      parentId: item.parentId || '0',
      children: []
    });
  });

  const roots: TreeOption[] = [];
  map.forEach(node => {
    const parentId = node.parentId || '0';
    if (parentId === '0' || !map.has(parentId)) {
      roots.push(node);
      return;
    }
    const parent = map.get(parentId)!;
    parent.children = parent.children || [];
    parent.children.push(node);
  });

  const prune = (nodes: TreeOption[]) => {
    nodes.forEach(n => {
      if (n.children?.length) {
        prune(n.children);
      } else {
        delete n.children;
      }
    });
  };
  prune(roots);
  return roots;
}

const treeData = computed(() => (detail.value ? buildPermTree(detail.value.perms) : []));

function expandAll() {
  expandedKeys.value = [...allExpandableKeys.value];
}

function collapseAll() {
  expandedKeys.value = [];
}

async function loadDetail() {
  if (!props.userId) {
    return;
  }
  loading.value = true;
  expandedKeys.value = [];
  allExpandableKeys.value = [];
  // 用户最终权限：多角色并集菜单树
  const { data, error } = await fetchGetUserPermDetail(props.userId);
  loading.value = false;
  if (error) {
    detail.value = null;
    return;
  }
  detail.value = data;
  allExpandableKeys.value = collectExpandableKeys(buildPermTree(data.perms));
}

watch(visible, val => {
  if (val) {
    detail.value = null;
    loadDetail();
  }
});

function closeModal() {
  visible.value = false;
}
</script>

<template>
  <NModal
    v-model:show="visible"
    preset="card"
    :title="$t('page.manage.user.permDetail')"
    class="w-640px"
    :mask-closable="false"
  >
    <NSpin :show="loading">
      <template v-if="detail">
        <NDescriptions bordered :column="1" label-placement="left" size="small" class="mb-16px">
          <NDescriptionsItem :label="$t('page.manage.user.userAccount')">
            {{ detail.userAccount }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.manage.user.userName')">
            {{ detail.userName }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.manage.user.userRole')">
            <NSpace v-if="detail.roles.length" size="small">
              <NTag v-for="role in detail.roles" :key="role.roleId" size="small" type="info">
                {{ role.roleName }}
              </NTag>
            </NSpace>
            <span v-else>{{ $t('common.noData') }}</span>
          </NDescriptionsItem>
        </NDescriptions>
        <div class="mb-8px flex items-center justify-between gap-8px">
          <div class="text-14px font-medium">{{ $t('page.manage.user.permTree') }}</div>
          <NSpace v-if="treeData.length" :size="8">
            <NButton size="small" @click="expandAll">{{ $t('page.manage.user.expandAll') }}</NButton>
            <NButton size="small" @click="collapseAll">{{ $t('page.manage.user.collapseAll') }}</NButton>
          </NSpace>
        </div>
        <NTree
          v-if="treeData.length"
          v-model:expanded-keys="expandedKeys"
          block-line
          expand-on-click
          :data="treeData"
          selectable
          class="max-h-360px overflow-auto"
        />
        <NEmpty v-else :description="$t('common.noData')" />
      </template>
      <NEmpty v-else-if="!loading" :description="$t('common.noData')" />
    </NSpin>
    <template #footer>
      <NSpace justify="end">
        <NButton @click="closeModal">{{ $t('common.close') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>
