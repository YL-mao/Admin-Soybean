<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { enabledFlagOptions, lockFlagOptions } from '@/constants/business';
import { translateOptions } from '@/utils/common';
import { $t } from '@/locales';

defineOptions({
  name: 'UserSearch'
});

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.UserSearchParams>('model', { required: true });

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
    <NCollapse :default-expanded-names="['user-search']">
      <NCollapseItem :title="$t('common.search')" name="user-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi
              span="24 s:12 m:6"
              :label="$t('page.manage.user.userAccount')"
              path="userAccount"
              class="pr-24px"
            >
              <NInput v-model:value="model.userAccount" :placeholder="$t('page.manage.user.form.userAccount')" />
            </NFormItemGi>
            <NFormItemGi
              span="24 s:12 m:6"
              :label="$t('page.manage.user.userStatus')"
              path="isEnabled"
              class="pr-24px"
            >
              <NSelect
                v-model:value="model.isEnabled"
                :placeholder="$t('page.manage.user.form.userStatus')"
                :options="translateOptions(enabledFlagOptions)"
                clearable
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.manage.user.userLock')" path="isLock" class="pr-24px">
              <NSelect
                v-model:value="model.isLock"
                :placeholder="$t('page.manage.user.form.userLock')"
                :options="translateOptions(lockFlagOptions)"
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

<style scoped></style>
