<script setup lang="ts">
import { computed } from 'vue';
import { useBrandingStore } from '@/store/modules/branding';

defineOptions({
  name: 'GlobalLogo'
});

interface Props {
  /** Whether to show the full title（展开时显示系统名称） */
  showTitle?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  showTitle: true
});

const brandingStore = useBrandingStore();

/** 折叠时展示简称（无简称则只留图标） */
const collapsedShortTitle = computed(() => brandingStore.branding.shortName.trim());
</script>

<template>
  <RouterLink
    to="/"
    class="w-full flex-center nowrap-hidden"
    :class="{ 'flex-col gap-2px': !props.showTitle && collapsedShortTitle }"
  >
    <!-- 统一无文字 Logo；标题用配置文案（name / shortName） -->
    <img
      v-if="brandingStore.logoUrl"
      :src="brandingStore.logoUrl"
      class="size-32px object-contain"
      alt="logo"
    />
    <SystemLogo v-else class="size-32px" />
    <h2
      v-if="props.showTitle"
      class="pl-8px text-16px text-primary font-bold transition duration-300 ease-in-out"
    >
      {{ brandingStore.displayTitle }}
    </h2>
    <h2
      v-else-if="collapsedShortTitle"
      class="max-w-full truncate px-2px text-10px text-primary font-bold leading-none"
      :title="collapsedShortTitle"
    >
      {{ collapsedShortTitle }}
    </h2>
  </RouterLink>
</template>

<style scoped></style>
