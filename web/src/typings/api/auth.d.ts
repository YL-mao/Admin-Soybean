declare namespace Api {
  /**
   * namespace Auth
   *
   * backend api module: "auth"
   */
  namespace Auth {
    /** 登录响应：token + 最小用户信息；完整 roles/buttons 以 getUserInfo 为准 */
    interface LoginToken {
      token: string;
      userId: string;
      userName: string;
      roles: string[];
    }

    /** getUserInfo：角色码 + 按钮权限码（sys_menu.menu_type=2）+ 头像 */
    interface UserInfo {
      userId: string;
      userName: string;
      /** 头像访问路径，可空；顶栏与个人中心共用 */
      userAvatar: string;
      roles: string[];
      buttons: string[];
    }

    /** 免登录品牌快照：对应 sys_config 的 system.* 展示值 */
    interface Branding {
      name: string;
      shortName: string;
      logo: string;
      favicon: string;
      copyright: string;
      adminMail: string;
      version: string;
      website: string;
      icp: string;
      policeIcp: string;
    }
  }
}
