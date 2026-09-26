<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import { $t } from '@/locales';

defineOptions({ name: 'HomeWorkspaceGreeting' });

const props = defineProps<{
  /** 首页统一拉取的个人资料；无权限或未加载完时为 null */
  profile?: Api.SystemManage.UserProfileDetail | null;
}>();

const authStore = useAuthStore();

const greetingText = computed(() => {
  const hour = new Date().getHours();
  const userName = authStore.userInfo.userName || '-';
  if (hour < 12) return $t('page.home.greetingMorning', { userName });
  if (hour < 18) return $t('page.home.greetingAfternoon', { userName });
  return $t('page.home.greetingEvening', { userName });
});

/** 与顶栏头像一致：相对路径走 /upload，stamp 破缓存 */
const avatarSrc = computed(() => {
  const raw = (authStore.userInfo.userAvatar ?? '').trim();
  if (!raw) return '';
  if (/^(https?:)?\/\//i.test(raw) || raw.startsWith('data:') || raw.startsWith('blob:')) {
    return raw;
  }
  const path = raw.startsWith('/') ? raw : `/${raw}`;
  return `${path}?_t=${authStore.avatarStamp}`;
});

const avatarOk = ref(Boolean(avatarSrc.value));

watch(avatarSrc, src => {
  avatarOk.value = Boolean(src);
});

function onAvatarError() {
  avatarOk.value = false;
}

const deptName = computed(() => props.profile?.deptName || null);
const lastLoginTime = computed(() => props.profile?.lastLoginTime || null);
</script>

<template>
  <div class="workspace-hero">
    <!-- 光晕单独裁剪，避免整卡 overflow:hidden 把窄屏换行内容裁掉 -->
    <div class="hero-glow-clip" aria-hidden="true">
      <div class="hero-glow" />
    </div>
    <div class="relative z-1 flex items-center gap-16px">
      <NAvatar v-if="avatarOk" :size="68" :src="avatarSrc" round class="shrink-0" @error="onAvatarError" />
      <div v-else class="avatar-fallback flex-center shrink-0">
        {{ (authStore.userInfo.userName || '?').slice(0, 1) }}
      </div>
      <div class="min-w-0 flex-1">
        <h3 class="truncate text-22px font-600 leading-32px text-white">{{ greetingText }}</h3>
        <div class="mt-10px flex flex-wrap items-center gap-8px text-13px">
          <span v-if="deptName" class="hero-chip">
            <SvgIcon icon="mdi:office-building-outline" class="text-15px opacity-90" />
            {{ deptName }}
          </span>
          <span class="hero-chip">
            <SvgIcon icon="mdi:clock-outline" class="text-15px opacity-90" />
            {{
              lastLoginTime
                ? $t('page.home.lastLogin', { time: lastLoginTime })
                : $t('page.home.lastLoginEmpty')
            }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.workspace-hero {
  position: relative;
  z-index: 1;
  isolation: isolate;
  padding: 22px 24px;
  border-radius: 12px;
  color: #fff;
  background: linear-gradient(
    135deg,
    rgb(var(--primary-color)) 0%,
    color-mix(in srgb, rgb(var(--primary-color)) 68%, #1f2937) 100%
  );
}

.hero-glow-clip {
  position: absolute;
  inset: 0;
  overflow: hidden;
  border-radius: inherit;
  pointer-events: none;
}

.hero-glow {
  position: absolute;
  right: -40px;
  top: -60px;
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: rgb(255 255 255 / 14%);
}

.avatar-fallback {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  font-size: 24px;
  font-weight: 600;
  color: rgb(var(--primary-color));
  background: #fff;
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  max-width: 100%;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgb(255 255 255 / 16%);
  color: rgb(255 255 255 / 95%);
}
</style>
