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

const model = defineModel<Api.SystemManage.OperateLogSearchParams>('model', { required: true });

const defaultModel = jsonClone(toRaw(model.value));

/** 访问状态：成功 / 失败 */
const successOptions = [
  { label: $t('page.ops.operateLog.visitSuccess'), value: 1 as const },
  { label: $t('page.ops.operateLog.visitFail'), value: 0 as const }
];

const businessTypeOptions = computed(() => {
  if (props.tab === 'login') {
    return [
      { label: 'LOGIN', value: 'LOGIN' },
      { label: 'LOGOUT', value: 'LOGOUT' }
    ];
  }
  if (props.tab === 'config') {
    // 筛的是库字段 businessType（@Log），不是 requestBody.action（INSERT/ENABLE）
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

/** 时间范围 ↔ startTime/endTime（后端 yyyy-MM-dd HH:mm:ss） */
const timeRange = computed({
  get(): [string, string] | null {
    if (model.value.startTime && model.value.endTime) {
      return [model.value.startTime, model.value.endTime];
    }
    return null;
  },
  set(val: [string, string] | null) {
    model.value.startTime = val?.[0] ?? null;
    model.value.endTime = val?.[1] ?? null;
  }
});

function resetModel() {
  const keep = {
    logType: model.value.logType,
    operateTitleExact: model.value.operateTitleExact,
    operateTitle: model.value.operateTitleExact ? model.value.operateTitle : null
  };
  Object.assign(model.value, defaultModel, keep, {
    businessType: null,
    operateName: null,
    operateIp: null,
    requestUri: null,
    isSuccess: null,
    startTime: null,
    endTime: null
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
              :label="$t('page.ops.operateLog.operateTitle')"
              class="pr-24px"
            >
              <NInput
                v-model:value="model.operateTitle"
                :placeholder="$t('page.ops.operateLog.form.operateTitle')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.ops.operateLog.businessType')" class="pr-24px">
              <NSelect
                v-model:value="model.businessType"
                clearable
                :placeholder="$t('page.ops.operateLog.form.businessType')"
                :options="businessTypeOptions"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.ops.operateLog.operateName')" class="pr-24px">
              <NInput
                v-model:value="model.operateName"
                :placeholder="$t('page.ops.operateLog.form.operateName')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.ops.operateLog.operateIp')" class="pr-24px">
              <NInput v-model:value="model.operateIp" :placeholder="$t('page.ops.operateLog.form.operateIp')" />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.ops.operateLog.requestUri')" class="pr-24px">
              <NInput
                v-model:value="model.requestUri"
                :placeholder="$t('page.ops.operateLog.form.requestUri')"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.ops.operateLog.visitStatus')" class="pr-24px">
              <NSelect
                v-model:value="model.isSuccess"
                clearable
                :placeholder="$t('page.ops.operateLog.form.isSuccess')"
                :options="successOptions"
              />
            </NFormItemGi>
            <NFormItemGi span="24 s:12 m:6" :label="$t('page.ops.operateLog.timeRange')" class="pr-24px">
              <!-- 输入框最宽对齐双月历 datetimerange 面板，避免占满半行 -->
              <NDatePicker
                v-model:formatted-value="timeRange"
                type="datetimerange"
                value-format="yyyy-MM-dd HH:mm:ss"
                clearable
                class="w-full max-w-640px"
                :placeholder="$t('page.ops.operateLog.form.timeRange')"
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
