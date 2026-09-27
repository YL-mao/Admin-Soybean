<script setup lang="ts">
import { computed } from 'vue';
import { useRouterPush } from '@/hooks/common/router';
import { $t } from '@/locales';

defineOptions({ name: 'HomeWorkspaceProfileSummary' });

type TileTone = 'primary' | 'info' | 'warning' | 'violet' | 'cyan' | 'success';

const props = defineProps<{
  /** 首页统一拉取的个人资料 */
  profile: Api.SystemManage.UserProfileDetail | null;
  loading: boolean;
  /** 有 user:info:select 才展示摘要 */
  canView: boolean;
  /** 有个人中心路由才显示「去资料」 */
  canOpen: boolean;
}>();

const { routerPushByKey } = useRouterPush();

const roleNames = computed(() => props.profile?.roleNames?.filter(Boolean) ?? []);

const roleText = computed(() => (roleNames.value.length ? roleNames.value.join('、') : '-'));

const statusLabel = computed(() => {
  if (props.profile?.isEnabled == null) return '-';
  return props.profile.isEnabled === 1
    ? $t('page.manage.common.status.enable')
    : $t('page.manage.common.status.disable');
});

const statusEnabled = computed(() => props.profile?.isEnabled === 1);

/** 六格配色互不重复，避免相邻同色 */
const summaryItems = computed(() => {
  if (!props.profile) return [];
  return [
    {
      key: 'userName',
      label: $t('page.account.userName'),
      value: props.profile.userName || '-',
      icon: 'mdi:badge-account-horizontal-outline',
      tone: 'primary' as TileTone
    },
    {
      key: 'account',
      label: $t('page.account.userAccount'),
      value: props.profile.userAccount || '-',
      icon: 'mdi:account-outline',
      tone: 'info' as TileTone
    },
    {
      key: 'dept',
      label: $t('page.account.deptName'),
      value: props.profile.deptName || '-',
      icon: 'mdi:office-building-outline',
      tone: 'warning' as TileTone
    },
    {
      key: 'post',
      label: $t('page.account.postName'),
      value: props.profile.postName || '-',
      icon: 'mdi:briefcase-outline',
      tone: 'violet' as TileTone
    },
    {
      key: 'role',
      label: $t('page.account.roleNames'),
      value: roleText.value,
      icon: 'mdi:shield-account-outline',
      tone: 'cyan' as TileTone
    },
    {
      key: 'status',
      label: $t('page.home.accountStatus'),
      value: statusLabel.value,
      icon: statusEnabled.value ? 'mdi:check-circle-outline' : 'mdi:pause-circle-outline',
      tone: 'success' as TileTone
    }
  ];
});

function goProfile() {
  routerPushByKey('account_info');
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper panel-card h-full">
    <template #header>
      <div class="flex items-center gap-8px">
        <span class="panel-title-icon flex-center">
          <SvgIcon icon="mdi:card-account-details-outline" class="text-16px" />
        </span>
        <span class="text-15px font-600">{{ $t('page.home.profileSummary') }}</span>
      </div>
    </template>
    <template v-if="canView && canOpen" #header-extra>
      <NButton size="tiny" quaternary type="primary" @click="goProfile">
        {{ $t('page.home.goProfile') }}
      </NButton>
    </template>

    <div v-if="!canView" class="panel-empty">
      <NEmpty :description="$t('page.home.loginLogNoPerm')" />
    </div>
    <div v-else class="summary-body">
      <NSpin class="summary-spin" :show="loading">
        <div v-if="summaryItems.length" class="summary-grid">
          <div v-for="item in summaryItems" :key="item.key" class="summary-tile" :class="`tone-${item.tone}`">
            <div class="tile-mark" aria-hidden="true">
              <SvgIcon :icon="item.icon" />
            </div>
            <div class="tile-top">
              <span class="tile-label">{{ item.label }}</span>
              <span class="tile-icon flex-center">
                <SvgIcon :icon="item.icon" class="text-15px" />
              </span>
            </div>
            <div class="tile-value" :title="item.value">{{ item.value }}</div>
          </div>
        </div>
        <div v-else class="panel-empty">
          <NEmpty />
        </div>
      </NSpin>
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
  min-height: 160px;
}

.summary-body {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
  height: 100%;
}

.summary-spin {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
  height: 100%;
}

.summary-spin :deep(.n-spin-container),
.summary-spin :deep(.n-spin-content) {
  display: flex !important;
  flex: 1;
  flex-direction: column;
  min-height: 0;
  height: 100%;
}

.summary-grid {
  display: grid;
  flex: 1 1 auto;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(3, minmax(0, 1fr));
  gap: 10px;
  min-height: 0;
  height: 100%;
}

.summary-tile {
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;
  min-height: 0;
  height: 100%;
  padding: 12px 12px 14px;
  border: 1px solid transparent;
  border-radius: 12px;
  transition: box-shadow 0.15s ease;
}

.summary-tile:hover {
  box-shadow: 0 8px 18px rgb(0 0 0 / 5%);
}

.tile-mark {
  position: absolute;
  right: -6px;
  bottom: -10px;
  font-size: 56px;
  line-height: 1;
  opacity: 0.12;
  pointer-events: none;
  transform: rotate(-8deg);
}

.tile-top {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.tile-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  flex-shrink: 0;
  color: #fff;
}

.tile-label {
  font-size: 12px;
  line-height: 18px;
  opacity: 0.72;
}

.tile-value {
  position: relative;
  z-index: 1;
  font-size: 15px;
  font-weight: 700;
  line-height: 22px;
  letter-spacing: 0.2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.tone-primary {
  color: rgb(var(--primary-color));
  background: color-mix(in srgb, rgb(var(--primary-color)) 10%, #fff);
  border-color: color-mix(in srgb, rgb(var(--primary-color)) 16%, transparent);
}

.tone-primary .tile-icon {
  background: rgb(var(--primary-color));
}

.tone-info {
  color: #2080f0;
  background: rgb(32 128 240 / 8%);
  border-color: rgb(32 128 240 / 14%);
}

.tone-info .tile-icon {
  background: #2080f0;
}

.tone-warning {
  color: #f0a020;
  background: rgb(240 160 32 / 10%);
  border-color: rgb(240 160 32 / 16%);
}

.tone-warning .tile-icon {
  background: #f0a020;
}

.tone-violet {
  color: #8b5cf6;
  background: rgb(139 92 246 / 10%);
  border-color: rgb(139 92 246 / 16%);
}

.tone-violet .tile-icon {
  background: #8b5cf6;
}

.tone-cyan {
  color: #0ea5e9;
  background: rgb(14 165 233 / 10%);
  border-color: rgb(14 165 233 / 16%);
}

.tone-cyan .tile-icon {
  background: #0ea5e9;
}

.tone-success {
  color: #18a058;
  background: rgb(24 160 88 / 8%);
  border-color: rgb(24 160 88 / 14%);
}

.tone-success .tile-icon {
  background: #18a058;
}

html.dark .tone-primary {
  background: color-mix(in srgb, rgb(var(--primary-color)) 18%, transparent);
}

html.dark .tone-info {
  background: rgb(32 128 240 / 16%);
}

html.dark .tone-warning {
  background: rgb(240 160 32 / 16%);
}

html.dark .tone-violet {
  background: rgb(139 92 246 / 18%);
}

html.dark .tone-cyan {
  background: rgb(14 165 233 / 18%);
}

html.dark .tone-success {
  background: rgb(24 160 88 / 16%);
}

html.dark .summary-tile:hover {
  box-shadow: 0 8px 18px rgb(0 0 0 / 28%);
}
</style>
