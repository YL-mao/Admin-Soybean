<script setup lang="ts">
import { computed, ref, shallowRef, watch } from 'vue';
import { useRouter } from 'vue-router';
import type { TreeOption } from 'naive-ui';
import type { RouteKey } from '@elegant-router/types';
import { fetchGetRoleMenuTree, fetchSaveRoleMenu } from '@/service/api';
import { useAuthStore } from '@/store/modules/auth';
import { useRouteStore } from '@/store/modules/route';
import { $t } from '@/locales';

defineOptions({
  name: 'MenuAuthModal'
});

interface Props {
  /** 角色 ID */
  roleId: string;
  /** 角色名称，仅用于标题 */
  roleName?: string;
}

const props = withDefaults(defineProps<Props>(), {
  roleName: ''
});

const visible = defineModel<boolean>('visible', {
  default: false
});

const router = useRouter();
const authStore = useAuthStore();
const routeStore = useRouteStore();
const saving = ref(false);

function closeModal() {
  visible.value = false;
}

const title = computed(() =>
  props.roleName ? `${props.roleName} - ${$t('page.manage.role.menuAuth')}` : $t('page.manage.role.menuAuth')
);

const tree = shallowRef<TreeOption[]>([]);
const checks = shallowRef<string[]>([]);
/** 默认收起；由「全部展开 / 全部收起」控制 */
const expandedKeys = shallowRef<string[]>([]);
const allExpandableKeys = shallowRef<string[]>([]);
/** 全部节点 key，供全选 / 反选 */
const allNodeKeys = shallowRef<string[]>([]);
/** menuId -> parentId，确认时补祖先 */
const parentMap = shallowRef<Map<string, string>>(new Map());
/** menuId -> 子 menuId 列表，回显时过滤半选父节点 */
const childrenMap = shallowRef<Map<string, string[]>>(new Map());

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

function collectDescendants(menuId: string, result: string[] = []): string[] {
  const children = childrenMap.value.get(menuId) || [];
  children.forEach(childId => {
    result.push(childId);
    collectDescendants(childId, result);
  });
  return result;
}

/**
 * cascade 回显：若某节点有未勾选子孙，不要把它放进 checked-keys，
 * 否则会连带勾选全部子孙；交给树自己显示半选。
 */
function toCascadeSafeChecks(candidateIds: string[]): string[] {
  const checked = new Set(candidateIds);
  return candidateIds.filter(id => {
    const descendants = collectDescendants(id);
    if (!descendants.length) {
      return true;
    }
    return descendants.every(childId => checked.has(childId));
  });
}

/** 勾选结果补全祖先，避免半选目录丢失后子路由抬成顶级无 layout */
function withAncestors(ids: string[]): string[] {
  const result = new Set(ids);
  ids.forEach(id => {
    let parentId = parentMap.value.get(id);
    while (parentId && parentId !== '0') {
      result.add(parentId);
      parentId = parentMap.value.get(parentId);
    }
  });
  return [...result];
}

/** 平铺 MenuCheck 组树，勾选以接口 checkArr 为准 */
function buildAuthTree(list: Api.SystemManage.MenuCheck[]) {
  const map = new Map<string, TreeOption>();
  const nextParentMap = new Map<string, string>();
  const nextChildrenMap = new Map<string, string[]>();
  const checkedKeys: string[] = [];

  list.forEach(item => {
    map.set(item.menuId, {
      key: item.menuId,
      label: item.menuName,
      children: []
    });
    nextParentMap.set(item.menuId, item.parentId || '0');
    if (item.checkArr === '1') {
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
    const siblings = nextChildrenMap.get(parentId) || [];
    siblings.push(item.menuId);
    nextChildrenMap.set(parentId, siblings);
  });

  pruneEmptyChildren(roots);
  tree.value = roots;
  parentMap.value = nextParentMap;
  childrenMap.value = nextChildrenMap;
  const rawChecks = checkedKeys;
  checks.value = toCascadeSafeChecks(rawChecks);
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
    parentMap.value = new Map();
    childrenMap.value = new Map();
    return;
  }
  const { error, data } = await fetchGetRoleMenuTree(props.roleId);
  if (error || !data) {
    tree.value = [];
    checks.value = [];
    expandedKeys.value = [];
    allExpandableKeys.value = [];
    allNodeKeys.value = [];
    parentMap.value = new Map();
    childrenMap.value = new Map();
    return;
  }
  buildAuthTree(data);
}

/** 确认后直接保存角色菜单，并刷新当前用户的权限与路由 */
async function handleConfirm() {
  if (!props.roleId || saving.value) {
    return;
  }
  saving.value = true;
  const { error } = await fetchSaveRoleMenu({
    roleId: props.roleId,
    menuIds: withAncestors(checks.value).join(',')
  });
  saving.value = false;
  if (error) return;

  const infoOk = await authStore.refreshUserInfo();
  if (!infoOk) {
    // 会话已失效，勿提示授权成功
    return;
  }
  const routeOk = await routeStore.reloadAuthRoute();
  if (!routeOk) {
    // 动态路由刷新失败已踢登录，勿再提示「更新成功」
    return;
  }
  // 改掉自己角色后当前页可能已被摘掉，落到首页避免空白
  const currentName = router.currentRoute.value.name;
  if (typeof currentName === 'string' && currentName && !router.hasRoute(currentName)) {
    await router.replace({ name: routeStore.routeHome as RouteKey });
  }
  window.$message?.success($t('common.updateSuccess'));
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
        <NButton type="primary" size="small" class="mt-16px" :loading="saving" @click="handleConfirm">
          {{ $t('common.confirm') }}
        </NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
