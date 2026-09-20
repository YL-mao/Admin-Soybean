<script setup lang="ts">
import { computed } from 'vue';
import { useBrandingStore } from '@/store/modules/branding';

defineOptions({
  name: 'GlobalFooter'
});

const brandingStore = useBrandingStore();

/** 页脚有任意一段品牌信息才渲染内容区 */
const hasFooterContent = computed(() => {
  const b = brandingStore.branding;
  return Boolean(
    b.copyright.trim() ||
      b.version.trim() ||
      b.adminMail.trim() ||
      b.icp.trim() ||
      b.policeIcp.trim() ||
      brandingStore.websiteHref
  );
});
</script>

<template>
  <DarkModeContainer class="h-full flex-center px-12px">
    <div v-if="hasFooterContent" class="flex-y-center flex-wrap justify-center gap-x-12px gap-y-4px text-12px">
      <a
        v-if="brandingStore.branding.copyright.trim()"
        :href="brandingStore.websiteHref || undefined"
        :target="brandingStore.websiteHref ? '_blank' : undefined"
        :rel="brandingStore.websiteHref ? 'noopener noreferrer' : undefined"
        class="text-inherit"
      >
        {{ brandingStore.branding.copyright }}
      </a>
      <span v-if="brandingStore.branding.version.trim()">v{{ brandingStore.branding.version }}</span>
      <a
        v-if="brandingStore.mailHref"
        :href="brandingStore.mailHref"
        class="text-inherit"
      >
        {{ brandingStore.branding.adminMail }}
      </a>
      <a
        v-if="brandingStore.branding.icp.trim()"
        href="https://beian.miit.gov.cn/"
        target="_blank"
        rel="noopener noreferrer"
        class="text-inherit"
      >
        {{ brandingStore.branding.icp }}
      </a>
      <a
        v-if="brandingStore.branding.policeIcp.trim()"
        href="https://www.beian.gov.cn/"
        target="_blank"
        rel="noopener noreferrer"
        class="text-inherit"
      >
        {{ brandingStore.branding.policeIcp }}
      </a>
    </div>
  </DarkModeContainer>
</template>

<style scoped></style>
