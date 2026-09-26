<script setup lang="ts">
import { ref, toRaw } from 'vue';
import type { SelectOption } from 'naive-ui';
import { jsonClone } from '@sa/utils';
import { fetchSearchUser } from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { $t } from '@/locales';

defineOptions({ name: 'OnlineSearch' });

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.OnlineSearchParams>('model', { required: true });

const { hasAuth } = useAuth();

const defaultModel = jsonClone(toRaw(model.value));

const userOptions = ref<SelectOption[]>([]);

function resetModel() {
  Object.assign(model.value, defaultModel);
  userOptions.value = [];
}

function search() {
  emit('search');
}

/** 远程搜人；无检索权限时不请求 */
async function handleUserSearch(keyword: string) {
  if (!keyword) {
    userOptions.value = [];
    return;
  }
  if (!hasAuth('system:user:search')) {
    userOptions.value = [];
    return;
  }
  const { data, error } = await fetchSearchUser(keyword);
  if (error || !data) return;
  userOptions.value = data.map(item => ({
    label: item.label || `${item.userName}(${item.userAccount})`,
    value: item.userId
  }));
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <NCollapse :default-expanded-names="['online-search']">
      <NCollapseItem :title="$t('common.search')" name="online-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:8" :label="$t('page.autobox.online.user')" class="pr-24px">
              <NSelect
                v-model:value="model.userId"
                filterable
                remote
                clearable
                :options="userOptions"
                :placeholder="$t('page.autobox.online.form.user')"
                :disabled="!hasAuth('system:user:search')"
                @search="handleUserSearch"
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
