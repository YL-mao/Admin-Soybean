<script setup lang="ts">
import { computed } from 'vue';
import { getPaletteColorByNumber, mixColor } from '@sa/color';
import { loginModuleRecord } from '@/constants/app';
import { useAppStore } from '@/store/modules/app';
import { useThemeStore } from '@/store/modules/theme';
import { useBrandingStore } from '@/store/modules/branding';
import { $t } from '@/locales';
import PwdLogin from './modules/pwd-login.vue';

const appStore = useAppStore();
const themeStore = useThemeStore();
const brandingStore = useBrandingStore();

const bgThemeColor = computed(() =>
  themeStore.darkMode ? getPaletteColorByNumber(themeStore.themeColor, 600) : themeStore.themeColor
);

const bgColor = computed(() => {
  const COLOR_WHITE = '#ffffff';

  const ratio = themeStore.darkMode ? 0.5 : 0.2;

  return mixColor(COLOR_WHITE, themeStore.themeColor, ratio);
});
</script>

<template>
  <div class="relative size-full flex-center overflow-hidden" :style="{ backgroundColor: bgColor }">
    <WaveBg :theme-color="bgThemeColor" />
    <NCard :bordered="false" class="relative z-4 w-auto rd-12px">
      <div class="w-400px lt-sm:w-300px">
        <header class="flex-y-center justify-between">
          <img
            v-if="brandingStore.logoUrl"
            :src="brandingStore.logoUrl"
            class="size-64px object-contain lt-sm:size-48px"
            alt="logo"
          />
          <SystemLogo v-else class="size-64px lt-sm:size-48px" />
          <h3 class="text-28px text-primary font-500 lt-sm:text-22px">{{ brandingStore.displayTitle }}</h3>
          <div class="i-flex-col">
            <ThemeSchemaSwitch
              :theme-schema="themeStore.themeScheme"
              :show-tooltip="false"
              class="text-20px lt-sm:text-18px"
              @switch="themeStore.toggleThemeScheme"
            />
            <LangSwitch
              v-if="themeStore.header.multilingual.visible"
              :lang="appStore.locale"
              :lang-options="appStore.localeOptions"
              :show-tooltip="false"
              @change-lang="appStore.changeLocale"
            />
          </div>
        </header>
        <main class="pt-24px">
          <h3 class="text-18px text-primary font-medium">{{ $t(loginModuleRecord['pwd-login']) }}</h3>
          <div class="pt-24px">
            <Transition :name="themeStore.page.animateMode" mode="out-in" appear>
              <PwdLogin />
            </Transition>
          </div>
        </main>
      </div>
    </NCard>
    <!-- 登录页底部：版权 / 版本 / 备案（有值才显示） -->
    <footer
      v-if="brandingStore.branding.copyright.trim() || brandingStore.branding.version.trim() || brandingStore.branding.icp.trim()"
      class="absolute bottom-16px z-4 flex-y-center flex-wrap justify-center gap-x-12px gap-y-4px px-16px text-12px text-#666"
    >
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
        v-if="brandingStore.branding.icp.trim()"
        href="https://beian.miit.gov.cn/"
        target="_blank"
        rel="noopener noreferrer"
        class="text-inherit"
      >
        {{ brandingStore.branding.icp }}
      </a>
    </footer>
  </div>
</template>

<style scoped></style>
