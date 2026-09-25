<script setup lang="ts">
import { onMounted, ref, toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { fetchGetDictOptions } from '@/service/api';
import { $t } from '@/locales';

defineOptions({ name: 'UserNoticeSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.UserInboxNoticeSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

const typeOptions = ref<{ label: string; value: number }[]>([]);

const readOptions = [
  { label: $t('page.autobox.account.noticeUnread'), value: 0 },
  { label: $t('page.autobox.account.noticeRead'), value: 1 }
];

function resetModel() {
  Object.assign(model.value, defaultModel);
}

function search() {
  emit('search');
}

function reset() {
  resetModel();
  search();
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
    <NForm :model="model" label-placement="left" :label-width="80" :show-feedback="false">
      <NGrid cols="1 s:2 m:4" responsive="screen" :x-gap="16" :y-gap="16">
        <NFormItemGi :label="$t('page.autobox.notice.noticeTitle')" class="pr-24px">
          <NInput
            v-model:value="model.noticeTitle"
            clearable
            :placeholder="$t('page.autobox.notice.form.noticeTitle')"
          />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.autobox.notice.noticeType')" class="pr-24px">
          <NSelect
            v-model:value="model.noticeType"
            clearable
            :options="typeOptions"
            :placeholder="$t('page.autobox.notice.form.noticeType')"
          />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.autobox.account.noticeReadState')" class="pr-24px">
          <NSelect
            v-model:value="model.readState"
            clearable
            :options="readOptions"
            :placeholder="$t('page.autobox.account.form.noticeReadState')"
          />
        </NFormItemGi>
        <NFormItemGi class="pr-24px">
          <NSpace class="w-full" justify="end">
            <NButton @click="reset">
              <template #icon>
                <icon-ic-round-refresh class="text-icon" />
              </template>
              {{ $t('common.reset') }}
            </NButton>
            <NButton type="primary" ghost @click="search">
              <template #icon>
                <icon-ic-round-search class="text-icon" />
              </template>
              {{ $t('common.search') }}
            </NButton>
          </NSpace>
        </NFormItemGi>
      </NGrid>
    </NForm>
  </NCard>
</template>
