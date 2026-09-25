<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import type { RouteKey } from '@elegant-router/types';
import { useRouterPush } from '@/hooks/common/router';
import { useAuthStore } from '@/store/modules/auth';
import { useRouteStore } from '@/store/modules/route';
import { $t } from '@/locales';

defineOptions({ name: 'HomeWorkspaceShortcuts' });

interface ShortcutItem {
  key: string;
  label: string;
  icon: string;
  tone: 'primary' | 'success' | 'warning' | 'info';
  /** 依赖的菜单路由名；无则按登录即可 */
  routeName?: RouteKey;
  onClick: () => void;
}

const router = useRouter();
const authStore = useAuthStore();
const routeStore = useRouteStore();
const { routerPushByKey } = useRouterPush();

function hasRoute(name: RouteKey) {
  if (!authStore.isLogin || !routeStore.isInitAuthRoute) return false;
  return router.getRoutes().some(item => item.name === name);
}

const shortcuts = computed(() => {
  const items: ShortcutItem[] = [
    {
      key: 'account',
      label: $t('page.home.shortcutAccount'),
      icon: 'mdi:account-circle-outline',
      tone: 'primary',
      routeName: 'account_info',
      onClick: () => routerPushByKey('account_info')
    },
    {
      key: 'password',
      label: $t('page.home.shortcutPassword'),
      icon: 'mdi:lock-reset',
      tone: 'warning',
      routeName: 'account_info',
      onClick: () => routerPushByKey('account_info', { query: { tab: 'password' } })
    },
    {
      key: 'notice',
      label: $t('page.home.shortcutNotice'),
      icon: 'mdi:bullhorn-outline',
      tone: 'info',
      routeName: 'account_notice',
      onClick: () => routerPushByKey('account_notice')
    },
    {
      key: 'file',
      label: $t('page.home.shortcutFile'),
      icon: 'mdi:folder-outline',
      tone: 'success',
      routeName: 'setting_file',
      onClick: () => routerPushByKey('setting_file')
    }
  ];
  return items.filter(item => !item.routeName || hasRoute(item.routeName));
});
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper panel-card h-full">
    <template #header>
      <div class="flex items-center gap-8px">
        <span class="panel-title-icon flex-center">
          <SvgIcon icon="mdi:apps" class="text-16px" />
        </span>
        <span class="text-15px font-600">{{ $t('page.home.shortcuts') }}</span>
      </div>
    </template>

    <div v-if="shortcuts.length" class="shortcut-grid">
      <button
        v-for="item in shortcuts"
        :key="item.key"
        type="button"
        class="shortcut-tile"
        :class="`tone-${item.tone}`"
        @click="item.onClick()"
      >
        <div class="shortcut-icon flex-center">
          <SvgIcon :icon="item.icon" class="text-22px" />
        </div>
        <span class="mt-10px text-13px font-500">{{ item.label }}</span>
      </button>
    </div>
    <div v-else class="panel-empty">
      <NEmpty :description="$t('page.home.shortcutEmpty')" />
    </div>
  </NCard>
</template>

<style scoped>
.panel-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.panel-card :deep(.n-card-header) {
  flex-shrink: 0;
}

.panel-card :deep(.n-card__content) {
  display: flex;
  flex: 1 1 auto;
  flex-direction: column;
  min-height: 0;
  height: 100%;
}

.panel-title-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  color: rgb(var(--primary-color));
  background: color-mix(in srgb, rgb(var(--primary-color)) 12%, transparent);
}

.panel-empty {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  min-height: 140px;
}

/* 2x2 铺满卡片内容区，与右侧公告等高 */
.shortcut-grid {
  display: grid;
  flex: 1;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 12px;
  min-height: 0;
  height: 100%;
}

.shortcut-tile {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 0;
  padding: 16px 8px;
  border: 1px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition:
    box-shadow 0.15s ease,
    filter 0.15s ease;
}

.shortcut-tile:hover {
  filter: brightness(1.02);
  box-shadow: 0 10px 24px rgb(0 0 0 / 6%);
}

.shortcut-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
}

.tone-primary {
  color: rgb(var(--primary-color));
  background: color-mix(in srgb, rgb(var(--primary-color)) 10%, #fff);
  border-color: color-mix(in srgb, rgb(var(--primary-color)) 16%, transparent);
}

.tone-primary .shortcut-icon {
  color: #fff;
  background: rgb(var(--primary-color));
}

.tone-success {
  color: #18a058;
  background: rgb(24 160 88 / 8%);
  border-color: rgb(24 160 88 / 14%);
}

.tone-success .shortcut-icon {
  color: #fff;
  background: #18a058;
}

.tone-warning {
  color: #f0a020;
  background: rgb(240 160 32 / 10%);
  border-color: rgb(240 160 32 / 16%);
}

.tone-warning .shortcut-icon {
  color: #fff;
  background: #f0a020;
}

.tone-info {
  color: #2080f0;
  background: rgb(32 128 240 / 8%);
  border-color: rgb(32 128 240 / 14%);
}

.tone-info .shortcut-icon {
  color: #fff;
  background: #2080f0;
}

html.dark .tone-primary {
  background: color-mix(in srgb, rgb(var(--primary-color)) 18%, transparent);
}

html.dark .tone-success {
  background: rgb(24 160 88 / 16%);
}

html.dark .tone-warning {
  background: rgb(240 160 32 / 16%);
}

html.dark .tone-info {
  background: rgb(32 128 240 / 16%);
}

html.dark .shortcut-tile:hover {
  box-shadow: 0 10px 24px rgb(0 0 0 / 28%);
}
</style>
