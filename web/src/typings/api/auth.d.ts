declare namespace Api {
  /**
   * namespace Auth
   *
   * backend api module: "auth"
   */
  namespace Auth {
    /** 登录响应：token + 最小用户信息（暂不走 getUserInfo） */
    interface LoginToken {
      token: string;
      userId: string;
      userName: string;
      roles: string[];
    }

    interface UserInfo {
      userId: string;
      userName: string;
      roles: string[];
      buttons: string[];
    }
  }
}
