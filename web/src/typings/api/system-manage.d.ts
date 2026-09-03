declare namespace Api {
  /**
   * namespace SystemManage
   *
   * backend api module: "systemManage"
   */
  namespace SystemManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    /** role */
    type Role = Common.CommonRecord<{
      /** role name */
      roleName: string;
      /** role code */
      roleCode: string;
      /** role description */
      roleDesc: string;
    }>;

    /** role search params */
    type RoleSearchParams = CommonType.RecordNullable<
      Pick<Api.SystemManage.Role, 'roleName' | 'roleCode' | 'status'> & CommonSearchParams
    >;

    /** role list */
    type RoleList = Common.PaginatingQueryRecord<Role>;

    /** all role */
    type AllRole = Pick<Role, 'id' | 'roleName' | 'roleCode'>;

    /**
     * user gender
     *
     * - "1": "male"
     * - "2": "female"
     */
    type UserGender = '1' | '2';

    /** user */
    type User = Common.CommonRecord<{
      /** user name */
      userName: string;
      /** user gender */
      userGender: UserGender | null;
      /** user nick name */
      nickName: string;
      /** user phone */
      userPhone: string;
      /** user email */
      userEmail: string;
      /** user role code collection */
      userRoles: string[];
    }>;

    /** user search params */
    type UserSearchParams = CommonType.RecordNullable<
      Pick<Api.SystemManage.User, 'userName' | 'userGender' | 'nickName' | 'userPhone' | 'userEmail' | 'status'> &
        CommonSearchParams
    >;

    /** user list */
    type UserList = Common.PaginatingQueryRecord<User>;

    /**
     * menu type（对齐 sys_menu.menu_type）
     *
     * - 0: 目录
     * - 1: 菜单
     * - 2: 按钮
     */
    type MenuType = 0 | 1 | 2;

    /**
     * icon type（对齐 sys_menu.icon_type）
     *
     * - 1: iconify
     * - 2: 本地 SVG
     */
    type IconType = 1 | 2;

    /** 启停（对齐库 0/1） */
    type EnabledFlag = 0 | 1;

    /** 菜单列表/树节点（字段对齐 MenuListVo） */
    type Menu = {
      menuId: string;
      parentId: string;
      menuPath: string | null;
      menuName: string;
      menuDesc: string | null;
      menuType: MenuType;
      routeName: string | null;
      routePath: string | null;
      routeComp: string | null;
      routeQuery: string | null;
      menuHref: string | null;
      isBlank: EnabledFlag;
      permCode: string | null;
      menuIcon: string | null;
      iconType: IconType | null;
      i18nKey: string | null;
      keepAlive: EnabledFlag;
      isShow: EnabledFlag;
      activeMenu: string | null;
      orderNum: number;
      isEnabled: EnabledFlag;
      createTime: string | null;
      children?: Menu[] | null;
    };

    /** 菜单列表查询参数 */
    type MenuSearchParams = CommonType.RecordNullable<{
      menuName: string;
      permCode: string;
      routePath: string;
    }>;

    /** 新增菜单 */
    type MenuInsert = {
      parentId: string;
      menuName: string;
      menuDesc?: string | null;
      menuType: MenuType;
      routeName?: string | null;
      routePath?: string | null;
      routeComp?: string | null;
      routeQuery?: string | null;
      menuHref?: string | null;
      isBlank?: EnabledFlag | null;
      permCode?: string | null;
      menuIcon?: string | null;
      iconType?: IconType | null;
      i18nKey?: string | null;
      keepAlive?: EnabledFlag | null;
      isShow?: EnabledFlag | null;
      activeMenu?: string | null;
      orderNum: number;
      isEnabled: EnabledFlag;
    };

    /** 修改菜单 */
    type MenuUpdate = MenuInsert & {
      menuId: string;
    };

    /** 上级菜单下拉节点 */
    type MenuParent = {
      menuId: string;
      parentId: string;
      menuName: string;
      menuPath: string | null;
      children?: MenuParent[];
    };

    /** 角色授权树节点（含勾选） */
    type MenuCheck = {
      menuId: string;
      parentId: string;
      menuName: string;
      checkArr: string;
    };

    type MenuTree = {
      id: number;
      label: string;
      pId: number;
      children?: MenuTree[];
    };
  }
}
