<script setup lang="tsx">
import { computed, reactive, ref, watch } from 'vue';
import { NButton, NInputNumber, NTag } from 'naive-ui';
import {
  fetchCleanOperateLogByRetention,
  fetchLogConfigGroup,
  fetchOperateLogList,
  fetchUpdateLogConfigGroup
} from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { backendPageTransform, emptyAuthListResponse, useNaivePaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import OperateLogSearch from './modules/operate-log-search.vue';

defineOptions({ name: 'OpsOperateLog' });

type LogTab = 'operate' | 'login' | 'config';

const CONFIG_AUDIT_TITLE = '系统配置变更';

const appStore = useAppStore();
const { hasAuth } = useAuth();

const activeTab = ref<LogTab>('operate');
const showDetail = ref(false);
const showLogConfig = ref(false);
const detailRow = ref<Api.AutoboxScaffold.OperateLog | null>(null);
const logConfigLoading = ref(false);
const logConfigSaving = ref(false);
const logConfigItems = ref<Api.AutoboxScaffold.LogConfigItem[]>([]);
const logConfigForm = reactive({
  loginEnabled: true,
  operateEnabled: true,
  retainDays: 0
});

const searchParams = ref<Api.AutoboxScaffold.OperateLogSearchParams>({
  current: 1,
  size: 10,
  operateTitle: null,
  operateTitleExact: false,
  loggingType: 'OPERATE',
  businessType: null,
  operateName: null,
  operateIp: null
});

function applyTabFilters(tab: LogTab) {
  searchParams.value.current = 1;
  searchParams.value.businessType = null;
  if (tab === 'login') {
    searchParams.value.loggingType = 'LOGIN';
    searchParams.value.operateTitle = null;
    searchParams.value.operateTitleExact = false;
  } else if (tab === 'config') {
    searchParams.value.loggingType = 'OPERATE';
    searchParams.value.operateTitle = CONFIG_AUDIT_TITLE;
    searchParams.value.operateTitleExact = true;
  } else {
    searchParams.value.loggingType = 'OPERATE';
    searchParams.value.operateTitle = null;
    searchParams.value.operateTitleExact = false;
  }
}

function parseConfigAudit(row: Api.AutoboxScaffold.OperateLog): Api.AutoboxScaffold.ConfigAuditItem | null {
  if (!row.requestBody) return null;
  try {
    return JSON.parse(row.requestBody) as Api.AutoboxScaffold.ConfigAuditItem;
  } catch {
    return null;
  }
}

function openDetail(row: Api.AutoboxScaffold.OperateLog) {
  detailRow.value = row;
  showDetail.value = true;
}

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination, reloadColumns } =
  useNaivePaginatedTable({
  api: () =>
    hasAuth('system:log:select')
      ? fetchOperateLogList(searchParams.value)
      : Promise.resolve(emptyAuthListResponse<Api.AutoboxScaffold.OperateLog>()),
  transform: response =>
    backendPageTransform(response, searchParams.value.current || 1, searchParams.value.size || 10),
  onPaginationParamsChange: params => {
    searchParams.value.current = params.page;
    searchParams.value.size = params.pageSize;
  },
  columns: () => {
    if (activeTab.value === 'config') {
      return [
        {
          key: 'configCode',
          title: $t('page.autobox.config.configCode'),
          align: 'center',
          minWidth: 140,
          render: row => parseConfigAudit(row)?.configCode || '-'
        },
        {
          key: 'configName',
          title: $t('page.autobox.config.configName'),
          align: 'center',
          minWidth: 120,
          render: row => parseConfigAudit(row)?.configName || '-'
        },
        {
          key: 'action',
          title: $t('page.autobox.operateLog.auditAction'),
          align: 'center',
          width: 100,
          render: row => parseConfigAudit(row)?.action || row.businessType
        },
        { key: 'operateName', title: $t('page.autobox.operateLog.operateName'), align: 'center', width: 100 },
        { key: 'operateIp', title: $t('page.autobox.operateLog.operateIp'), align: 'center', width: 120 },
        { key: 'operateTime', title: $t('page.autobox.operateLog.operateTime'), align: 'center', width: 170 },
        {
          key: 'operate',
          title: $t('common.operate'),
          align: 'center',
          width: 90,
          render: row => (
            <NButton size="small" ghost type="primary" onClick={() => openDetail(row)}>
              {$t('page.autobox.operateLog.detail')}
            </NButton>
          )
        }
      ];
    }

    const cols: NaiveUI.TableColumn<Api.AutoboxScaffold.OperateLog>[] = [
      { key: 'operateTitle', title: $t('page.autobox.operateLog.operateTitle'), align: 'center', minWidth: 120 },
      { key: 'businessType', title: $t('page.autobox.operateLog.businessType'), align: 'center', width: 100 },
      { key: 'requestMethod', title: $t('page.autobox.operateLog.requestMethod'), align: 'center', width: 90 },
      { key: 'requestUri', title: $t('page.autobox.operateLog.requestUri'), align: 'center', minWidth: 160 },
      { key: 'browser', title: $t('page.autobox.operateLog.browser'), align: 'center', width: 90 },
      { key: 'operateIp', title: $t('page.autobox.operateLog.operateIp'), align: 'center', width: 120 },
      { key: 'systemOs', title: $t('page.autobox.operateLog.systemOs'), align: 'center', width: 100 },
      { key: 'operateTime', title: $t('page.autobox.operateLog.operateTime'), align: 'center', width: 170 },
      { key: 'operateName', title: $t('page.autobox.operateLog.operateName'), align: 'center', width: 90 }
    ];

    if (activeTab.value === 'operate') {
      cols.push({
        key: 'costTime',
        title: $t('page.autobox.operateLog.costTime'),
        align: 'center',
        width: 90
      });
    }

    cols.push(
      {
        key: 'isSuccess',
        title: $t('page.autobox.operateLog.visitStatus'),
        align: 'center',
        width: 100,
        render: row => (
          <NTag type={row.isSuccess === 1 ? 'success' : 'error'}>
            {row.isSuccess === 1 ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no')}
          </NTag>
        )
      },
      {
        key: 'operate',
        title: $t('common.operate'),
        align: 'center',
        width: 90,
        render: row => (
          <NButton size="small" ghost type="primary" onClick={() => openDetail(row)}>
            {$t('page.autobox.operateLog.detail')}
          </NButton>
        )
      }
    );

    return cols;
  }
});

watch(activeTab, tab => {
  applyTabFilters(tab);
  reloadColumns();
  getDataByPage();
});

const detailAudit = computed(() => (detailRow.value ? parseConfigAudit(detailRow.value) : null));

async function openLogConfig() {
  if (!hasAuth('system:config:log')) {
    window.$message?.warning($t('common.noPermission'));
    return;
  }
  showLogConfig.value = true;
  logConfigLoading.value = true;
  const { data: items, error } = await fetchLogConfigGroup();
  logConfigLoading.value = false;
  if (error) return;
  logConfigItems.value = items || [];
  const login = logConfigItems.value.find(i => i.configCode === 'log.loginEn');
  const operate = logConfigItems.value.find(i => i.configCode === 'log.operEn');
  const retain = logConfigItems.value.find(i => i.configCode === 'log.retainDays');
  logConfigForm.loginEnabled = (login?.configValue || 'true').toLowerCase() === 'true';
  logConfigForm.operateEnabled = (operate?.configValue || 'true').toLowerCase() === 'true';
  logConfigForm.retainDays = Number(retain?.configValue || 0) || 0;
}

async function saveLogConfig() {
  if (!hasAuth('system:config:log')) return;
  const payload = logConfigItems.value
    .filter(i => ['log.loginEn', 'log.operEn', 'log.retainDays'].includes(i.configCode))
    .map(item => {
      let configValue = item.configValue;
      if (item.configCode === 'log.loginEn') configValue = String(logConfigForm.loginEnabled);
      if (item.configCode === 'log.operEn') configValue = String(logConfigForm.operateEnabled);
      if (item.configCode === 'log.retainDays') configValue = String(logConfigForm.retainDays);
      return {
        configId: item.configId,
        configCode: item.configCode,
        configValue,
        isEnabled: item.isEnabled
      };
    });
  if (payload.length === 0) {
    window.$message?.error($t('page.autobox.operateLog.configMissing'));
    return;
  }
  logConfigSaving.value = true;
  const { error } = await fetchUpdateLogConfigGroup({ configGroup: 'log', configs: payload });
  logConfigSaving.value = false;
  if (error) return;
  window.$message?.success($t('common.updateSuccess'));
  showLogConfig.value = false;
}

async function cleanByRetention() {
  if (!hasAuth('system:config:log')) return;
  const { data: deleted, error } = await fetchCleanOperateLogByRetention();
  if (error) return;
  window.$message?.success($t('page.autobox.operateLog.cleanSuccess', { count: deleted ?? 0 }));
  getData();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <OperateLogSearch v-model:model="searchParams" :tab="activeTab" @search="getDataByPage" />

    <NCard :bordered="false" size="small" class="card-wrapper sm:flex-1-hidden">
      <template #header-extra>
        <NSpace>
          <NButton v-if="hasAuth('system:config:log')" size="small" @click="openLogConfig">
            {{ $t('page.autobox.operateLog.logConfig') }}
          </NButton>
          <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData">
            <template #default />
          </TableHeaderOperation>
        </NSpace>
      </template>

      <NTabs v-model:value="activeTab" type="line">
        <NTabPane name="operate" :tab="$t('page.autobox.operateLog.tabOperate')" />
        <NTabPane name="login" :tab="$t('page.autobox.operateLog.tabLogin')" />
        <NTabPane name="config" :tab="$t('page.autobox.operateLog.tabConfig')" />
      </NTabs>

      <NDataTable
        :columns="columns"
        :data="data"
        size="small"
        :scroll-x="activeTab === 'config' ? 1100 : 1500"
        :flex-height="!appStore.isMobile"
        :loading="loading"
        remote
        :row-key="row => row.operateId"
        :pagination="mobilePagination"
        class="sm:h-420px"
      />
      <div v-if="!hasAuth('system:log:select')" class="py-24px text-center text-gray-400">
        {{ $t('common.noPermission') }}
      </div>
    </NCard>

    <NModal v-model:show="showDetail" preset="card" :title="$t('page.autobox.operateLog.detailTitle')" class="w-720px">
      <NDescriptions v-if="detailRow" bordered :column="1" label-placement="left" size="small">
        <NDescriptionsItem :label="$t('page.autobox.operateLog.operateTitle')">
          {{ detailRow.operateTitle }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.businessType')">
          {{ detailRow.businessType }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.requestMethod')">
          {{ detailRow.requestMethod }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.requestUri')">
          {{ detailRow.requestUri }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.operateName')">
          {{ detailRow.operateName || '-' }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.operateIp')">
          {{ detailRow.operateIp || '-' }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.browser')">
          {{ detailRow.browser || '-' }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.systemOs')">
          {{ detailRow.systemOs || '-' }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.costTime')">
          {{ detailRow.costTime ?? '-' }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.visitStatus')">
          <NTag :type="detailRow.isSuccess === 1 ? 'success' : 'error'">
            {{ detailRow.isSuccess === 1 ? $t('common.yesOrNo.yes') : $t('common.yesOrNo.no') }}
          </NTag>
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('page.autobox.operateLog.operateTime')">
          {{ detailRow.operateTime }}
        </NDescriptionsItem>
        <template v-if="detailAudit">
          <NDescriptionsItem :label="$t('page.autobox.config.configCode')">
            {{ detailAudit.configCode }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.autobox.config.configName')">
            {{ detailAudit.configName }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.autobox.operateLog.auditAction')">
            {{ detailAudit.action }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.autobox.operateLog.beforeValue')">
            {{ detailAudit.beforeValue ?? '-' }}
          </NDescriptionsItem>
          <NDescriptionsItem :label="$t('page.autobox.operateLog.afterValue')">
            {{ detailAudit.afterValue ?? '-' }}
          </NDescriptionsItem>
        </template>
        <NDescriptionsItem v-else-if="detailRow.requestBody" :label="$t('page.autobox.operateLog.requestBody')">
          <pre class="max-h-160px overflow-auto whitespace-pre-wrap break-all text-12px">{{
            detailRow.requestBody
          }}</pre>
        </NDescriptionsItem>
        <NDescriptionsItem v-if="detailRow.errorMsg" :label="$t('page.autobox.operateLog.errorMsg')">
          {{ detailRow.errorMsg }}
        </NDescriptionsItem>
      </NDescriptions>
    </NModal>

    <NDrawer v-model:show="showLogConfig" :width="400">
      <NDrawerContent :title="$t('page.autobox.operateLog.logConfig')" closable>
        <NSpin :show="logConfigLoading">
          <NForm label-placement="left" :label-width="120">
            <NFormItem :label="$t('page.autobox.operateLog.tabLogin')">
              <NSwitch v-model:value="logConfigForm.loginEnabled" />
            </NFormItem>
            <NFormItem :label="$t('page.autobox.operateLog.tabOperate')">
              <NSwitch v-model:value="logConfigForm.operateEnabled" />
            </NFormItem>
            <NFormItem :label="$t('page.autobox.operateLog.retainDays')">
              <NInputNumber v-model:value="logConfigForm.retainDays" :min="0" :precision="0" class="w-full" />
            </NFormItem>
          </NForm>
        </NSpin>
        <template #footer>
          <NSpace justify="end">
            <NButton @click="cleanByRetention">{{ $t('page.autobox.operateLog.cleanByRetention') }}</NButton>
            <NButton type="primary" :loading="logConfigSaving" @click="saveLogConfig">
              {{ $t('common.confirm') }}
            </NButton>
          </NSpace>
        </template>
      </NDrawerContent>
    </NDrawer>
  </div>
</template>
