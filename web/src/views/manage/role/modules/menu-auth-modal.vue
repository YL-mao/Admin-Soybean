<script setup lang="ts">
import { computed, shallowRef, watch } from 'vue';
import type { TreeOption } from 'naive-ui';
import { fetchGetRoleMenuTree } from '@/service/api';
import { $t } from '@/locales';

defineOptions({
  name: 'MenuAuthModal'
});

interface Props {
  /** 角色 ID */
  roleId: string;
  /** 父抽屉暂存勾选；null 表示用接口回显 */
  draftMenuIds?: string[] | null;
}

const props = withDefaults(defineProps<Props>(), {
  draftMenuIds: null
});

interface Emits {
  (e: 'confirm', menuIds: string[]): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

function closeModal() {
  visible.value = false;
}

const title = computed(() => $t('common.edit') + $t('page.manage.role.menuAuth'));

const tree = shallowRef<TreeOption[]>([]);
const checks = shallowRef<string[]>([]);
/** 默认收起；由「全部展开 / 全部收起」控制 */
const expandedKeys = shallowRef<string[]>([]);
const allExpandableKeys = shallowRef<string[]>([]);
/** 全部节点 key，供全选 / 反选 */
const allNodeKeys = shallowRef<string[]>([]);

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

/** 收集树全部节点 key */
function collectAllKeys(nodes: TreeOption[], keys: string[] = []) {
  nodes.forEach(node => {
    keys.push(String(node.key));
    if (node.children?.length) {
      collectAllKeys(node.children, keys);
    }
  });
  return keys;
}

/** 平铺 MenuCheck 组树；优先用草稿勾选，否则用接口 checkArr */
function buildAuthTree(list: Api.SystemManage.MenuCheck[], preferredChecks: string[] | null) {
  const map = new Map<string, TreeOption>();
  const checkedKeys: string[] = [];

  list.forEach(item => {
    map.set(item.menuId, {
      key: item.menuId,
      label: item.menuName,
      children: []
    });
    if (preferredChecks === null && item.checkArr === '1') {
      checkedKeys.push(item.menuId);
    }
  });

  const roots: TreeOption[] = [];
  list.forEach(item => {
    const node = map.get(item.menuId)!;
    const parentId = item.parentId || '0';
    if (parentId === '0' || !map.has(parentId)) {
      roots.push(node);
      return;
    }
    const parent = map.get(parentId)!;
    parent.children = parent.children || [];
    parent.children.push(node);
  });

  pruneEmptyChildren(roots);
  tree.value = roots;
  checks.value = preferredChecks !== null ? [...preferredChecks] : checkedKeys;
  allExpandableKeys.value = collectExpandableKeys(roots);
  allNodeKeys.value = collectAllKeys(roots);
  // 打开时默认收起
  expandedKeys.value = [];
}

function pruneEmptyChildren(nodes: TreeOption[]) {
  nodes.forEach(node => {
    if (node.children?.length) {
      pruneEmptyChildren(node.children);
    } else {
      delete node.children;
    }
  });
}

function expandAll() {
  expandedKeys.value = [...allExpandableKeys.value];
}

function collapseAll() {
  expandedKeys.value = [];
}

function checkAll() {
  checks.value = [...allNodeKeys.value];
}

/** 已勾选与未勾选互换 */
function invertAll() {
  const selected = new Set(checks.value);
  checks.value = allNodeKeys.value.filter(key => !selected.has(key));
}

async function loadTree() {
  if (!props.roleId) {
    tree.value = [];
    checks.value = [];
    expandedKeys.value = [];
    allExpandableKeys.value = [];
    allNodeKeys.value = [];
    return;
  }
  const { error, data } = await fetchGetRoleMenuTree(props.roleId);
  if (error || !data) {
    tree.value = [];
    checks.value = [];
    expandedKeys.value = [];
    allExpandableKeys.value = [];
    allNodeKeys.value = [];
    return;
  }
  buildAuthTree(data, props.draftMenuIds ?? null);
}

/** 只回写父抽屉草稿，不调保存接口 */
function handleConfirm() {
  emit('confirm', [...checks.value]);
  closeModal();
}

watch(visible, val => {
  if (val) {
    void loadTree();
  }
});
</script>

<template>
  <NModal v-model:show="visible" :title="title" preset="card" class="w-480px">
    <NSpace class="pb-12px" :size="8">
      <NButton size="small" @click="expandAll">{{ $t('page.manage.role.expandAll') }}</NButton>
      <NButton size="small" @click="collapseAll">{{ $t('page.manage.role.collapseAll') }}</NButton>
      <NButton size="small" @click="checkAll">{{ $t('page.manage.role.checkAll') }}</NButton>
      <NButton size="small" @click="invertAll">{{ $t('page.manage.role.invertAll') }}</NButton>
    </NSpace>
    <NTree
      v-model:checked-keys="checks"
      v-model:expanded-keys="expandedKeys"
      :data="tree"
      key-field="key"
      label-field="label"
      checkable
      cascade
      expand-on-click
      virtual-scroll
      block-line
      class="h-280px"
    />
    <template #footer>
      <NSpace justify="end">
        <NButton size="small" class="mt-16px" @click="closeModal">
          {{ $t('common.cancel') }}
        </NButton>
        <NButton type="primary" size="small" class="mt-16px" @click="handleConfirm">
          {{ $t('common.confirm') }}
        </NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
