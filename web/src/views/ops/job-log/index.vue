<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { $t } from '@/locales';

defineOptions({ name: 'OpsJobLog' });

const router = useRouter();

// 执行日志已改为任务列表抽屉；隐藏路由仅作兜底跳回，不单独维护页面。
onMounted(() => {
  window.$message?.info($t('page.autobox.jobLog.useDrawerHint'));
  router.replace({ name: 'ops_job' });
});
</script>

<template>
  <div />
</template>
