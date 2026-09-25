<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useRouterPush } from '@/hooks/common/router';
import { $t } from '@/locales';

defineOptions({ name: 'SettingNoticeConsole' });

const route = useRoute();
const { routerPushByKey } = useRouterPush();

/** 控制台已改为公告列表抽屉；隐藏路由直访时提示并回公告管理（有 noticeId 则带参打开抽屉） */
onMounted(async () => {
  const noticeId = typeof route.query.noticeId === 'string' ? route.query.noticeId.trim() : '';
  if (!noticeId) {
    window.$message?.warning($t('page.autobox.notice.consoleDirectVisitTip'));
  }
  await routerPushByKey('setting_notice', noticeId ? { query: { noticeId } } : undefined);
});
</script>

<template>
  <div />
</template>
