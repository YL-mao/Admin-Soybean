<script setup lang="ts">
import { onMounted, ref, toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { fetchGetDictOptions } from '@/service/api';
import { $t } from '@/locales';

defineOptions({ name: 'NoticeSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.NoticeSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

const typeOptions = ref<{ label: string; value: number }[]>([]);

const sendOptions = [
  { label: $t('page.setting.notice.draft'), value: 0 },
  { label: $t('page.setting.notice.published'), value: 1 }
];

function resetModel() {
  Object.assign(model.value, defaultModel);
}

function search() {
  emit('search');
}

onMounted(async () => {
  const { data, error } = await fetchGetDictOptions('sys_notice_type');
  if (error || !data) return;
  typeOptions.value = data.map(item => ({
    label: item.dictDataLabel,
    value: Number(item.dictDataValue)
  }));
});
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <NCollapse :default-expanded-names="['notice-search']">
      <NCollapseItem :title="$t('common.search')" name="notice-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.setting.notice.noticeTitle')" class="pr-24px">
              <NInput v-model:value="model.noticeTitle" :placeholder="$t('page.setting.notice.form.noticeTitle')" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.setting.notice.noticeType')" class="pr-24px">
              <NSelect
                v-model:value="model.noticeType"
                clearable
                :options="typeOptions"
                :placeholder="$t('page.setting.notice.form.noticeType')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.setting.notice.publishStatus')" class="pr-24px">
              <NSelect v-model:value="model.isSend" clearable :options="sendOptions" />
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
