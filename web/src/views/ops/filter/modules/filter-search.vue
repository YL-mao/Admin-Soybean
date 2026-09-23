<script setup lang="ts">
import { computed, toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { enabledFlagOptions } from '@/constants/business';
import { $t } from '@/locales';
import { translateOptions } from '@/utils/common';

defineOptions({ name: 'FilterSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.FilterSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

/** 管理端仅暴露 IP / USER_ID */
const filterTypeOptions = computed(() => [
  { label: $t('page.autobox.filter.typeIp'), value: 'IP' },
  { label: $t('page.autobox.filter.typeUser'), value: 'USER_ID' }
]);

const policyModeOptions = computed(() => [
  { label: $t('page.autobox.filter.modeBlack'), value: 'BLACK' },
  { label: $t('page.autobox.filter.modeWhite'), value: 'WHITE' }
]);

const filterSourceOptions = computed(() => [
  { label: $t('page.autobox.filter.sourceManual'), value: 'MANUAL' },
  { label: $t('page.autobox.filter.sourceAuto'), value: 'AUTO' }
]);

function resetModel() {
  Object.assign(model.value, defaultModel);
}

function search() {
  emit('search');
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <NCollapse :default-expanded-names="['filter-search']">
      <NCollapseItem :title="$t('common.search')" name="filter-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.filter.filterType')" class="pr-24px">
              <NSelect
                v-model:value="model.filterType"
                clearable
                :options="filterTypeOptions"
                :placeholder="$t('page.autobox.filter.form.filterType')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.filter.valueLabel')" class="pr-24px">
              <NInput
                v-model:value="model.filterValue"
                :placeholder="$t('page.autobox.filter.form.filterValue')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.filter.policyMode')" class="pr-24px">
              <NSelect
                v-model:value="model.policyMode"
                clearable
                :options="policyModeOptions"
                :placeholder="$t('page.autobox.filter.form.policyMode')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.filter.filterSource')" class="pr-24px">
              <NSelect
                v-model:value="model.filterSource"
                clearable
                :options="filterSourceOptions"
                :placeholder="$t('page.autobox.filter.form.filterSource')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.manage.common.status.enable')" class="pr-24px">
              <NSelect
                v-model:value="model.isEnabled"
                clearable
                :options="translateOptions(enabledFlagOptions)"
                :placeholder="$t('page.autobox.filter.form.isEnabled')"
              />
            </NFormItemGi>
            <!-- Soybean：操作行占满一行，按钮靠右下 -->
            <NFormItemGi span="24" class="pr-24px" :show-label="false" :show-feedback="false">
              <TableSearchActions @reset="resetModel" @search="search" />
            </NFormItemGi>
          </NGrid>
        </NForm>
      </NCollapseItem>
    </NCollapse>
  </NCard>
</template>
