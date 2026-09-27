<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { fetchGetConfigGroup, fetchUpdateConfigGroup } from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { $t } from '@/locales';

defineOptions({ name: 'ConfigGroupForm' });

interface Props {
  /** 配置分组，如 upload / security */
  configGroup: string;
  /** 保存所需权限码 */
  permCode: string;
  /** 是否自动加载；抽屉打开时由外部触发也可 */
  autoLoad?: boolean;
  /** 只展示/保存指定编码；不传则整组 */
  configCodes?: string[];
}

const props = withDefaults(defineProps<Props>(), {
  autoLoad: true,
  configCodes: undefined
});

interface Emits {
  (e: 'saved'): void;
}

const emit = defineEmits<Emits>();

const { hasAuth, guardAuth } = useAuth();

const loading = ref(false);
const saving = ref(false);
const items = ref<Api.SystemManage.ConfigGroupItem[]>([]);
/** configId -> 编辑中的字符串值 */
const valueMap = reactive<Record<string, string>>({});

const canSave = computed(() => hasAuth(props.permCode));

function isBooleanType(valueType: string) {
  return valueType === 'boolean';
}

function isNumberType(valueType: string) {
  return valueType === 'number';
}

function isMultilineType(valueType: string) {
  return valueType === 'json' || valueType === 'text';
}

function toBool(raw: string | null | undefined) {
  return String(raw ?? 'false').toLowerCase() === 'true';
}

function setBool(configId: string, checked: boolean) {
  valueMap[configId] = String(checked);
}

function numberValue(configId: string) {
  const n = Number(valueMap[configId]);
  return Number.isFinite(n) ? n : 0;
}

function setNumber(configId: string, value: number | null) {
  valueMap[configId] = String(value ?? 0);
}

async function load() {
  if (!props.configGroup) return;
  loading.value = true;
  const { data, error } = await fetchGetConfigGroup(props.configGroup);
  loading.value = false;
  if (error) {
    items.value = [];
    return;
  }
  const allowCodes = props.configCodes;
  const list = [...(data || [])]
    .filter(item => !allowCodes?.length || allowCodes.includes(item.configCode))
    .sort((a, b) => (a.orderNum ?? 0) - (b.orderNum ?? 0));
  items.value = list;
  list.forEach(item => {
    valueMap[item.configId] = item.configValue ?? '';
  });
}

async function save() {
  if (!guardAuth(props.permCode)) return;
  if (!items.value.length) {
    window.$message?.error($t('page.setting.config.configMissing'));
    return;
  }
  const configs: Api.SystemManage.ConfigGroupSaveItem[] = items.value.map(item => ({
    configId: item.configId,
    configCode: item.configCode,
    configValue: valueMap[item.configId] ?? item.configValue,
    isEnabled: item.isEnabled
  }));
  saving.value = true;
  const { error } = await fetchUpdateConfigGroup({
    configGroup: props.configGroup,
    configs
  });
  saving.value = false;
  if (error) return;
  window.$message?.success($t('common.updateSuccess'));
  emit('saved');
}

watch(
  () => props.configGroup,
  () => {
    if (props.autoLoad) void load();
  },
  { immediate: true }
);

defineExpose({ load, save, loading, saving, canSave });
</script>

<template>
  <NSpin :show="loading">
    <NEmpty v-if="!loading && !items.length" :description="$t('page.setting.config.configMissing')" />
    <!-- 抽屉内也用主题设置行式布局，避免表单标签把输入框拉满 -->
    <div v-else class="flex-col-stretch gap-16px">
      <div
        v-for="item in items"
        :key="item.configId"
        class="w-full flex-y-center justify-between gap-12px"
      >
        <div class="flex-y-center shrink-0">
          <span class="pr-8px text-base-text">{{ item.configName }}</span>
          <IconTooltip v-if="item.configDesc" class="text-14px text-gray-400" :desc="item.configDesc" />
        </div>

        <NSwitch
          v-if="isBooleanType(item.valueType)"
          :value="toBool(valueMap[item.configId])"
          :disabled="!canSave"
          @update:value="checked => setBool(item.configId, checked)"
        />
        <NInputNumber
          v-else-if="isNumberType(item.valueType)"
          size="small"
          class="w-140px"
          :value="numberValue(item.configId)"
          :disabled="!canSave"
          :precision="0"
          @update:value="val => setNumber(item.configId, val)"
        />
        <NInput
          v-else-if="isMultilineType(item.valueType)"
          v-model:value="valueMap[item.configId]"
          size="small"
          type="textarea"
          class="w-220px"
          :autosize="{ minRows: 2, maxRows: 5 }"
          :disabled="!canSave"
          :placeholder="item.configDesc || item.configCode"
        />
        <NInput
          v-else
          v-model:value="valueMap[item.configId]"
          size="small"
          class="w-220px"
          :disabled="!canSave"
          :placeholder="item.configDesc || item.configCode"
        />
      </div>
    </div>
  </NSpin>
</template>
