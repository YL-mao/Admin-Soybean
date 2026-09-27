<script setup lang="ts">
import { toRaw } from 'vue';
import { jsonClone } from '@sa/utils';
import { $t } from '@/locales';

defineOptions({
  name: 'DictDataSearch'
});

interface Props {
  /** 未选类型时禁用搜索 */
  disabled?: boolean;
}

withDefaults(defineProps<Props>(), {
  disabled: false
});

interface Emits {
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.SystemManage.DictDataSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

function resetModel() {
  const dictTypeCode = model.value.dictTypeCode;
  Object.assign(model.value, defaultModel);
  // 重置时保留当前选中的字典编码
  model.value.dictTypeCode = dictTypeCode;
}

function search() {
  emit('search');
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <NCollapse :default-expanded-names="['dict-data-search']">
      <NCollapseItem :title="$t('common.search')" name="dict-data-search">
        <NForm :model="model" label-placement="left" :label-width="80">
          <NGrid responsive="screen" item-responsive>
            <NFormItemGi span="24 s:12 m:12" :label="$t('page.setting.dict.dictDataLabel')" path="dictDataLabel" class="pr-24px">
              <NInput
                v-model:value="model.dictDataLabel"
                :disabled="disabled"
                :placeholder="$t('page.setting.dict.form.dictDataLabel')"
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

<style scoped></style>
