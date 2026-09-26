<script setup lang="tsx">
import { computed, onMounted, reactive, ref, toRef, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { NTag } from 'naive-ui';
import { userSexOptions } from '@/constants/business';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { backendPageTransform, useNaivePaginatedTable } from '@/hooks/common/table';
import {
  fetchGetOwnLoginLogs,
  fetchGetUserProfileDetail,
  fetchOverwriteFile,
  fetchUpdateOwnAvatar,
  fetchUpdateOwnPassword,
  fetchUpdateUserProfile,
  fetchUploadFile
} from '@/service/api';
import { useAuthStore } from '@/store/modules/auth';
import { $t } from '@/locales';

defineOptions({ name: 'AccountInfo' });

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const activeTab = ref<'profile' | 'password' | 'loginLog'>('profile');
const loadingDetail = ref(false);
const savingProfile = ref(false);
const savingPwd = ref(false);
const uploadingAvatar = ref(false);
const avatarInputRef = ref<HTMLInputElement | null>(null);
/** 覆盖后同路径需换戳，否则浏览器/ NAvatar 仍用旧缓存 */
const avatarCacheKey = ref(Date.now());

const profile = ref<Api.SystemManage.UserProfileDetail | null>(null);

const { formRef: profileFormRef, validate: validateProfile, restoreValidation: restoreProfileValidation } =
  useNaiveForm();
const { formRef: pwdFormRef, validate: validatePwd } = useNaiveForm();
const { createRequiredRule, createConfirmPwdRule, defaultRequiredRule } = useFormRules();

const profileModel = reactive({
  userName: '',
  userSex: null as Api.SystemManage.UserSex | null,
  userEmail: '',
  userPhone: ''
});

const pwdModel = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

/** 最近登录分页查询参数（与其它列表页一致：current/size） */
const loginLogSearch = ref<Api.SystemManage.CommonSearchParams>({
  current: 1,
  size: 10
});

const {
  columns: loginLogColumns,
  data: loginLogs,
  getDataByPage: getLoginLogsByPage,
  loading: loadingLogs,
  mobilePagination: loginLogPagination
} = useNaivePaginatedTable({
  // 进「最近登录」Tab 再拉，避免进页就请求
  immediate: false,
  api: () => fetchGetOwnLoginLogs(loginLogSearch.value),
  transform: response =>
    backendPageTransform(response, loginLogSearch.value.current || 1, loginLogSearch.value.size || 10),
  onPaginationParamsChange: params => {
    loginLogSearch.value.current = params.page || 1;
    loginLogSearch.value.size = params.pageSize || 10;
  },
  columns: () => [
    {
      key: 'loginTime',
      title: $t('page.autobox.account.loginTime'),
      align: 'center',
      minWidth: 170
    },
    {
      key: 'loginIp',
      title: $t('page.autobox.account.loginIp'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'browser',
      title: $t('page.autobox.account.browser'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'systemOs',
      title: $t('page.autobox.account.systemOs'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'current',
      title: $t('page.autobox.account.currentSession'),
      align: 'center',
      width: 100,
      render: (row: Api.SystemManage.UserOwnLoginLog) =>
        row.current ? <NTag type="success">{$t('common.yesOrNo.yes')}</NTag> : <NTag>{$t('common.yesOrNo.no')}</NTag>
    }
  ]
});

/** 对齐后端 PasswordPolicyService：8～64 位，字母+数字+特殊字符 */
const passwordPolicyRule: App.Global.FormRule = {
  validator: (_rule, value: string) => {
    if (!value) {
      return new Error($t('page.autobox.account.form.newPassword'));
    }
    if (value.length < 8 || value.length > 64) {
      return new Error($t('page.manage.user.passwordPolicy'));
    }
    if (!/[A-Za-z]/.test(value) || !/\d/.test(value) || !/[^A-Za-z0-9]/.test(value)) {
      return new Error($t('page.manage.user.passwordPolicy'));
    }
    return true;
  },
  trigger: ['input', 'blur']
};

const profileRules = {
  userName: defaultRequiredRule,
  userSex: createRequiredRule($t('page.manage.user.form.userSex'))
};

const pwdRules = {
  oldPassword: createRequiredRule($t('page.autobox.account.form.oldPassword')),
  newPassword: [createRequiredRule($t('page.autobox.account.form.newPassword')), passwordPolicyRule],
  confirmPassword: createConfirmPwdRule(toRef(pwdModel, 'newPassword'))
};

/**
 * 头像直链：开发态走 Vite `/upload` 专用代理（不要拼 /proxy-default）。
 * 绝对 URL / data / blob 原样用。
 */
const avatarSrc = computed(() => {
  const raw = (profile.value?.userAvatar ?? '').trim();
  if (!raw) return '';
  if (/^(https?:)?\/\//i.test(raw) || raw.startsWith('data:') || raw.startsWith('blob:')) {
    return raw;
  }
  const path = raw.startsWith('/') ? raw : `/${raw}`;
  return `${path}?_t=${avatarCacheKey.value}`;
});

/** 从 userAvatar（/upload/{fileId} 或纯 id）解析可覆盖的 fileId */
function parseAvatarFileId(avatar?: string | null): string | null {
  const raw = (avatar ?? '').trim();
  if (!raw) return null;
  const matched = raw.match(/\/upload\/([^/?#]+)/i);
  if (matched?.[1]) return matched[1];
  if (/^\d{15,}$/.test(raw)) return raw;
  return null;
}

const roleNames = computed(() => profile.value?.roleNames ?? []);

/** 只读组织信息行 */
const readonlyItems = computed(() => [
  { label: $t('page.autobox.account.userAccount'), value: profile.value?.userAccount || '-' },
  { label: $t('page.autobox.account.deptName'), value: profile.value?.deptName || '-' },
  { label: $t('page.autobox.account.postName'), value: profile.value?.postName || '-' },
  { label: $t('page.autobox.account.roleNames'), value: roleNames.value.length ? roleNames.value.join('、') : '-' },
  { label: $t('page.autobox.account.createTime'), value: profile.value?.createTime || '-' },
  { label: $t('page.autobox.account.lastLoginTime'), value: profile.value?.lastLoginTime || '-' }
]);

function fillProfileModel(detail: Api.SystemManage.UserProfileDetail) {
  profileModel.userName = detail.userName ?? '';
  profileModel.userSex = (detail.userSex as Api.SystemManage.UserSex | null) ?? null;
  profileModel.userEmail = detail.userEmail ?? '';
  profileModel.userPhone = detail.userPhone ?? '';
}

async function loadDetail() {
  loadingDetail.value = true;
  const { data, error } = await fetchGetUserProfileDetail();
  loadingDetail.value = false;
  if (error || !data) return;
  profile.value = data;
  fillProfileModel(data);
  restoreProfileValidation();
  avatarCacheKey.value = Date.now();
  // 顶栏昵称/头像与个人中心保持一致
  authStore.syncProfile({
    userName: data.userName ?? '',
    userAvatar: data.userAvatar
  });
}

type AccountTab = 'profile' | 'password' | 'loginLog';

/** 首页快捷入口等：解析 ?tab=，兼容 string[] */
function resolveTabFromQuery(raw: unknown): AccountTab | null {
  const tab = Array.isArray(raw) ? raw[0] : raw;
  if (tab === 'password' || tab === 'loginLog' || tab === 'profile') return tab;
  return null;
}

async function applyTabFromQuery() {
  // 无有效 ?tab= 时回到资料（keepAlive 下从改密入口再进个人中心）
  const tab = resolveTabFromQuery(route.query.tab) ?? 'profile';
  const changed = activeTab.value !== tab;
  activeTab.value = tab;
  if (tab === 'loginLog' && changed) {
    await getLoginLogsByPage(1);
  }
}

/** 手动切 Tab 回写 URL，刷新后与当前页签一致；资料为默认，去掉 tab */
async function syncTabToQuery(tab: AccountTab) {
  const current = resolveTabFromQuery(route.query.tab);
  if (tab === 'profile') {
    if (current == null) return;
    const query = { ...route.query };
    delete query.tab;
    await router.replace({ query });
    return;
  }
  if (current === tab) return;
  await router.replace({ query: { ...route.query, tab } });
}

async function handleTabUpdate(name: string) {
  const tab = name as AccountTab;
  const changed = activeTab.value !== tab;
  activeTab.value = tab;
  if (tab === 'loginLog' && changed) {
    await getLoginLogsByPage(1);
  }
  await syncTabToQuery(tab);
}

async function handleSaveProfile() {
  await validateProfile();
  if (!profileModel.userSex) return;
  savingProfile.value = true;
  const { error } = await fetchUpdateUserProfile({
    userName: profileModel.userName.trim(),
    userSex: profileModel.userSex,
    userEmail: profileModel.userEmail.trim() || null,
    userPhone: profileModel.userPhone.trim() || null
  });
  savingProfile.value = false;
  if (error) return;
  window.$message?.success($t('common.updateSuccess'));
  await loadDetail();
}

async function handleSavePassword() {
  await validatePwd();
  savingPwd.value = true;
  const { error } = await fetchUpdateOwnPassword({
    oldPassword: pwdModel.oldPassword,
    newPassword: pwdModel.newPassword
  });
  savingPwd.value = false;
  if (error) return;
  window.$message?.success($t('page.autobox.account.pwdChangedRelogin'));
  await authStore.resetStore();
}

/** 打开本地选图（不用 NUpload，避免 trigger 被遮罩挡点击） */
function openAvatarPicker() {
  if (uploadingAvatar.value) return;
  avatarInputRef.value?.click();
}

async function handleAvatarFileChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const raw = input.files?.[0];
  input.value = '';
  if (!raw) return;

  uploadingAvatar.value = true;
  const existingFileId = parseAvatarFileId(profile.value?.userAvatar);

  if (existingFileId) {
    // 已有本系统头像：覆盖同一 fileId，访问地址不变
    const { error } = await fetchOverwriteFile(existingFileId, raw);
    uploadingAvatar.value = false;
    if (error) return;
    window.$message?.success($t('page.autobox.account.avatarUpdated'));
    await loadDetail();
    return;
  }

  // 首次或非本系统地址：新建文件后写回 userAvatar（直链展示用 needLogin=0）
  const { data, error } = await fetchUploadFile(raw, {
    fileScene: 'image',
    needLogin: 0
  });
  if (error || !data?.accessUrl) {
    uploadingAvatar.value = false;
    return;
  }
  const { error: avatarError } = await fetchUpdateOwnAvatar(data.accessUrl);
  uploadingAvatar.value = false;
  if (avatarError) return;
  window.$message?.success($t('page.autobox.account.avatarUpdated'));
  await loadDetail();
}

// keepAlive / 仅 query 变化时也要切 Tab（对齐我的公告 noticeId）
watch(
  () => route.query.tab,
  () => {
    applyTabFromQuery();
  }
);

onMounted(async () => {
  await applyTabFromQuery();
  await loadDetail();
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px">
    <!-- 资料头图：shrink-0 防止 flex 布局把高度压扁；overflow visible 避免头像被裁 -->
    <NCard
      :bordered="false"
      size="small"
      class="profile-hero-card card-wrapper shrink-0"
      content-style="padding: 0; overflow: visible;"
      :loading="loadingDetail"
    >
      <div class="profile-hero px-24px py-24px">
        <div class="relative z-1 flex flex-wrap items-center gap-24px">
          <!-- 头像更换：隐藏 input + 点击头像选图 -->
          <div class="avatar-upload shrink-0 flex-col-center gap-8px">
            <input
              ref="avatarInputRef"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleAvatarFileChange"
            >
            <div
              class="avatar-wrap relative h-96px w-96px cursor-pointer overflow-hidden rounded-full"
              role="button"
              tabindex="0"
              :style="{ opacity: uploadingAvatar ? 0.65 : 1 }"
              @click="openAvatarPicker"
              @keydown.enter.prevent="openAvatarPicker"
            >
              <!-- Naive Avatar：有默认插槽时不会渲染 img，字母回退只能在无 src 时提供 -->
              <NAvatar
                :key="avatarSrc || 'empty'"
                round
                :size="96"
                object-fit="cover"
                :src="avatarSrc || undefined"
                class="h-full w-full text-28px"
              >
                <template v-if="!avatarSrc">
                  {{ (profileModel.userName || profile?.userAccount || '?').slice(0, 1) }}
                </template>
              </NAvatar>
              <div class="avatar-mask absolute inset-0 flex-col-center gap-4px bg-black/45 text-12px text-white">
                <SvgIcon icon="mdi:camera-outline" class="text-22px" />
                <span>
                  {{ uploadingAvatar ? $t('page.autobox.account.avatarUploading') : $t('page.autobox.account.avatarHint') }}
                </span>
              </div>
            </div>
          </div>

          <div class="min-w-0 flex-1 text-white">
            <div class="truncate text-26px font-600 tracking-wide">
              {{ profileModel.userName || profile?.userAccount || '-' }}
            </div>
            <div class="mt-6px flex flex-wrap items-center gap-x-16px gap-y-6px text-13px opacity-90">
              <span class="inline-flex items-center gap-4px">
                <SvgIcon icon="mdi:account-outline" class="text-15px" />
                {{ profile?.userAccount || '-' }}
              </span>
              <span class="inline-flex items-center gap-4px">
                <SvgIcon icon="mdi:office-building-outline" class="text-15px" />
                {{ profile?.deptName || '-' }}
              </span>
              <span class="inline-flex items-center gap-4px">
                <SvgIcon icon="mdi:badge-account-outline" class="text-15px" />
                {{ profile?.postName || '-' }}
              </span>
            </div>
            <div class="mt-14px flex flex-wrap gap-8px">
              <NTag
                v-for="name in roleNames"
                :key="name"
                size="small"
                round
                :bordered="false"
                class="!bg-white/20 !text-white"
              >
                {{ name }}
              </NTag>
              <NTag v-if="!roleNames.length" size="small" round :bordered="false" class="!bg-white/20 !text-white">
                -
              </NTag>
            </div>
          </div>
        </div>
      </div>
    </NCard>

    <!-- 功能区：segment Tab + 分区面板 -->
    <NCard :bordered="false" size="small" class="card-wrapper">
      <!-- 不用 animated：高度过渡会先裁切再撑开，看起来像「先一半后全部」 -->
      <NTabs :value="activeTab" type="segment" size="medium" @update:value="handleTabUpdate">
        <NTabPane name="profile" :tab="$t('page.autobox.account.tabProfile')">
          <!-- 可编辑 / 只读：左右两栏；窄屏再叠成上下 -->
          <div class="grid grid-cols-1 gap-20px pt-16px md:grid-cols-2">
            <div class="section-panel min-w-0">
              <div class="section-title">
                <SvgIcon icon="mdi:account-edit-outline" class="text-18px text-primary" />
                <span>{{ $t('page.autobox.account.sectionEditable') }}</span>
              </div>
              <NForm
                ref="profileFormRef"
                class="mt-4px"
                :model="profileModel"
                :rules="profileRules"
                label-placement="top"
                require-mark-placement="right-hanging"
              >
                <NFormItem :label="$t('page.autobox.account.userName')" path="userName">
                  <NInput
                    v-model:value="profileModel.userName"
                    :placeholder="$t('page.autobox.account.form.userName')"
                  />
                </NFormItem>
                <NFormItem :label="$t('page.manage.user.userSex')" path="userSex">
                  <NRadioGroup v-model:value="profileModel.userSex">
                    <NSpace>
                      <NRadio
                        v-for="item in userSexOptions"
                        :key="item.value"
                        :value="item.value"
                        :label="$t(item.label)"
                      />
                    </NSpace>
                  </NRadioGroup>
                </NFormItem>
                <NFormItem :label="$t('page.autobox.account.userEmail')" path="userEmail">
                  <NInput
                    v-model:value="profileModel.userEmail"
                    :placeholder="$t('page.autobox.account.form.userEmail')"
                  />
                </NFormItem>
                <NFormItem :label="$t('page.autobox.account.userPhone')" path="userPhone">
                  <NInput
                    v-model:value="profileModel.userPhone"
                    :placeholder="$t('page.autobox.account.form.userPhone')"
                  />
                </NFormItem>
                <div class="mt-4px">
                  <NButton type="primary" :loading="savingProfile" @click="handleSaveProfile">
                    {{ $t('common.update') }}
                  </NButton>
                </div>
              </NForm>
            </div>

            <div class="section-panel min-w-0">
              <div class="section-title">
                <SvgIcon icon="mdi:information-outline" class="text-18px text-primary" />
                <span>{{ $t('page.autobox.account.sectionReadonly') }}</span>
              </div>
              <div class="mt-8px flex-col gap-0">
                <div v-for="item in readonlyItems" :key="item.label" class="info-row">
                  <span class="info-label">{{ item.label }}</span>
                  <span class="info-value">{{ item.value }}</span>
                </div>
              </div>
            </div>
          </div>
        </NTabPane>

        <NTabPane name="password" :tab="$t('page.autobox.account.tabPassword')">
          <div class="mx-auto max-w-520px pt-16px">
            <div class="section-panel">
              <div class="section-title">
                <SvgIcon icon="mdi:lock-reset" class="text-18px text-primary" />
                <span>{{ $t('page.autobox.account.tabPassword') }}</span>
              </div>
              <NAlert class="mb-16px mt-8px" type="warning" :bordered="false">
                {{ $t('page.autobox.account.pwdHint') }}
              </NAlert>
              <NForm
                ref="pwdFormRef"
                :model="pwdModel"
                :rules="pwdRules"
                label-placement="top"
                require-mark-placement="right-hanging"
              >
                <NFormItem :label="$t('page.autobox.account.oldPassword')" path="oldPassword">
                  <NInput
                    v-model:value="pwdModel.oldPassword"
                    type="password"
                    show-password-on="click"
                    :placeholder="$t('page.autobox.account.form.oldPassword')"
                  />
                </NFormItem>
                <NFormItem :label="$t('page.autobox.account.newPassword')" path="newPassword">
                  <NInput
                    v-model:value="pwdModel.newPassword"
                    type="password"
                    show-password-on="click"
                    :placeholder="$t('page.autobox.account.form.newPassword')"
                  />
                </NFormItem>
                <NFormItem :label="$t('page.autobox.account.confirmPassword')" path="confirmPassword">
                  <NInput
                    v-model:value="pwdModel.confirmPassword"
                    type="password"
                    show-password-on="click"
                    :placeholder="$t('page.autobox.account.form.confirmPassword')"
                  />
                </NFormItem>
                <NButton type="primary" block :loading="savingPwd" @click="handleSavePassword">
                  {{ $t('page.autobox.account.changePassword') }}
                </NButton>
              </NForm>
            </div>
          </div>
        </NTabPane>

        <NTabPane name="loginLog" :tab="$t('page.autobox.account.tabLoginLog')">
          <div class="section-panel mt-16px">
            <div class="section-title mb-12px">
              <SvgIcon icon="mdi:history" class="text-18px text-primary" />
              <span>{{ $t('page.autobox.account.tabLoginLog') }}</span>
            </div>
            <NDataTable
              size="small"
              :columns="loginLogColumns"
              :data="loginLogs"
              :loading="loadingLogs"
              :bordered="false"
              :single-line="false"
              remote
              paginate-single-page
              :row-key="row => `${row.loginTime}-${row.loginIp}-${row.browser}`"
              :pagination="loginLogPagination"
            />
          </div>
        </NTabPane>
      </NTabs>
    </NCard>
  </div>
</template>

<style scoped>
/* Naive Card 默认可能裁切圆角内容，头图强制可见 */
.profile-hero-card :deep(.n-card__content) {
  overflow: visible;
}

.profile-hero {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background: linear-gradient(
    135deg,
    rgb(var(--primary-color)) 0%,
    color-mix(in srgb, rgb(var(--primary-color)) 72%, #1f2937) 100%
  );
}

.profile-hero::after {
  content: '';
  position: absolute;
  right: -40px;
  top: -60px;
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: rgb(255 255 255 / 12%);
  pointer-events: none;
}

/* 头像点击区固定尺寸 */
.avatar-upload {
  width: 96px;
}

.avatar-wrap {
  box-shadow:
    0 0 0 3px rgb(255 255 255 / 90%),
    0 8px 20px rgb(0 0 0 / 18%);
}

/* 默认几乎透明，悬停才加深，避免遮住已加载头像 */
.avatar-mask {
  opacity: 0;
  transition: opacity 0.2s ease;
  pointer-events: none;
}

.avatar-wrap:hover .avatar-mask {
  opacity: 1;
}

.section-panel {
  padding: 18px 18px 20px;
  border-radius: 12px;
  background: var(--n-color-embedded, rgb(0 0 0 / 3%));
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  font-size: 15px;
  font-weight: 600;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid rgb(0 0 0 / 6%);
}

.info-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.info-label {
  width: 96px;
  flex-shrink: 0;
  color: var(--n-text-color-3, #9ca3af);
  font-size: 13px;
  line-height: 22px;
}

.info-value {
  flex: 1;
  min-width: 0;
  font-size: 13px;
  line-height: 22px;
  word-break: break-all;
}

html.dark .section-panel {
  background: rgb(255 255 255 / 4%);
}

html.dark .info-row {
  border-bottom-color: rgb(255 255 255 / 8%);
}
</style>
