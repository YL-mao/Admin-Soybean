<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import { SwaggerUIBundle } from 'swagger-ui-dist';
import type { SwaggerRequest } from 'swagger-ui-dist';
import 'swagger-ui-dist/swagger-ui.css';
import { $t } from '@/locales';
import { getAuthorization } from '@/service/request/shared';
import { DEVICE_ID_HEADER, getDeviceId } from '@/utils/device-id';
import { getServiceBaseURL, resolveBackendAssetUrl } from '@/utils/service';

defineOptions({ name: 'DevApidoc' });

/** 仅 Vite 开发模式渲染；生产构建不提供（后端 springdoc 生产亦关闭） */
const isDev = import.meta.env.DEV;
const hostRef = ref<HTMLElement | null>(null);

const isHttpProxy = import.meta.env.DEV && import.meta.env.VITE_HTTP_PROXY === 'Y';
const { baseURL: serviceBaseURL } = getServiceBaseURL(import.meta.env, isHttpProxy);

/** 同源且未走代理时补前缀，避免 Try it out 打到 Vite */
function rewriteToService(url: string) {
  try {
    const abs = new URL(url, window.location.href);
    if (abs.origin !== window.location.origin) return url;
    if (abs.pathname === serviceBaseURL || abs.pathname.startsWith(`${serviceBaseURL}/`)) return url;
    return `${serviceBaseURL}${abs.pathname}${abs.search}`;
  } catch {
    return url;
  }
}

/** 给拉 OpenAPI 与 Try it out 统一带上登录头（文档路径仍须登录，不放白名单） */
function attachAuthHeaders(req: SwaggerRequest): SwaggerRequest {
  req.url = rewriteToService(req.url);
  const headers = (req.headers ?? {}) as Record<string, string>;
  const token = getAuthorization();
  if (token) {
    headers.saToken = token;
  }
  headers[DEVICE_ID_HEADER] = getDeviceId();
  req.headers = headers;
  return req;
}

function mountSwagger() {
  const el = hostRef.value;
  if (!el || !isDev) return;

  el.innerHTML = '';
  // 仅保留 admin 分组（含认证与业务接口）
  SwaggerUIBundle({
    domNode: el,
    url: resolveBackendAssetUrl('/v3/api-docs/admin'),
    deepLinking: false,
    docExpansion: 'list',
    defaultModelsExpandDepth: 0,
    persistAuthorization: true,
    presets: [SwaggerUIBundle.presets.apis],
    requestInterceptor: attachAuthHeaders
  });
}

onMounted(async () => {
  if (!isDev) return;
  await nextTick();
  mountSwagger();
});

onBeforeUnmount(() => {
  hostRef.value && (hostRef.value.innerHTML = '');
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px">
    <NCard
      :title="$t('page.dev.apidoc.title')"
      :bordered="false"
      size="small"
      class="card-wrapper flex-col-stretch flex-1-hidden"
      content-style="flex: 1; display: flex; flex-direction: column; min-height: 0;"
    >
      <template v-if="isDev">
        <p class="mb-12px text-gray-600">{{ $t('page.dev.apidoc.desc') }}</p>
        <!-- 本页 Swagger UI：已登录请求头拉 OpenAPI / 试调 -->
        <div ref="hostRef" class="w-full flex-1 overflow-auto rounded-8px bg-white" style="min-height: 70vh" />
      </template>
      <p v-else class="text-gray-600">{{ $t('page.dev.apidoc.devOnly') }}</p>
    </NCard>
  </div>
</template>
