<script setup lang="ts">
import { toRaw, watch } from 'vue';
import { jsonClone } from '@sa/utils';
import { $t } from '@/locales';

defineOptions({ name: 'NoticeConsoleReceiverSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.NoticeConsoleReceiverSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

const readOptions = [
  { label: $t('page.setting.notice.consoleUnread'), value: 0 },
  { label: $t('page.setting.notice.consoleRead'), value: 1 }
];

watch(
  () => model.value.noticeId,
  () => {
    // 切换公告时刷新默认快照，避免重置带回上一次筛选
    Object.assign(defaultModel, jsonClone(toRaw(model.value)));
  }
);

function resetModel() {
  Object.assign(model.value, jsonClone(defaultModel), {
    noticeId: model.value.noticeId,
    readState: null
  });
  // 重置后立刻重拉，避免表单已清、表格仍是旧筛选
  emit('search');
}

function search() {
  emit('search');
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper shrink-0">
    <NCollapse :default-expanded-names="['notice-console-receiver-search']">
      <NCollapseItem :title="$t('common.search')" name="notice-console-receiver-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:8" :label="$t('page.setting.notice.consoleReadState')" class="pr-24px">
              <NSelect
                v-model:value="model.readState"
                clearable
                :options="readOptions"
                :placeholder="$t('page.setting.notice.form.consoleReadState')"
              />
            </NFormItemGi>
            <NFormItemGi span="24" class="pr-24px" :show-label="false" :show-feedback="false">
              <TableSearchActions @reset="resetModel" @search="search" />
            </NFormItemGi>
          </NGrid>
        </NForm>
      </NCollapseItem>
    </NCollapse>
  </NCard>
</template>
