<script setup lang="tsx">
import { computed, ref, watch } from 'vue';
import type { SelectOption, TreeSelectOption } from 'naive-ui';
import { getIcon, Icon } from '@iconify/vue';
import { enabledFlagOptions, menuIconTypeOptions, menuTypeOptions } from '@/constants/business';
import {
  fetchCheckMenuCodeUnique,
  fetchCheckMenuNameUnique,
  fetchCreateMenu,
  fetchGetMenuParentOptions,
  fetchUpdateMenu
} from '@/service/api';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { translateOptions } from '@/utils/common';
import { getLocalIcons, getMdiIconNames } from '@/utils/icon';
import { $t } from '@/locales';
import SvgIcon from '@/components/custom/svg-icon.vue';
import {
  buildMenuParentTree,
  getLayoutAndPage,
  getRoutePathByRouteName,
  transformLayoutAndPageToComponent
} from './shared';

defineOptions({
  name: 'MenuOperateModal'
});

export type OperateType = NaiveUI.TableOperateType | 'addChild';

interface Props {
  /** the type of operation */
  operateType: OperateType;
  /** the edit menu data or the parent menu data when adding a child menu */
  rowData?: Api.SystemManage.Menu | null;
  /** 菜单树中已使用的 Iconify，并入下拉候选 */
  iconifyIcons?: string[];
}

const props = withDefaults(defineProps<Props>(), {
  rowData: null,
  iconifyIcons: () => []
});

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule, createRequiredRule } = useFormRules();

const title = computed(() => {
  const titles: Record<OperateType, string> = {
    add: $t('page.manage.menu.addMenu'),
    addChild: $t('page.manage.menu.addChildMenu'),
    edit: $t('page.manage.menu.editMenu')
  };
  return titles[props.operateType];
});

type Model = {
  parentId: string;
  menuType: Api.SystemManage.MenuType;
  menuName: string;
  menuDesc: string;
  routeName: string;
  routePath: string;
  routeComp: string;
  routeQuery: string;
  menuHref: string;
  isBlank: Api.SystemManage.EnabledFlag;
  permCode: string;
  menuIcon: string;
  iconType: Api.SystemManage.IconType;
  i18nKey: string;
  keepAlive: Api.SystemManage.EnabledFlag;
  isShow: Api.SystemManage.EnabledFlag;
  activeMenu: string;
  orderNum: number;
  isEnabled: Api.SystemManage.EnabledFlag;
  layout: string;
  page: string;
};

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    parentId: '0',
    menuType: 1,
    menuName: '',
    menuDesc: '',
    routeName: '',
    routePath: '',
    routeComp: '',
    routeQuery: '',
    menuHref: '',
    isBlank: 0,
    permCode: '',
    menuIcon: '',
    // 菜单种子/侧栏用的都是 Iconify，默认走这条通道
    iconType: 1,
    i18nKey: '',
    keepAlive: 0,
    isShow: 1,
    activeMenu: '',
    orderNum: 0,
    // 表单默认禁用，仍允许手动改启用
    isEnabled: 0,
    layout: '',
    page: ''
  };
}

const rules = computed(() => {
  const base: Record<string, App.Global.FormRule | App.Global.FormRule[]> = {
    menuName: defaultRequiredRule,
    menuType: defaultRequiredRule,
    orderNum: defaultRequiredRule,
    isEnabled: defaultRequiredRule,
    parentId: defaultRequiredRule
  };

  // 目录/菜单需要路由；按钮需要权限标识
  if (model.value.menuType !== 2) {
    base.routeName = defaultRequiredRule;
    base.routePath = defaultRequiredRule;
  } else {
    base.permCode = [
      createRequiredRule($t('page.manage.menu.form.permCode')),
      {
        pattern: /^[a-zA-Z0-9_.:-]+$/,
        message: $t('page.manage.menu.form.permCode'),
        trigger: 'blur'
      }
    ];
  }

  return base;
});

const disabledMenuType = computed(() => props.operateType === 'edit');

const localIcons = getLocalIcons();
const localIconOptions = localIcons.map<SelectOption>(item => ({
  label: () => (
    <div class="flex-y-center gap-16px">
      <SvgIcon localIcon={item} class="text-icon" />
      <span>{item}</span>
    </div>
  ),
  value: item
}));

/** 无关键字时展示的常用 / 已用图标，避免一次塞入全量 MDI */
const ICONIFY_PRESET = [
  'mdi:monitor-dashboard',
  'mdi:cloud-cog-outline',
  'mdi:routes',
  'mdi:account-badge-outline',
  'mdi:account-cog-outline',
  'mdi:account-group-outline',
  'mdi:sitemap-outline',
  'mdi:briefcase-outline',
  'mdi:cog-outline',
  'mdi:tune-variant',
  'mdi:book-alphabet',
  'mdi:file-multiple-outline',
  'mdi:bullhorn-outline',
  'mdi:chart-box-outline',
  'mdi:shield-lock-outline',
  'mdi:filter-outline',
  'mdi:clock-outline',
  'mdi:text-box-outline',
  'mdi:account-check-outline',
  'mdi:history',
  'mdi:tools',
  'mdi:api',
  'mdi:account',
  'mdi:bell-outline'
];

/** 远程检索每次最多展示条数，防止 7000+ 选项卡死下拉 */
const ICONIFY_SEARCH_LIMIT = 80;

const iconifyKeyword = ref('');

function defaultIconifyCandidates(): string[] {
  const set = new Set<string>([...ICONIFY_PRESET, ...props.iconifyIcons]);
  if (model.value.menuIcon && model.value.iconType === 1) {
    set.add(model.value.menuIcon);
  }
  return [...set].filter(Boolean).sort();
}

/** 按关键字检索本地 MDI；空关键字回常用列表。优先前缀匹配 */
function searchMdiIcons(keyword: string): string[] {
  const raw = keyword.trim().toLowerCase();
  if (!raw) {
    return defaultIconifyCandidates();
  }

  const q = raw.startsWith('mdi:') ? raw.slice(4) : raw;
  const starts: string[] = [];
  const includes: string[] = [];

  for (const name of getMdiIconNames()) {
    const id = name.slice(4).toLowerCase();
    if (id.startsWith(q)) {
      starts.push(name);
    } else if (id.includes(q)) {
      includes.push(name);
    }
    if (starts.length >= ICONIFY_SEARCH_LIMIT) {
      break;
    }
  }

  return [...starts, ...includes].slice(0, ICONIFY_SEARCH_LIMIT);
}

function renderIconifyOptionLabel(name: string) {
  // 本地 addCollection 后 getIcon 同步可读，避免下拉空白
  const data = getIcon(name);
  return (
    <div class="flex-y-center gap-12px min-w-0">
      <span class="inline-flex h-20px w-20px shrink-0 items-center justify-center text-icon">
        {data ? <Icon icon={data} /> : null}
      </span>
      <span class="truncate">{name}</span>
    </div>
  );
}

function toIconifyOption(name: string): SelectOption {
  return {
    label: () => renderIconifyOptionLabel(name),
    value: name
  };
}

const iconifyOptions = computed<SelectOption[]>(() => searchMdiIcons(iconifyKeyword.value).map(toIconifyOption));

/** 当前值不在候选里时仍能显示图标文字 */
function fallbackIconifyOption(value: string | number) {
  return toIconifyOption(String(value));
}

function handleIconifySearch(query: string) {
  iconifyKeyword.value = query;
}

function handleIconifyDropdownShow(show: boolean) {
  if (show) {
    iconifyKeyword.value = '';
  }
}

const showLayout = computed(() => model.value.menuType === 0);
const showPage = computed(() => model.value.menuType === 1);
const showRouteFields = computed(() => model.value.menuType !== 2);
/** 仅按钮可填权限码；目录/页面禁用并由提交时清空 */
const permCodeDisabled = computed(() => model.value.menuType !== 2);

const parentOptions = ref<TreeSelectOption[]>([]);

async function loadParentOptions() {
  const { error, data } = await fetchGetMenuParentOptions();
  if (error || !data) {
    parentOptions.value = [];
    return;
  }
  const tree = buildMenuParentTree(data);
  parentOptions.value = mapParentTreeOptions(tree);
}

function mapParentTreeOptions(nodes: Api.SystemManage.MenuParent[]): TreeSelectOption[] {
  return nodes.map(node => ({
    key: node.menuId,
    label: node.menuName,
    children: node.children?.length ? mapParentTreeOptions(node.children) : undefined
  }));
}

const layoutOptions: CommonType.Option[] = [
  { label: 'base', value: 'base' },
  { label: 'blank', value: 'blank' }
];

function handleInitModel() {
  model.value = createDefaultModel();

  if (!props.rowData) return;

  if (props.operateType === 'addChild') {
    Object.assign(model.value, { parentId: props.rowData.menuId });
  }

  if (props.operateType === 'edit') {
    const row = props.rowData;
    const { layout, page } = getLayoutAndPage(row.routeComp);
    Object.assign(model.value, {
      parentId: row.parentId || '0',
      menuType: row.menuType,
      menuName: row.menuName || '',
      menuDesc: row.menuDesc || '',
      routeName: row.routeName || '',
      routePath: row.routePath || '',
      routeComp: row.routeComp || '',
      routeQuery: row.routeQuery || '',
      menuHref: row.menuHref || '',
      isBlank: row.isBlank ?? 0,
      permCode: row.permCode || '',
      menuIcon: row.menuIcon || '',
      iconType: row.iconType ?? 1,
      i18nKey: row.i18nKey || '',
      keepAlive: row.keepAlive ?? 0,
      isShow: row.isShow ?? 1,
      activeMenu: row.activeMenu || '',
      orderNum: row.orderNum ?? 0,
      isEnabled: row.isEnabled ?? 0,
      layout,
      page
    });
    // 目录/页面不展示也不保留权限码
    if (model.value.menuType !== 2) {
      model.value.permCode = '';
    }
  }
}

function closeDrawer() {
  visible.value = false;
}

function handleUpdateRoutePathByRouteName() {
  if (model.value.routeName) {
    model.value.routePath = getRoutePathByRouteName(model.value.routeName);
  } else {
    model.value.routePath = '';
  }
}

function handleUpdateI18nKeyByRouteName() {
  if (model.value.routeName) {
    model.value.i18nKey = `route.${model.value.routeName}`;
  } else {
    model.value.i18nKey = '';
  }
}

function buildSubmitBody(): Api.SystemManage.MenuInsert {
  const routeComp =
    model.value.menuType === 2
      ? null
      : transformLayoutAndPageToComponent(model.value.layout, model.value.page) || model.value.routeComp || null;

  return {
    parentId: model.value.parentId || '0',
    menuName: model.value.menuName,
    menuDesc: model.value.menuDesc || null,
    menuType: model.value.menuType,
    routeName: model.value.menuType === 2 ? null : model.value.routeName || null,
    routePath: model.value.menuType === 2 ? null : model.value.routePath || null,
    routeComp,
    routeQuery: model.value.routeQuery || null,
    menuHref: model.value.menuHref || null,
    isBlank: model.value.isBlank,
    // 非按钮不提交权限码，避免脏值落库
    permCode: model.value.menuType === 2 ? model.value.permCode || null : null,
    menuIcon: model.value.menuIcon || null,
    iconType: model.value.menuIcon ? model.value.iconType : null,
    i18nKey: model.value.i18nKey || null,
    keepAlive: model.value.keepAlive,
    isShow: model.value.menuType === 2 ? 0 : model.value.isShow,
    activeMenu: model.value.activeMenu || null,
    orderNum: model.value.orderNum,
    isEnabled: model.value.isEnabled
  };
}

async function handleSubmit() {
  await validate();

  // 提交前做名称/权限码唯一校验（编辑且未改时可仍通过）
  const { data: nameOk, error: nameErr } = await fetchCheckMenuNameUnique({
    parentId: model.value.parentId || '0',
    menuName: model.value.menuName
  });
  if (nameErr) return;
  if (
    nameOk === false &&
    !(props.operateType === 'edit' && props.rowData?.menuName === model.value.menuName && props.rowData.parentId === model.value.parentId)
  ) {
    window.$message?.error($t('page.manage.menu.form.menuName'));
    return;
  }

  if (model.value.menuType === 2 && model.value.permCode) {
    const { data: codeOk, error: codeErr } = await fetchCheckMenuCodeUnique({
      parentId: model.value.parentId || '0',
      permCode: model.value.permCode
    });
    if (codeErr) return;
    if (
      codeOk === false &&
      !(
        props.operateType === 'edit' &&
        props.rowData?.permCode === model.value.permCode &&
        props.rowData.parentId === model.value.parentId
      )
    ) {
      window.$message?.error($t('page.manage.menu.form.permCode'));
      return;
    }
  }

  const body = buildSubmitBody();
  if (props.operateType === 'edit' && props.rowData) {
    const { error } = await fetchUpdateMenu({ ...body, menuId: props.rowData.menuId });
    if (error) return;
    window.$message?.success($t('common.updateSuccess'));
  } else {
    const { error } = await fetchCreateMenu(body);
    if (error) return;
    window.$message?.success($t('common.addSuccess'));
  }

  closeDrawer();
  emit('submitted');
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
    iconifyKeyword.value = '';
    void loadParentOptions();
  }
});

/** 切到目录/页面时清空权限码，避免禁用框残留旧值 */
watch(
  () => model.value.menuType,
  type => {
    if (!visible.value) return;
    if (type !== 2) {
      model.value.permCode = '';
    }
  }
);

watch(
  () => model.value.routeName,
  () => {
    if (props.operateType === 'edit') return;
    handleUpdateRoutePathByRouteName();
    handleUpdateI18nKeyByRouteName();
  }
);
</script>

<template>
  <NModal v-model:show="visible" :title="title" preset="card" class="w-800px">
    <NScrollbar class="h-480px pr-20px">
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
        <NGrid responsive="screen" item-responsive>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.parentId')" path="parentId">
            <NTreeSelect
              v-model:value="model.parentId"
              :options="parentOptions"
              key-field="key"
              label-field="label"
              default-expand-all
              :placeholder="$t('page.manage.menu.form.parentId')"
            />
          </NFormItemGi>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.menuType')" path="menuType">
            <NRadioGroup v-model:value="model.menuType" :disabled="disabledMenuType">
              <NRadio v-for="item in menuTypeOptions" :key="item.value" :value="item.value" :label="$t(item.label)" />
            </NRadioGroup>
          </NFormItemGi>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.menuName')" path="menuName">
            <NInput v-model:value="model.menuName" :placeholder="$t('page.manage.menu.form.menuName')" />
          </NFormItemGi>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.menuDesc')" path="menuDesc">
            <NInput v-model:value="model.menuDesc" :placeholder="$t('page.manage.menu.form.menuDesc')" />
          </NFormItemGi>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.permCode')" path="permCode">
            <NInput
              v-model:value="model.permCode"
              :disabled="permCodeDisabled"
              :placeholder="$t('page.manage.menu.form.permCode')"
            />
          </NFormItemGi>
          <template v-if="showRouteFields">
            <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.routeName')" path="routeName">
              <NInput v-model:value="model.routeName" :placeholder="$t('page.manage.menu.form.routeName')" />
            </NFormItemGi>
            <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.routePath')" path="routePath">
              <NInput v-model:value="model.routePath" :placeholder="$t('page.manage.menu.form.routePath')" />
            </NFormItemGi>
            <NFormItemGi v-if="showLayout" span="24 m:12" :label="$t('page.manage.menu.layout')" path="layout">
              <NSelect
                v-model:value="model.layout"
                :options="layoutOptions"
                clearable
                :placeholder="$t('page.manage.menu.form.layout')"
              />
            </NFormItemGi>
            <NFormItemGi v-if="showPage" span="24 m:12" :label="$t('page.manage.menu.page')" path="page">
              <NInput v-model:value="model.page" :placeholder="$t('page.manage.menu.form.page')" />
            </NFormItemGi>
            <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.i18nKey')" path="i18nKey">
              <NInput v-model:value="model.i18nKey" :placeholder="$t('page.manage.menu.form.i18nKey')" />
            </NFormItemGi>
            <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.href')" path="menuHref">
              <NInput v-model:value="model.menuHref" :placeholder="$t('page.manage.menu.form.href')" />
            </NFormItemGi>
            <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.query')" path="routeQuery">
              <NInput v-model:value="model.routeQuery" :placeholder="$t('page.manage.menu.form.query')" />
            </NFormItemGi>
            <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.keepAlive')" path="keepAlive">
              <NRadioGroup v-model:value="model.keepAlive">
                <NRadio :value="1" :label="$t('common.yesOrNo.yes')" />
                <NRadio :value="0" :label="$t('common.yesOrNo.no')" />
              </NRadioGroup>
            </NFormItemGi>
            <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.hideInMenu')" path="isShow">
              <NRadioGroup v-model:value="model.isShow">
                <!-- isShow=0 为隐藏侧栏 -->
                <NRadio :value="0" :label="$t('common.yesOrNo.yes')" />
                <NRadio :value="1" :label="$t('common.yesOrNo.no')" />
              </NRadioGroup>
            </NFormItemGi>
            <NFormItemGi v-if="model.isShow === 0" span="24 m:12" :label="$t('page.manage.menu.activeMenu')" path="activeMenu">
              <NInput v-model:value="model.activeMenu" :placeholder="$t('page.manage.menu.form.activeMenu')" />
            </NFormItemGi>
            <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.isBlank')" path="isBlank">
              <NRadioGroup v-model:value="model.isBlank">
                <NRadio :value="1" :label="$t('common.yesOrNo.yes')" />
                <NRadio :value="0" :label="$t('common.yesOrNo.no')" />
              </NRadioGroup>
            </NFormItemGi>
          </template>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.order')" path="orderNum">
            <NInputNumber v-model:value="model.orderNum" class="w-full" :placeholder="$t('page.manage.menu.form.order')" />
          </NFormItemGi>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.iconTypeTitle')" path="iconType">
            <NSelect
              v-model:value="model.iconType"
              :options="translateOptions(menuIconTypeOptions)"
              :placeholder="$t('page.manage.menu.iconTypeTitle')"
            />
          </NFormItemGi>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.icon')" path="menuIcon">
            <!-- Iconify：本地 MDI 全集按关键字检索；无关键字时只展示常用/已用 -->
            <NSelect
              v-if="model.iconType === 1"
              v-model:value="model.menuIcon"
              filterable
              remote
              clearable
              :options="iconifyOptions"
              :fallback-option="fallbackIconifyOption"
              :placeholder="$t('page.manage.menu.form.icon')"
              @search="handleIconifySearch"
              @update:show="handleIconifyDropdownShow"
            />
            <NSelect
              v-else
              v-model:value="model.menuIcon"
              filterable
              clearable
              :placeholder="$t('page.manage.menu.form.localIcon')"
              :options="localIconOptions"
            />
          </NFormItemGi>
          <NFormItemGi span="24 m:12" :label="$t('page.manage.menu.menuStatus')" path="isEnabled">
            <NRadioGroup v-model:value="model.isEnabled">
              <NRadio
                v-for="item in enabledFlagOptions"
                :key="item.value"
                :value="item.value"
                :label="$t(item.label)"
              />
            </NRadioGroup>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </NScrollbar>
    <template #footer>
      <NSpace justify="end" :size="16">
        <NButton @click="closeDrawer">{{ $t('common.cancel') }}</NButton>
        <NButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
