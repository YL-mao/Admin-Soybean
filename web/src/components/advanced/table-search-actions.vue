<script setup lang="ts">
import { $t } from '@/locales';

/**
 * 搜索区「重置 / 搜索」按钮组。
 * 必须放在父级 `NGrid` 下的 `NFormItemGi` 内（FormItemGi 不能写在本组件里，否则栅格失效、按钮不显示）。
 */
defineOptions({ name: 'TableSearchActions' });

interface Emits {
  (e: 'reset'): void;
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();
</script>

<template>
  <NSpace class="w-full" justify="end" :wrap="false">
    <NButton @click="emit('reset')">
      <template #icon>
        <icon-ic-round-refresh class="text-icon" />
      </template>
      {{ $t('common.reset') }}
    </NButton>
    <NButton type="primary" ghost @click="emit('search')">
      <template #icon>
        <icon-ic-round-search class="text-icon" />
      </template>
      {{ $t('common.search') }}
    </NButton>
  </NSpace>
</template>
