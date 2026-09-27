<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { fetchGetConfigGroup, fetchUpdateConfigGroup } from '@/service/api';
import { useAuth } from '@/hooks/business/auth';
import { $t } from '@/locales';
import { resolveBackendAssetUrl } from '@/utils/service';
import { useBrandingStore } from '@/store/modules/branding';

defineOptions({ name: 'SettingConfig' });

/**
 * 系统信息页：窄卡片居中；分区用 NDivider。
 * 行布局为标签定宽 + 输入 flex-1，避免 SettingItem 右对齐导致输入框宽窄不一。
 */
const SECTION_DEFS: { titleKey: App.I18n.I18nKey; codes: string[] }[] = [
  {
    titleKey: 'page.setting.config.sectionBrand',
    codes: ['system.name', 'system.shortName', 'system.copyright', 'system.logo', 'system.favicon']
  },
  {
    titleKey: 'page.setting.config.sectionContact',
    codes: ['system.adminMail', 'system.version', 'system.website']
  },
  {
    titleKey: 'page.setting.config.sectionFiling',
    codes: ['system.icp', 'system.policeIcp']
  }
];

/** 需要右侧缩略图预览的图片类配置码 */
const IMAGE_PREVIEW_CODES = new Set(['system.logo', 'system.favicon']);

const { hasAuth, guardAuth } = useAuth();
const brandingStore = useBrandingStore();

const loading = ref(false);
const saving = ref(false);
const items = ref<Api.SystemManage.ConfigGroupItem[]>([]);
const valueMap = reactive<Record<string, string>>({});

const canSave = computed(() => hasAuth('system:config:system'));

const itemByCode = computed(() => {
  const map = new Map<string, Api.SystemManage.ConfigGroupItem>();
  items.value.forEach(item => map.set(item.configCode, item));
  return map;
});

const sections = computed(() => {
  const used = new Set<string>();
  const result = SECTION_DEFS.map(def => {
    const list = def.codes
      .map(code => itemByCode.value.get(code))
      .filter((item): item is Api.SystemManage.ConfigGroupItem => Boolean(item));
    list.forEach(item => used.add(item.configCode));
    return { titleKey: def.titleKey, items: list };
  }).filter(section => section.items.length > 0);

  const rest = items.value.filter(item => !used.has(item.configCode));
  if (rest.length) {
    result.push({ titleKey: 'page.setting.config.sectionOther' as App.I18n.I18nKey, items: rest });
  }
  return result;
});

function isImagePreview(code: string) {
  return IMAGE_PREVIEW_CODES.has(code);
}

async function load() {
  loading.value = true;
  const { data, error } = await fetchGetConfigGroup('system');
  loading.value = false;
  if (error) {
    items.value = [];
    return;
  }
  const list = [...(data || [])].sort((a, b) => (a.orderNum ?? 0) - (b.orderNum ?? 0));
  items.value = list;
  list.forEach(item => {
    valueMap[item.configId] = item.configValue ?? '';
  });
}

async function save() {
  if (!guardAuth('system:config:system')) return;
  if (!items.value.length) {
    window.$message?.error($t('page.setting.config.configMissing'));
    return;
  }
  saving.value = true;
  const { error } = await fetchUpdateConfigGroup({
    configGroup: 'system',
    configs: items.value.map(item => ({
      configId: item.configId,
      configCode: item.configCode,
      configValue: valueMap[item.configId] ?? item.configValue,
      isEnabled: item.isEnabled
    }))
  });
  saving.value = false;
  if (error) return;
  // 保存后刷新全局品牌，登录页/布局立刻生效
  await brandingStore.fetchBranding();
  window.$message?.success($t('common.updateSuccess'));
}

void load();
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px">
    <NCard
      :title="$t('page.setting.config.title')"
      :bordered="false"
      size="small"
      class="card-wrapper mx-auto max-w-640px"
    >
      <template #header-extra>
        <NButton v-if="canSave" type="primary" size="small" :loading="saving" @click="save">
          {{ $t('common.update') }}
        </NButton>
      </template>

      <NSpin :show="loading">
        <NEmpty v-if="!loading && !items.length" :description="$t('page.setting.config.configMissing')" />
        <div v-else>
          <template v-for="section in sections" :key="section.titleKey">
            <NDivider>{{ $t(section.titleKey) }}</NDivider>
            <div class="flex-col-stretch gap-12px">
              <!-- 标签定宽 + 输入吃剩余宽度，保证每行输入框左缘对齐且同宽 -->
              <div
                v-for="item in section.items"
                :key="item.configId"
                class="w-full flex gap-12px"
                :class="isImagePreview(item.configCode) ? 'items-start' : 'items-center'"
              >
                <div
                  class="w-100px flex-y-center shrink-0"
                  :class="{ 'pt-4px': isImagePreview(item.configCode) }"
                >
                  <span class="pr-4px text-base-text">{{ item.configName }}</span>
                  <IconTooltip
                    v-if="item.configDesc"
                    class="text-14px text-gray-400"
                    :desc="item.configDesc"
                  />
                </div>
                <div class="min-w-0 flex-1 flex-col gap-8px">
                  <!-- Logo / Favicon：预览在输入框上方，点击可放大 -->
                  <NImage
                    v-if="isImagePreview(item.configCode) && valueMap[item.configId]?.trim()"
                    :src="resolveBackendAssetUrl(valueMap[item.configId])"
                    :width="96"
                    :height="96"
                    object-fit="contain"
                    class="rd-4px"
                  />
                  <NInput
                    v-model:value="valueMap[item.configId]"
                    size="small"
                    class="w-full"
                    :disabled="!canSave"
                    :placeholder="item.configCode"
                  />
                </div>
              </div>
            </div>
          </template>
        </div>
      </NSpin>
    </NCard>
  </div>
</template>
