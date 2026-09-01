<script setup lang="ts">
import { toRaw } from 'vue';

import { jsonClone } from '@sa/utils';

import { enableStatusOptions } from '@/constants/business';

import { translateOptions } from '@/utils/common';

import { $t } from '@/locales';

defineOptions({ name: 'PostSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.AutoboxScaffold.PostSearchParams>('model', { required: true });

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
    <NCollapse :default-expanded-names="['post-search']">
      <NCollapseItem :title="$t('common.search')" name="post-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.post.postName')" path="postName" class="pr-24px">
              <NInput v-model:value="model.postName" :placeholder="$t('page.autobox.post.form.postName')" />
            </NFormItemGi>

            <NFormItemGi span="24 s:12 m:6" :label="$t('page.autobox.post.postCode')" path="postCode" class="pr-24px">
              <NInput v-model:value="model.postCode" :placeholder="$t('page.autobox.post.form.postCode')" />
            </NFormItemGi>

            <NFormItemGi
              span="24 s:12 m:6"
              :label="$t('page.manage.common.status.enable')"
              path="status"
              class="pr-24px"
            >
              <NSelect
                v-model:value="model.status"
                :placeholder="$t('page.autobox.post.form.status')"
                :options="translateOptions(enableStatusOptions)"
                clearable
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

<style scoped></style>
