declare namespace Api {
  /**
   * namespace SystemManage
   *
   * backend api module: "systemManage"
   */
  namespace SystemManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    /** 角色（对齐 RoleListVo / sys_role） */
    type Role = {
      roleId: string;
      roleName: string;
      roleCode: string;
      orderNum: number;
      isEnabled: EnabledFlag;
    };

    /** 角色搜索（后端目前按 roleName 模糊；页码在请求里映射为 page/limit） */
    type RoleSearchParams = CommonType.RecordNullable<Pick<Role, 'roleName'> & CommonSearchParams>;

    /** 角色分页列表（前端表格用；实际接口 data 为数组 + 顶层 count） */
    type RoleList = Common.PaginatingQueryRecord<Role>;

    /** 新增角色 */
    type RoleInsert = {
      roleName: string;
      roleCode: string;
      orderNum: number;
      isEnabled: EnabledFlag;
    };

    /** 修改角色 */
    type RoleUpdate = RoleInsert & {
      roleId: string;
    };

    /** 下拉用角色（用户分配） */
    type RoleOption = {
      roleId: string;
      roleName: string;
    };

    /**
     * 用户性别（对齐 sys_user.user_sex / 字典 sys_user_sex）
     *
     * - "0": 男
     * - "1": 女
     */
    type UserSex = '0' | '1';

    /** 用户（对齐 UserListVo / sys_user） */
    type User = {
      userId: string;
      userAccount: string;
      userName: string;
      userSex: UserSex | null;
      userSexName: string | null;
      userEmail: string | null;
      userPhone: string | null;
      deptId: string | null;
      deptName: string | null;
      postId: string | null;
      postName: string | null;
      roleIds: string | null;
      roleNames: string | null;
      isEnabled: EnabledFlag;
      isLock: EnabledFlag;
      online: EnabledFlag;
    };

    /** 用户搜索（后端支持 userAccount / isEnabled / isLock） */
    type UserSearchParams = CommonType.RecordNullable<
      Pick<User, 'userAccount' | 'isEnabled' | 'isLock'> & CommonSearchParams
    >;

    /** 用户分页列表（前端表格用；实际接口 data 为数组 + 顶层 count） */
    type UserList = Common.PaginatingQueryRecord<User>;

    /** 新增用户（无密码、无启停；后端默认禁用） */
    type UserInsert = {
      userAccount: string;
      userName: string;
      userSex?: string | null;
      userEmail?: string | null;
      userPhone?: string | null;
      deptId?: string | null;
      postId?: string | null;
      roleIds?: string | null;
    };

    /** 修改用户 */
    type UserUpdate = UserInsert & {
      userId: string;
    };

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

    /**
     * 岗位类型（对齐 sys_post.post_type / 字典 sys_post_type）
     *
     * - 1: 管理岗
     * - 2: 技术岗
     * - 3: 运营岗
     * - 4: 市场岗
     */
    type PostType = 1 | 2 | 3 | 4;

    /** 岗位（对齐 PostListVo / sys_post） */
    type Post = {
      postId: string;
      postCode: string;
      postName: string;
      postType: PostType;
      postTypeName: string | null;
      orderNum: number;
      isEnabled: EnabledFlag;
      createTime: string | null;
    };

    /** 岗位搜索（后端按 postCode / postName） */
    type PostSearchParams = CommonType.RecordNullable<Pick<Post, 'postCode' | 'postName'> & CommonSearchParams>;

    type PostInsert = {
      postCode: string;
      postName: string;
      postType: PostType;
      orderNum: number;
      isEnabled: EnabledFlag;
    };

    type PostUpdate = PostInsert & {
      postId: string;
    };

    type PostOption = {
      postId: string;
      postName: string;
    };

    /** 部门（对齐 DeptListVo / sys_dept） */
    type Dept = {
      deptId: string;
      parentId: string;
      deptPath: string | null;
      deptName: string;
      orderNum: number;
      deptLeader: string | null;
      leaderPhone: string | null;
      leaderEmail: string | null;
      isEnabled: EnabledFlag;
      createTime: string | null;
      children?: Dept[] | null;
    };

    /** 部门列表查询（平铺，前端组树） */
    type DeptSearchParams = CommonType.RecordNullable<{
      deptName: string;
      parentId: string;
    }>;

    type DeptInsert = {
      parentId?: string | null;
      deptName: string;
      orderNum: number;
      deptLeader?: string | null;
      leaderPhone?: string | null;
      leaderEmail?: string | null;
      isEnabled: EnabledFlag;
    };

    type DeptUpdate = DeptInsert & {
      deptId: string;
    };

    /** 部门下拉/上级树节点 */
    type DeptOption = {
      deptId: string;
      parentId: string;
      deptName: string;
      children?: DeptOption[];
    };
  }
}
