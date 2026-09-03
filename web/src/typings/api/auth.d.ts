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

    /** getUserInfo：角色码 + 按钮权限码（sys_menu.menu_type=2） */
    interface UserInfo {
      userId: string;
      userName: string;
      roles: string[];
      buttons: string[];
    }
  }
}
