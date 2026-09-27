<script setup lang="ts">
import { toRaw, watch } from 'vue';
import { jsonClone } from '@sa/utils';
import { $t } from '@/locales';

defineOptions({ name: 'JobLogSearch' });

interface Props {
  jobName?: string;
  jobCode?: string;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.JobLogSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

/** 与后端 JobDto.JobLogList.runStatus 枚举一致 */
const runStatusOptions = [
  { label: $t('page.ops.jobLog.statusSuccess'), value: 'SUCCESS' },
  { label: $t('page.ops.jobLog.statusFailed'), value: 'FAILED' },
  { label: $t('page.ops.jobLog.statusSkipped'), value: 'SKIPPED' }
];

watch(
  () => props.jobCode,
  () => {
    // 切换任务时刷新默认快照，避免重置带回上一次任务的筛选。
    Object.assign(defaultModel, jsonClone(toRaw(model.value)));
  }
);

function resetModel() {
  Object.assign(model.value, jsonClone(defaultModel), {
    jobId: model.value.jobId,
    runStatus: null
  });
}

function search() {
  emit('search');
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <NCollapse :default-expanded-names="['job-log-search']">
      <NCollapseItem :title="$t('common.search')" name="job-log-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:8" :label="$t('page.ops.jobLog.jobName')" class="pr-24px">
              <NInput :value="jobName" disabled />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:8" :label="$t('page.ops.jobLog.jobCode')" class="pr-24px">
              <NInput :value="jobCode" disabled />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:8" :label="$t('page.ops.job.runStatus')" class="pr-24px">
              <NSelect
                v-model:value="model.runStatus"
                clearable
                :options="runStatusOptions"
                :placeholder="$t('page.ops.jobLog.form.runStatus')"
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
