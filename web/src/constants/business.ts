import { transformRecordToOption } from '@/utils/common';

export const enableStatusRecord: Record<Api.Common.EnableStatus, App.I18n.I18nKey> = {
  '1': 'page.manage.common.status.enable',
  '2': 'page.manage.common.status.disable'
};

export const enableStatusOptions = transformRecordToOption(enableStatusRecord);

/** 库字段 is_enabled：1 启用 0 停用 */
export const enabledFlagRecord: Record<Api.SystemManage.EnabledFlag, App.I18n.I18nKey> = {
  1: 'page.manage.common.status.enable',
  0: 'page.manage.common.status.disable'
};

// 数值枚举不用 Object.entries，避免 key 被转成字符串
export const enabledFlagOptions: CommonType.Option<Api.SystemManage.EnabledFlag, App.I18n.I18nKey>[] = [
  { value: 1, label: enabledFlagRecord[1] },
  { value: 0, label: enabledFlagRecord[0] }
];

export const userSexRecord: Record<Api.SystemManage.UserSex, App.I18n.I18nKey> = {
  '0': 'page.manage.user.gender.male',
  '1': 'page.manage.user.gender.female'
};

export const userSexOptions: CommonType.Option<Api.SystemManage.UserSex, App.I18n.I18nKey>[] = [
  { value: '0', label: userSexRecord['0'] },
  { value: '1', label: userSexRecord['1'] }
];

/** 锁定：0 正常 1 锁定 */
export const lockFlagRecord: Record<Api.SystemManage.EnabledFlag, App.I18n.I18nKey> = {
  0: 'page.manage.user.lock.normal',
  1: 'page.manage.user.lock.locked'
};

export const lockFlagOptions: CommonType.Option<Api.SystemManage.EnabledFlag, App.I18n.I18nKey>[] = [
  { value: 0, label: lockFlagRecord[0] },
  { value: 1, label: lockFlagRecord[1] }
];

/** 岗位类型：1 管理 2 技术 3 运营 4 市场 */
export const postTypeRecord: Record<Api.SystemManage.PostType, App.I18n.I18nKey> = {
  1: 'page.org.post.postTypeOptions.manage',
  2: 'page.org.post.postTypeOptions.tech',
  3: 'page.org.post.postTypeOptions.ops',
  4: 'page.org.post.postTypeOptions.market'
};

export const postTypeOptions: CommonType.Option<Api.SystemManage.PostType, App.I18n.I18nKey>[] = [
  { value: 1, label: postTypeRecord[1] },
  { value: 2, label: postTypeRecord[2] },
  { value: 3, label: postTypeRecord[3] },
  { value: 4, label: postTypeRecord[4] }
];

export const menuTypeRecord: Record<Api.SystemManage.MenuType, App.I18n.I18nKey> = {
  0: 'page.manage.menu.type.directory',
  1: 'page.manage.menu.type.menu',
  2: 'page.manage.menu.type.button'
};

export const menuTypeOptions: CommonType.Option<Api.SystemManage.MenuType, App.I18n.I18nKey>[] = [
  { value: 0, label: menuTypeRecord[0] },
  { value: 1, label: menuTypeRecord[1] },
  { value: 2, label: menuTypeRecord[2] }
];

export const menuIconTypeRecord: Record<Api.SystemManage.IconType, App.I18n.I18nKey> = {
  1: 'page.manage.menu.iconType.iconify',
  2: 'page.manage.menu.iconType.local'
};

export const menuIconTypeOptions: CommonType.Option<Api.SystemManage.IconType, App.I18n.I18nKey>[] = [
  { value: 1, label: menuIconTypeRecord[1] },
  { value: 2, label: menuIconTypeRecord[2] }
];
