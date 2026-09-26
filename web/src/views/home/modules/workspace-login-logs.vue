<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGetOwnLoginLogs } from '@/service/api';
import { useRouterPush } from '@/hooks/common/router';
import { useAuthStore } from '@/store/modules/auth';
import { useRouteStore } from '@/store/modules/route';
import { $t } from '@/locales';

defineOptions({ name: 'HomeWorkspaceLoginLogs' });

const router = useRouter();
const authStore = useAuthStore();
const routeStore = useRouteStore();
const { routerPushByKey } = useRouterPush();

const loading = ref(false);
const rows = ref<Api.SystemManage.UserOwnLoginLog[]>([]);
/** 防门禁变化后旧响应写回 */
let loadSeq = 0;

/** 最近登录挂在个人中心，无该路由则不请求 */
const canView = computed(() => {
  if (!authStore.isLogin || !routeStore.isInitAuthRoute) return false;
  return router.getRoutes().some(item => item.name === 'account_info');
});

async function loadLogs() {
  const seq = ++loadSeq;
  if (!canView.value) {
    rows.value = [];
    loading.value = false;
    return;
  }
  loading.value = true;
  try {
    const { data, error } = await fetchGetOwnLoginLogs({ current: 1, size: 5 });
    if (seq !== loadSeq) return;
    if (error) {
      rows.value = [];
      return;
    }
    rows.value = data ?? [];
  } finally {
    if (seq === loadSeq) {
      loading.value = false;
    }
  }
}

function goMore() {
  routerPushByKey('account_info', { query: { tab: 'loginLog' } });
}

watch(canView, () => {
  loadLogs();
});

onMounted(() => {
  loadLogs();
});
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper panel-card h-full">
    <template #header>
      <div class="flex items-center gap-8px">
        <span class="panel-title-icon flex-center">
          <SvgIcon icon="mdi:history" class="text-16px" />
        </span>
        <span class="text-15px font-600">{{ $t('page.home.recentLogins') }}</span>
      </div>
    </template>
    <template v-if="canView" #header-extra>
      <NButton size="tiny" quaternary type="primary" @click="goMore">
        {{ $t('page.home.viewAll') }}
      </NButton>
    </template>

    <div v-if="!canView" class="panel-empty">
      <NEmpty :description="$t('page.home.loginLogNoPerm')" />
    </div>
    <NSpin v-else class="log-spin" :show="loading">
      <div v-if="rows.length" class="log-list">
        <div
          v-for="(item, index) in rows"
          :key="`${item.loginTime}-${index}`"
          class="log-row"
          :class="{ 'is-current': item.current }"
        >
          <div class="log-index flex-center shrink-0">
            <SvgIcon v-if="item.current" icon="mdi:map-marker-radius-outline" class="text-15px" />
            <span v-else>{{ index + 1 }}</span>
          </div>
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-8px">
              <span class="log-time">{{ item.loginTime || '-' }}</span>
              <NTag v-if="item.current" size="small" type="success" :bordered="false" round>
                {{ $t('page.autobox.account.currentSession') }}
              </NTag>
            </div>
            <div class="log-chips">
              <span class="log-chip chip-ip">
                <SvgIcon icon="mdi:ip-network-outline" class="text-13px" />
                {{ item.loginIp || '-' }}
              </span>
              <span v-if="item.browser" class="log-chip chip-browser">
                <SvgIcon icon="mdi:web" class="text-13px" />
                {{ item.browser }}
              </span>
              <span v-if="item.systemOs" class="log-chip chip-os">
                <SvgIcon icon="mdi:laptop" class="text-13px" />
                {{ item.systemOs }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="panel-empty">
        <NEmpty />
      </div>
    </NSpin>
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

.log-spin {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
}

.log-spin :deep(.n-spin-container),
.log-spin :deep(.n-spin-content) {
  display: flex !important;
  flex: 1;
  flex-direction: column;
  min-height: 0;
}

.log-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 8px;
  min-height: 0;
}

.log-row {
  display: flex;
  flex: 1;
  align-items: center;
  gap: 12px;
  min-height: 0;
  padding: 10px 12px;
  border: 1px solid rgb(0 0 0 / 5%);
  border-radius: 12px;
  background: rgb(0 0 0 / 2%);
  transition:
    background 0.15s ease,
    border-color 0.15s ease;
}

.log-row:hover {
  background: rgb(0 0 0 / 3.5%);
  border-color: rgb(0 0 0 / 8%);
}

.log-row.is-current {
  border-color: color-mix(in srgb, rgb(var(--primary-color)) 22%, transparent);
  background: color-mix(in srgb, rgb(var(--primary-color)) 7%, #fff);
}

.log-index {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  color: rgb(0 0 0 / 45%);
  background: rgb(0 0 0 / 5%);
}

.log-row.is-current .log-index {
  color: #fff;
  background: rgb(var(--primary-color));
}

.log-time {
  font-size: 14px;
  font-weight: 600;
  line-height: 22px;
  color: rgb(0 0 0 / 88%);
}

.log-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.log-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  max-width: 100%;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  line-height: 18px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chip-ip {
  color: #2080f0;
  background: rgb(32 128 240 / 10%);
}

.chip-browser {
  color: #f0a020;
  background: rgb(240 160 32 / 12%);
}

.chip-os {
  color: #18a058;
  background: rgb(24 160 88 / 10%);
}

html.dark .log-row {
  border-color: rgb(255 255 255 / 8%);
  background: rgb(255 255 255 / 3%);
}

html.dark .log-row:hover {
  background: rgb(255 255 255 / 5%);
}

html.dark .log-row.is-current {
  background: color-mix(in srgb, rgb(var(--primary-color)) 16%, transparent);
}

html.dark .log-index {
  color: rgb(255 255 255 / 55%);
  background: rgb(255 255 255 / 8%);
}

html.dark .log-time {
  color: rgb(255 255 255 / 90%);
}
</style>
