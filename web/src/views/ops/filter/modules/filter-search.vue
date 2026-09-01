<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { enableStatusOptions } from '@/constants/business';
import { translateOptions } from '@/utils/common';
import { $t } from '@/locales';

defineOptions({ name: 'FilterSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.AutoboxScaffold.FilterSearchParams>('model', { required: true });

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
    <NCollapse :default-expanded-names="['filter-search']">
      <NCollapseItem :title="$t('common.search')" name="filter-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.filter.valueLabel')" class="pr-24px">
              <NInput v-model:value="model.valueLabel" :placeholder="$t('page.autobox.filter.form.valueLabel')" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.filter.filterType')" class="pr-24px">
              <NInput v-model:value="model.filterTypeName" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.manage.common.status.enable')" class="pr-24px">
              <NSelect
                v-model:value="model.status"
                :options="translateOptions(enableStatusOptions)"
                clearable
                :placeholder="$t('page.autobox.filter.form.status')"
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
