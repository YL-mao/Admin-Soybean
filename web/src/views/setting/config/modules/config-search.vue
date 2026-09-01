<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { enableStatusOptions } from '@/constants/business';
import { translateOptions } from '@/utils/common';
import { $t } from '@/locales';

defineOptions({ name: 'ConfigSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.AutoboxScaffold.ConfigSearchParams>('model', { required: true });

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
    <NCollapse :default-expanded-names="['config-search']">
      <NCollapseItem :title="$t('common.search')" name="config-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.config.configGroup')" class="pr-24px">
              <NInput v-model:value="model.configGroup" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.config.configName')" class="pr-24px">
              <NInput v-model:value="model.configName" :placeholder="$t('page.autobox.config.form.configName')" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.config.configCode')" class="pr-24px">
              <NInput v-model:value="model.configCode" :placeholder="$t('page.autobox.config.form.configCode')" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.manage.common.status.enable')" class="pr-24px">
              <NSelect v-model:value="model.status" clearable :options="translateOptions(enableStatusOptions)" />
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
