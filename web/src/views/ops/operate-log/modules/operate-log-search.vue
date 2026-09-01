<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { $t } from '@/locales';

defineOptions({ name: 'OperateLogSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.AutoboxScaffold.OperateLogSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

const businessTypeOptions = [
  { label: 'QUERY', value: 'QUERY' },
  { label: 'ADD', value: 'ADD' },
  { label: 'UPDATE', value: 'UPDATE' },
  { label: 'DELETE', value: 'DELETE' }
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
    <NCollapse :default-expanded-names="['operate-log-search']">
      <NCollapseItem :title="$t('common.search')" name="operate-log-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.operateLog.operateTitle')" class="pr-24px">
              <NInput
                v-model:value="model.operateTitle"
                :placeholder="$t('page.autobox.operateLog.form.operateTitle')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.operateLog.businessType')" class="pr-24px">
              <NSelect v-model:value="model.businessType" clearable :options="businessTypeOptions" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.operateLog.operateName')" class="pr-24px">
              <NInput v-model:value="model.operateName" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.operateLog.operateIp')" class="pr-24px">
              <NInput v-model:value="model.operateIp" />
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
