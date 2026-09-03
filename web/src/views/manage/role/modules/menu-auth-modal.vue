<script setup lang="ts">
import { computed, shallowRef, watch } from 'vue';
import type { TreeOption } from 'naive-ui';
import { fetchGetRoleMenuTree, fetchSaveRoleMenu } from '@/service/api';
import { $t } from '@/locales';

defineOptions({
  name: 'MenuAuthModal'
});

interface Props {
  /** 角色 ID */
  roleId: string;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', {
  default: false
});

function closeModal() {
  visible.value = false;
}

const title = computed(() => $t('common.edit') + $t('page.manage.role.menuAuth'));

const tree = shallowRef<TreeOption[]>([]);
const checks = shallowRef<string[]>([]);

/** 平铺 MenuCheck 组树，并收集已勾选 menuId */
function buildAuthTree(list: Api.SystemManage.MenuCheck[]) {
  const map = new Map<string, TreeOption>();
  const checkedKeys: string[] = [];

  list.forEach(item => {
    map.set(item.menuId, {
      key: item.menuId,
      label: item.menuName,
      children: []
    });
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
  });

  pruneEmptyChildren(roots);
  tree.value = roots;
  checks.value = checkedKeys;
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

async function loadTree() {
  if (!props.roleId) {
    tree.value = [];
    checks.value = [];
    return;
  }
  const { error, data } = await fetchGetRoleMenuTree(props.roleId);
  if (error || !data) {
    tree.value = [];
    checks.value = [];
    return;
  }
  buildAuthTree(data);
}

async function handleSubmit() {
  if (!props.roleId) return;
  const { error } = await fetchSaveRoleMenu({
    roleId: props.roleId,
    menuIds: checks.value.join(',')
  });
  if (error) return;

  window.$message?.success?.($t('common.modifySuccess'));
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
    <NTree
      v-model:checked-keys="checks"
      :data="tree"
      key-field="key"
      label-field="label"
      checkable
      cascade
      expand-on-click
      default-expand-all
      virtual-scroll
      block-line
      class="h-280px"
    />
    <template #footer>
      <NSpace justify="end">
        <NButton size="small" class="mt-16px" @click="closeModal">
          {{ $t('common.cancel') }}
        </NButton>
        <NButton type="primary" size="small" class="mt-16px" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
