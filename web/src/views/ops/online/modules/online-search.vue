<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { $t } from '@/locales';

defineOptions({ name: 'OnlineSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.AutoboxScaffold.OnlineSearchParams>('model', { required: true });

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
    <NCollapse :default-expanded-names="['online-search']">
      <NCollapseItem :title="$t('common.search')" name="online-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.online.userAccount')" class="pr-24px">
              <NInput v-model:value="model.userAccount" :placeholder="$t('page.autobox.online.form.userAccount')" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.online.userName')" class="pr-24px">
              <NInput v-model:value="model.userName" :placeholder="$t('page.autobox.online.form.userName')" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.online.loginIp')" class="pr-24px">
              <NInput v-model:value="model.loginIp" :placeholder="$t('page.autobox.online.form.loginIp')" />
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
