<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { $t } from '@/locales';

defineOptions({ name: 'JobLogSearch' });

interface Props {
  jobName?: string;
  jobCode?: string;
}

defineProps<Props>();

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.AutoboxScaffold.JobLogSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

const runStatusOptions = [
  { label: 'SUCCESS', value: 'SUCCESS' },
  { label: 'FAIL', value: 'FAIL' }
];

function resetModel() {
  Object.assign(model.value, defaultModel);
}

function search() {
  emit('search');
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <NCollapse :default-expanded-names="['job-log-search']">
      <NCollapseItem :title="$t('common.search')" name="job-log-search">
        <NForm label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.jobLog.jobName')" class="pr-24px">
              <NInput :value="jobName" disabled />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.jobLog.jobCode')" class="pr-24px">
              <NInput :value="jobCode" disabled />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.job.runStatus')" class="pr-24px">
              <NSelect
                v-model:value="model.runStatus"
                clearable
                :options="runStatusOptions"
                :placeholder="$t('page.autobox.jobLog.form.runStatus')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" class="pr-24px" :show-label="false" :show-feedback="false">
              <TableSearchActions @reset="resetModel" @search="search" />
            </NFormItemGi>
          </NGrid>
        </NForm>
      </NCollapseItem>
    </NCollapse>
  </NCard>
</template>
