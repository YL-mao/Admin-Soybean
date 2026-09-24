<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import type { VNode } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import { useRouterPush } from '@/hooks/common/router';
import { useSvgIcon } from '@/hooks/common/icon';
import { $t } from '@/locales';

defineOptions({
  name: 'UserAvatar'
});

const authStore = useAuthStore();
const { routerPushByKey, toLogin } = useRouterPush();
const { SvgIconVNode } = useSvgIcon();

function loginOrRegister() {
  toLogin();
}

type DropdownKey = 'account_info' | 'logout';

type DropdownOption =
  | {
      key: DropdownKey;
      label: string;
      icon?: () => VNode;
    }
  | {
      type: 'divider';
      key: string;
    };

const options = computed(() => {
  // 个人菜单默认不进侧栏，入口挂在头像下拉
  const opts: DropdownOption[] = [
    {
      label: $t('common.userCenter'),
      key: 'account_info',
      icon: SvgIconVNode({ icon: 'ph:user-circle', fontSize: 18 })
    },
    {
      type: 'divider',
      key: 'divider'
    },
    {
      label: $t('common.logout'),
      key: 'logout',
      icon: SvgIconVNode({ icon: 'ph:sign-out', fontSize: 18 })
    }
  ];

  return opts;
});

/** 与个人中心一致：相对路径走 /upload，覆盖后靠 stamp 破缓存 */
const headerAvatarSrc = computed(() => {
  const raw = (authStore.userInfo.userAvatar ?? '').trim();
  if (!raw) return '';
  if (/^(https?:)?\/\//i.test(raw) || raw.startsWith('data:') || raw.startsWith('blob:')) {
    return raw;
  }
  const path = raw.startsWith('/') ? raw : `/${raw}`;
  return `${path}?_t=${authStore.avatarStamp}`;
});

/** 无地址或加载失败时不渲染头像，只留昵称 */
const avatarOk = ref(Boolean(headerAvatarSrc.value));

watch(headerAvatarSrc, src => {
  avatarOk.value = Boolean(src);
});

function onAvatarError() {
  avatarOk.value = false;
}

function logout() {
  window.$dialog?.info({
    title: $t('common.tip'),
    content: $t('common.logoutConfirm'),
    positiveText: $t('common.confirm'),
    negativeText: $t('common.cancel'),
    onPositiveClick: async () => {
      await authStore.resetStore();
      window.$message?.success($t('common.logoutSuccess'));
    }
  });
}

function handleDropdown(key: DropdownKey) {
  if (key === 'logout') {
    logout();
  } else {
    routerPushByKey(key);
  }
}
</script>

<template>
  <NButton v-if="!authStore.isLogin" quaternary @click="loginOrRegister">
    {{ $t('page.login.common.loginOrRegister') }}
  </NButton>
  <NDropdown v-else placement="bottom" trigger="click" :options="options" @select="handleDropdown">
    <div>
      <ButtonIcon>
        <NAvatar
          v-if="avatarOk && headerAvatarSrc"
          :key="headerAvatarSrc"
          round
          :size="28"
          object-fit="cover"
          :src="headerAvatarSrc"
          @error="onAvatarError"
        />
        <span class="text-16px font-medium">{{ authStore.userInfo.userName }}</span>
      </ButtonIcon>
    </div>
  </NDropdown>
</template>

<style scoped></style>
