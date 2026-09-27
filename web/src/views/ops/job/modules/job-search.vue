<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { enabledFlagOptions } from '@/constants/business';
import { translateOptions } from '@/utils/common';
import { $t } from '@/locales';

defineOptions({ name: 'JobSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.JobSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

function resetModel() {
  Object.assign(model.value, defaultModel);
}

function search() {
  emit('search');
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <NCollapse :default-expanded-names="['job-search']">
      <NCollapseItem :title="$t('common.search')" name="job-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.ops.job.jobName')" path="jobName" class="pr-24px">
              <NInput v-model:value="model.jobName" :placeholder="$t('page.ops.job.form.jobName')" clearable />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.ops.job.jobCode')" path="jobCode" class="pr-24px">
              <NInput v-model:value="model.jobCode" :placeholder="$t('page.ops.job.form.jobCode')" clearable />
            </NFormItemGi>
            <NFormItemGi
              span="24 s:12 m:6"
              :label="$t('page.manage.common.status.enable')"
              path="isEnabled"
              class="pr-24px"
            >
              <NSelect
                v-model:value="model.isEnabled"
                :placeholder="$t('page.ops.job.form.isEnabled')"
                :options="translateOptions(enabledFlagOptions)"
                clearable
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
