<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { $t } from '@/locales';

defineOptions({
  name: 'RoleSearch'
});

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.RoleSearchParams>('model', { required: true });

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
    <NCollapse :default-expanded-names="['role-search']">
      <NCollapseItem :title="$t('common.search')" name="role-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.manage.role.roleName')" path="roleName" class="pr-24px">
              <NInput v-model:value="model.roleName" :placeholder="$t('page.manage.role.form.roleName')" />
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

<style scoped></style>
