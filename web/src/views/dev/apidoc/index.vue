<script setup lang="ts">
import { $t } from '@/locales';

defineOptions({ name: 'DevApidoc' });
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px">
    <NCard :title="$t('page.autobox.apidoc.title')" :bordered="false" size="small" class="card-wrapper max-w-720px">
      <p class="mb-16px text-gray-600">{{ $t('page.autobox.apidoc.desc') }}</p>
      <NButton type="primary" tag="a" href="/v3/api-docs/swagger-ui/index.html" target="_blank">
        {{ $t('page.autobox.apidoc.openDoc') }}
      </NButton>
    </NCard>
  </div>
</template>
