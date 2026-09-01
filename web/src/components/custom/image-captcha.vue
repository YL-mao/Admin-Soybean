<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { getServiceBaseURL } from '@/utils/service';
import { $t } from '@/locales';

defineOptions({ name: 'ImageCaptcha' });

interface Props {
  /** 输入框占位文案 */
  placeholder?: string;
}

withDefaults(defineProps<Props>(), {
  placeholder: ''
});

const captcha = defineModel<string>('value', { default: '' });

const imageUrl = ref('');
let objectUrl: string | null = null;

function revokeObjectUrl() {
  if (objectUrl) {
    URL.revokeObjectURL(objectUrl);
    objectUrl = null;
  }
}

/** 拉取后端 EasyCaptcha 图；Cookie captchaId 由浏览器保存供登录校验。 */
async function refresh() {
  revokeObjectUrl();

  const isHttpProxy = import.meta.env.DEV && import.meta.env.VITE_HTTP_PROXY === 'Y';
  const { baseURL } = getServiceBaseURL(import.meta.env, isHttpProxy);
  const url = `${baseURL}/api/admin/auth/captchaImage?s=${Date.now()}`;

  try {
    const response = await fetch(url, { credentials: 'include' });

    if (!response.ok) {
      window.$message?.error($t('page.login.pwdLogin.captchaLoadFailed'));
      return;
    }

    if (response.headers.get('X-Captcha-Limited') === '1') {
      window.$message?.warning($t('page.login.pwdLogin.captchaLimited'));
    }

    const blob = await response.blob();
    objectUrl = URL.createObjectURL(blob);
    imageUrl.value = objectUrl;
  } catch {
    window.$message?.error($t('page.login.pwdLogin.captchaLoadFailed'));
  }
}

onMounted(() => {
  refresh();
});

onBeforeUnmount(() => {
  revokeObjectUrl();
});

defineExpose({
  refresh
});
</script>

<template>
  <div class="w-full flex-y-center gap-12px">
    <NInput
      v-model:value="captcha"
      :placeholder="placeholder || $t('page.login.pwdLogin.imageCodePlaceholder')"
    />
    <img
      v-if="imageUrl"
      :src="imageUrl"
      class="h-40px w-100px flex-shrink-0 cursor-pointer rd-4px"
      :title="$t('page.login.pwdLogin.refreshCaptcha')"
      alt="captcha"
      @click="refresh"
    />
    <div
      v-else
      class="h-40px w-100px flex-center flex-shrink-0 cursor-pointer rd-4px bg-gray-100"
      @click="refresh"
    >
      ...
    </div>
  </div>
</template>

<style scoped></style>
