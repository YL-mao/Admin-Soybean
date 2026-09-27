import { computed, reactive } from 'vue';
import { defineStore } from 'pinia';
import { fetchGetBranding } from '@/service/api';
import { SetupStoreId } from '@/enum';
import { $t } from '@/locales';
import { resolveBackendAssetUrl } from '@/utils/service';

function emptyBranding(): Api.Auth.Branding {
  return {
    name: '',
    shortName: '',
    logo: '',
    favicon: '',
    copyright: '',
    adminMail: '',
    version: '',
    website: '',
    icp: '',
    policeIcp: ''
  };
}

/** 规范化官网链接：无协议时补 https:// */
function normalizeWebsite(raw: string) {
  const value = raw.trim();
  if (!value) return '';
  if (/^(https?:)?\/\//i.test(value) || value.startsWith('mailto:')) return value;
  return `https://${value}`;
}

export const useBrandingStore = defineStore(SetupStoreId.Branding, () => {
  const branding = reactive<Api.Auth.Branding>(emptyBranding());

  /** 展开标题：系统名称优先 */
  const displayTitle = computed(() => branding.name.trim() || branding.shortName.trim() || $t('system.title'));

  /** 窄空间标题：简称优先 */
  const displayShortTitle = computed(() => branding.shortName.trim() || branding.name.trim() || $t('system.title'));

  const logoUrl = computed(() => resolveBackendAssetUrl(branding.logo));
  const faviconUrl = computed(() => resolveBackendAssetUrl(branding.favicon));
  const websiteHref = computed(() => normalizeWebsite(branding.website));
  const mailHref = computed(() => {
    const mail = branding.adminMail.trim();
    return mail ? `mailto:${mail}` : '';
  });

  function applyFavicon() {
    const href = faviconUrl.value;
    if (!href || typeof document === 'undefined') return;
    let link = document.querySelector<HTMLLinkElement>("link[rel*='icon']");
    if (!link) {
      link = document.createElement('link');
      link.rel = 'icon';
      document.head.appendChild(link);
    }
    link.href = href;
  }

  function applyBranding(data: Api.Auth.Branding) {
    Object.assign(branding, {
      name: data.name ?? '',
      shortName: data.shortName ?? '',
      logo: data.logo ?? '',
      favicon: data.favicon ?? '',
      copyright: data.copyright ?? '',
      adminMail: data.adminMail ?? '',
      version: data.version ?? '',
      website: data.website ?? '',
      icp: data.icp ?? '',
      policeIcp: data.policeIcp ?? ''
    });
    applyFavicon();
  }

  /** 拉取免登录品牌快照；失败时保留上次或空值，不打断启动 */
  async function fetchBranding() {
    const { data, error } = await fetchGetBranding();
    if (error || !data) return false;
    applyBranding(data);
    return true;
  }

  return {
    branding,
    displayTitle,
    displayShortTitle,
    logoUrl,
    faviconUrl,
    websiteHref,
    mailHref,
    fetchBranding,
    applyFavicon
  };
});
