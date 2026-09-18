<script setup lang="ts">
import { computed, toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { $t } from '@/locales';

defineOptions({ name: 'OperateLogSearch' });

interface Props {
  /** 当前页签，用于切换业务类型选项与搜索字段 */
  tab: 'operate' | 'login' | 'config';
}

interface Emits {
  (e: 'search'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const model = defineModel<Api.AutoboxScaffold.OperateLogSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

const businessTypeOptions = computed(() => {
  if (props.tab === 'login') {
    return [
      { label: 'LOGIN', value: 'LOGIN' },
      { label: 'LOGOUT', value: 'LOGOUT' }
    ];
  }
  if (props.tab === 'config') {
    return [
      { label: 'ADD', value: 'ADD' },
      { label: 'UPDATE', value: 'UPDATE' },
      { label: 'DELETE', value: 'DELETE' }
    ];
  }
  return [
    { label: 'QUERY', value: 'QUERY' },
    { label: 'ADD', value: 'ADD' },
    { label: 'UPDATE', value: 'UPDATE' },
    { label: 'DELETE', value: 'DELETE' },
    { label: 'OTHER', value: 'OTHER' }
  ];
});

function resetModel() {
  const keep = {
    loggingType: model.value.loggingType,
    operateTitleExact: model.value.operateTitleExact,
    operateTitle: model.value.operateTitleExact ? model.value.operateTitle : null
  };
  Object.assign(model.value, defaultModel, keep, {
    businessType: null,
    operateName: null,
    operateIp: null
  });
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
            <NFormItemGi
              v-if="tab === 'operate'"
              span="24 s:12 m:6"
              :label="$t('page.autobox.operateLog.operateTitle')"
              class="pr-24px"
            >
              <NInput
                v-model:value="model.operateTitle"
                :placeholder="$t('page.autobox.operateLog.form.operateTitle')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.operateLog.businessType')" class="pr-24px">
              <NSelect
                v-model:value="model.businessType"
                clearable
                :placeholder="$t('page.autobox.operateLog.form.businessType')"
                :options="businessTypeOptions"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.operateLog.operateName')" class="pr-24px">
              <NInput
                v-model:value="model.operateName"
                :placeholder="$t('page.autobox.operateLog.form.operateName')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.operateLog.operateIp')" class="pr-24px">
              <NInput v-model:value="model.operateIp" :placeholder="$t('page.autobox.operateLog.form.operateIp')" />
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
