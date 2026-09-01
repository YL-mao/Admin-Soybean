<script setup lang="tsx">
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { NTag } from 'naive-ui';
import { useAppStore } from '@/store/modules/app';

defineOptions({ name: 'SettingNoticeConsole' });

const route = useRoute();
const appStore = useAppStore();
const activeTab = ref('stats');

const noticeTitle = computed(() => String(route.query.noticeTitle || '公告'));

const receiverRows = ref([
  { userName: '演示用户', deptName: '研发部', readState: 0, readTime: '-' },
  { userName: '管理员', deptName: '总部', readState: 1, readTime: '2026-06-20 10:30:00' }
]);

const columns = [
  { key: 'userName', title: '用户', align: 'center' as const },
  { key: 'deptName', title: '部门', align: 'center' as const },
  {
    key: 'readState',
    title: '阅读状态',
    align: 'center' as const,
    width: 100,
    render: (row: (typeof receiverRows.value)[0]) => (
      <NTag type={row.readState ? 'success' : 'warning'}>{row.readState ? '已读' : '未读'}</NTag>
    )
  },
  { key: 'readTime', title: '阅读时间', align: 'center' as const, width: 170 }
];
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px">
    <NCard :title="noticeTitle" :bordered="false" size="small" class="card-wrapper">
      <div class="text-13px text-gray-500 mb-12px">类型：通知 · 发布时间：2026-06-20 10:00:00 · 已读 1 / 2</div>
      <NTabs v-model:value="activeTab" type="line">
        <NTabPane name="stats" tab="阅读统计">
          <NGrid cols="2 s:4" :x-gap="12">
            <NGi><NStatistic label="应读人数" :value="2" /></NGi>
            <NGi><NStatistic label="已读" :value="1" /></NGi>
            <NGi><NStatistic label="未读" :value="1" /></NGi>
            <NGi><NStatistic label="阅读率" value="50%" /></NGi>
          </NGrid>
        </NTabPane>
        <NTabPane name="receivers" tab="接收人明细">
          <NDataTable
            :columns="columns"
            :data="receiverRows"
            size="small"
            :flex-height="!appStore.isMobile"
            class="sm:h-360px"
          />
        </NTabPane>
      </NTabs>
    </NCard>
  </div>
</template>
