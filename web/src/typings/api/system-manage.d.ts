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

    /** 管理员重置密码（对齐 UserDto.UpdatePwd） */
    type UserUpdatePwd = {
      userId: string;
      userPassword: string;
    };

    /** 用户权限详情（对齐 UserVo.UserPermDetailVo） */
    type UserPermDetail = {
      userId: string;
      userName: string;
      userAccount: string;
      roles: Array<{
        roleId: string;
        roleName: string;
      }>;
      perms: Array<{
        permId: string;
        parentId: string;
        permName: string;
      }>;
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

    /** 字典类型（对齐 DictTypeListVo） */
    type DictType = {
      dictTypeId: string;
      dictTypeName: string;
      dictTypeCode: string;
      orderNum: number;
      isEnabled: EnabledFlag;
      dictTypeDesc: string | null;
      createTime: string | null;
    };

    /** 字典类型搜索（后端只按名称模糊） */
    type DictTypeSearchParams = CommonType.RecordNullable<Pick<DictType, 'dictTypeName'> & CommonSearchParams>;

    type DictTypeInsert = {
      dictTypeName: string;
      dictTypeCode: string;
      orderNum: number;
      isEnabled: EnabledFlag;
      dictTypeDesc?: string | null;
    };

    type DictTypeUpdate = DictTypeInsert & {
      dictTypeId: string;
    };

    /** 字典数据默认项（库字段 is_default：0 否 / 1 是） */
    type DictDefaultFlag = '0' | '1';

    /** 字典数据（对齐 DictDataListVo） */
    type DictData = {
      dictDataId: string;
      dictTypeCode: string;
      dictDataLabel: string;
      dictDataValue: string;
      orderNum: number;
      isDefault: DictDefaultFlag;
      isEnabled: EnabledFlag;
      dictDataDesc: string | null;
      createTime: string | null;
    };

    /** 字典数据搜索（必须带当前字典编码；标签模糊） */
    type DictDataSearchParams = CommonType.RecordNullable<
      Pick<DictData, 'dictTypeCode' | 'dictDataLabel'> & CommonSearchParams
    >;

    type DictDataInsert = {
      dictTypeCode: string;
      dictDataLabel: string;
      dictDataValue: string;
      orderNum: number;
      isEnabled: EnabledFlag;
      dictDataDesc?: string | null;
    };

    type DictDataUpdate = DictDataInsert & {
      dictDataId: string;
    };

    /** 配置分组明细项（对齐 ConfigListVo，供 /config/group） */
    type ConfigGroupItem = {
      configId: string;
      configName: string;
      configCode: string;
      configValue: string | null;
      configGroup: string;
      valueType: string;
      isBuiltin: number;
      isEnabled: EnabledFlag;
      orderNum: number;
      configDesc: string | null;
      createTime: string | null;
    };

    /** 分组保存单项 */
    type ConfigGroupSaveItem = {
      configId: string;
      configCode?: string;
      configValue?: string | null;
      isEnabled?: EnabledFlag;
    };

    /** 公告列表（对齐 NoticeListVo） */
    type Notice = {
      noticeId: string;
      noticeTitle: string;
      noticeContent: string;
      noticeType: number;
      noticeTypeName: string;
      receiverType: number;
      receiverTypeName: string;
      receiverIds: string | null;
      noticeDesc: string | null;
      isSend: 0 | 1;
      isSendName: string;
      orderNum: number;
      sendTime: string | null;
      expireTime: string | null;
      createBy: string | null;
      createTime: string | null;
      delivered: boolean;
    };

    type NoticeSearchParams = CommonType.RecordNullable<
      {
        noticeTitle: string;
        noticeType: number;
        isSend: 0 | 1;
      } & CommonSearchParams
    >;

    type NoticeInsert = {
      noticeTitle: string;
      noticeContent: string;
      noticeType: number;
      receiverType: number;
      receiverIds?: string | null;
      noticeDesc?: string | null;
      isSend: 0 | 1;
      orderNum: number;
      expireTime?: string | null;
    };

    type NoticeUpdate = NoticeInsert & {
      noticeId: string;
    };

    type DictOption = {
      dictDataLabel: string;
      dictDataValue: string;
      isDefault: string;
      orderNum: number;
    };

    type UserOption = {
      userId: string;
      userAccount: string;
      userName: string;
      label: string;
    };

    /** 虚拟目录（对齐 FolderOptionVo） */
    type FolderOption = {
      folderId: string;
      parentId: string;
      folderName: string;
      isBuiltin: 0 | 1;
      orderNum: number;
      children?: FolderOption[];
    };

    type FolderInsert = {
      parentId?: string | null;
      folderName: string;
      orderNum: number;
    };

    type FolderUpdate = FolderInsert & {
      folderId: string;
    };

    /** 文件列表项（对齐 FileListVo） */
    type FileResource = {
      fileId: string;
      folderId: string;
      originalName: string;
      storageType: string;
      fileSuffix: string;
      contentType: string | null;
      fileSize: number;
      fileScene: string;
      needLogin: 0 | 1;
      accessUrl: string;
      createTime: string | null;
    };

    type FileSearchParams = CommonType.RecordNullable<
      {
        folderId: string;
        originalName: string;
      } & CommonSearchParams
    >;

    type FileUploadParams = {
      folderId?: string | null;
      fileScene: string;
      needLogin?: 0 | 1 | null;
      forceOverwrite?: boolean | null;
    };

    type FileUpdate = {
      fileId: string;
      folderId: string;
      originalName: string;
      needLogin: 0 | 1;
    };

    type FileUploadRules = {
      maxFileSizeMb: number;
      imageExtensions: string[];
      documentExtensions: string[];
      excelExtensions: string[];
    };

    type FileCheckRefResult = {
      referenced: boolean;
      message: string;
    };

    /** 对齐后端 OperateLogVo.OperateLogListVo */
    type OperateLog = {
      operateId: string;
      loggingType: string;
      businessType: string;
      operateTitle: string;
      requestMethod: string;
      operateMethod: string | null;
      requestUri: string;
      requestParam: string | null;
      requestBody: string | null;
      responseBody: string | null;
      isSuccess: 0 | 1;
      statusCode: number | null;
      errorClass: string | null;
      errorMsg: string | null;
      errorStack: string | null;
      userId: string | null;
      operateName: string | null;
      operateIp: string | null;
      serverIp: string | null;
      userAgent: string | null;
      browser: string | null;
      systemOs: string | null;
      traceId: string | null;
      costTime: number | null;
      operateTime: string;
    };

    type OperateLogSearchParams = CommonType.RecordNullable<
      Pick<OperateLog, 'operateTitle' | 'businessType' | 'operateName' | 'operateIp' | 'loggingType'> &
        CommonSearchParams & {
          operateTitleExact?: boolean;
          isSuccess?: 0 | 1;
          startTime?: string;
          endTime?: string;
          requestUri?: string;
        }
    >;

    /** 配置变更审计写入 requestBody 的 JSON 结构 */
    type ConfigAuditItem = {
      action: string;
      configCode: string;
      configName: string;
      isBuiltin: number | null;
      beforeValue: string | null;
      afterValue: string | null;
      beforeEnabled: number | null;
      afterEnabled: number | null;
    };

    /** 对齐后端 FilterVo.FilterListVo */
    type Filter = {
      filterId: string;
      filterType: string;
      filterTypeName: string;
      filterValue: string;
      valueLabel: string;
      filterSource: string;
      filterSourceName: string;
      policyMode: string;
      policyModeName: string;
      filterDesc: string | null;
      expireTime: string;
      permanent: EnabledFlag;
      isEnabled: EnabledFlag;
      createTime: string | null;
    };

    type FilterSearchParams = CommonType.RecordNullable<
      {
        filterType: string;
        filterValue: string;
        filterSource: string;
        policyMode: string;
        isEnabled: EnabledFlag;
      } & CommonSearchParams
    >;

    type FilterInsert = {
      filterType: string;
      filterValue: string;
      filterDesc?: string | null;
      policyMode: string;
      expireTime: string;
      isEnabled: EnabledFlag;
    };

    type FilterUpdate = {
      filterId: string;
      filterType: string;
      filterValue: string;
      filterDesc?: string | null;
      policyMode: string;
      expireTime: string;
      isEnabled: EnabledFlag;
    };
  }
}
