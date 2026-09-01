<script setup lang="tsx">
import { ref } from 'vue';
import { $t } from '@/locales';

defineOptions({ name: 'DevGen' });

const tableOptions = [
  { label: 'sys_user', value: 'sys_user' },
  { label: 'sys_role', value: 'sys_role' }
];

const selectedTable = ref('sys_user');

const columnRows = ref([
  {
    columnName: 'user_id',
    columnComment: 'ID',
    javaType: 'String',
    primaryKey: true,
    formField: true,
    listDisplayField: true,
    listQueryField: false
  },
  {
    columnName: 'user_account',
    columnComment: 'account',
    javaType: 'String',
    primaryKey: false,
    formField: true,
    listDisplayField: true,
    listQueryField: true
  }
]);

const columns = [
  { key: 'columnName', title: $t('page.autobox.gen.columnName'), align: 'center' as const },
  { key: 'columnComment', title: $t('page.autobox.gen.columnComment'), align: 'center' as const },
  { key: 'javaType', title: $t('page.autobox.gen.javaType'), align: 'center' as const, width: 100 },
  {
    key: 'primaryKey',
    title: $t('page.autobox.gen.primaryKey'),
    align: 'center' as const,
    width: 70,
    render: (row: (typeof columnRows.value)[0]) => (row.primaryKey ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no'))
  },
  {
    key: 'formField',
    title: $t('page.autobox.gen.formField'),
    align: 'center' as const,
    width: 70,
    render: (row: (typeof columnRows.value)[0]) => <NCheckbox checked={row.formField} />
  },
  {
    key: 'listDisplayField',
    title: $t('page.autobox.gen.listDisplay'),
    align: 'center' as const,
    width: 70,
    render: (row: (typeof columnRows.value)[0]) => <NCheckbox checked={row.listDisplayField} />
  },
  {
    key: 'listQueryField',
    title: $t('page.autobox.gen.listQuery'),
    align: 'center' as const,
    width: 70,
    render: (row: (typeof columnRows.value)[0]) => <NCheckbox checked={row.listQueryField} />
  }
];
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px">
    <NCard :title="$t('page.autobox.gen.title')" :bordered="false" size="small" class="card-wrapper">
      <NForm label-placement="left" :label-width="100" class="max-w-720px">
        <NFormItem :label="$t('page.autobox.gen.tableName')">
          <NSelect v-model:value="selectedTable" :options="tableOptions" filterable />
        </NFormItem>
        <NFormItem :label="$t('page.autobox.gen.moduleName')"><NInput placeholder="system" /></NFormItem>
        <NFormItem :label="$t('page.autobox.gen.businessName')"><NInput placeholder="user" /></NFormItem>
        <NFormItem :label="$t('page.autobox.gen.functionName')"><NInput /></NFormItem>
        <NFormItem :label="$t('page.autobox.gen.author')"><NInput placeholder="autobox" /></NFormItem>
        <NFormItem :label="$t('page.autobox.gen.parentMenu')">
          <NTreeSelect :options="[{ label: 'manage', key: 'manage' }]" />
        </NFormItem>
        <NFormItem>
          <NSpace>
            <NButton type="primary">{{ $t('page.autobox.gen.generate') }}</NButton>
            <NButton>{{ $t('page.autobox.gen.preview') }}</NButton>
            <NButton type="info" ghost>{{ $t('page.autobox.gen.downloadZip') }}</NButton>
          </NSpace>
        </NFormItem>
      </NForm>
    </NCard>
    <NCard :title="$t('page.autobox.gen.columnTitle')" :bordered="false" size="small" class="card-wrapper">
      <NDataTable :columns="columns" :data="columnRows" size="small" :scroll-x="800" class="sm:h-320px" />
    </NCard>
  </div>
</template>
