const local: App.I18n.Schema = {
  system: {
    title: 'Admin-Soybean',
    updateTitle: '系统版本更新通知',
    updateContent: '检测到系统有新版本发布，是否立即刷新页面？',
    updateConfirm: '立即刷新',
    updateCancel: '稍后再说'
  },
  common: {
    action: '操作',
    add: '新增',
    addSuccess: '添加成功',
    backToHome: '返回首页',
    batchDelete: '批量删除',
    cancel: '取消',
    close: '关闭',
    check: '勾选',
    selectAll: '全选',
    expandColumn: '展开列',
    columnSetting: '列设置',
    config: '配置',
    confirm: '确认',
    delete: '删除',
    deleteSuccess: '删除成功',
    confirmDelete: '确认删除吗？',
    edit: '编辑',
    warning: '警告',
    error: '错误',
    index: '序号',
    keywordSearch: '请输入关键词搜索',
    logout: '退出登录',
    logoutConfirm: '确认退出登录吗？',
    logoutSuccess: '已退出登录',
    lookForward: '敬请期待',
    modify: '修改',
    modifySuccess: '修改成功',
    noData: '无数据',
    noPermission: '无权限',
    operate: '操作',
    pleaseCheckValue: '请检查输入的值是否合法',
    refresh: '刷新',
    reset: '重置',
    search: '搜索',
    switch: '切换',
    tip: '提示',
    trigger: '触发',
    update: '更新',
    updateSuccess: '更新成功',
    userCenter: '个人中心',
    yesOrNo: {
      yes: '是',
      no: '否'
    }
  },
  request: {
    logout: '请求失败后登出用户',
    logoutMsg: '登录已失效，请重新登录',
    logoutWithModal: '请求失败后弹出模态框再登出用户',
    logoutWithModalMsg: '登录已失效，请重新登录'
  },
  theme: {
    themeDrawerTitle: '主题配置',
    tabs: {
      appearance: '外观',
      layout: '布局',
      general: '通用',
      preset: '预设'
    },
    appearance: {
      themeSchema: {
        title: '主题模式',
        light: '亮色模式',
        dark: '暗黑模式',
        auto: '跟随系统'
      },
      grayscale: '灰色模式',
      colourWeakness: '色弱模式',
      themeColor: {
        title: '主题颜色',
        primary: '主色',
        info: '信息色',
        success: '成功色',
        warning: '警告色',
        error: '错误色',
        followPrimary: '跟随主色'
      },
      themeRadius: {
        title: '主题圆角'
      },
      recommendColor: '应用推荐算法的颜色',
      recommendColorDesc: '推荐颜色的算法参照',
      preset: {
        title: '主题预设',
        apply: '应用',
        applySuccess: '预设应用成功',
        default: {
          name: '默认预设',
          desc: 'Soybean 默认主题预设'
        },
        dark: {
          name: '暗色预设',
          desc: '适用于夜间使用的暗色主题预设'
        },
        compact: {
          name: '紧凑型',
          desc: '适用于小屏幕的紧凑布局预设'
        },
        azir: {
          name: 'Azir的预设',
          desc: '是 Azir 比较喜欢的莫兰迪色系冷淡风'
        }
      }
    },
    layout: {
      layoutMode: {
        title: '布局模式',
        vertical: '左侧菜单模式',
        'vertical-mix': '左侧菜单混合模式',
        'vertical-hybrid-header-first': '左侧混合-顶部优先',
        horizontal: '顶部菜单模式',
        'top-hybrid-sidebar-first': '顶部混合-侧边优先',
        'top-hybrid-header-first': '顶部混合-顶部优先',
        vertical_detail: '左侧菜单布局，菜单在左，内容在右。',
        'vertical-mix_detail': '左侧双菜单布局，一级菜单在左侧深色区域，二级菜单在左侧浅色区域。',
        'vertical-hybrid-header-first_detail':
          '左侧混合布局，一级菜单在顶部，二级菜单在左侧深色区域，三级菜单在左侧浅色区域。',
        horizontal_detail: '顶部菜单布局，菜单在顶部，内容在下方。',
        'top-hybrid-sidebar-first_detail': '顶部混合布局，一级菜单在左侧，二级菜单在顶部。',
        'top-hybrid-header-first_detail': '顶部混合布局，一级菜单在顶部，二级菜单在左侧。'
      },
      tab: {
        title: '标签栏设置',
        visible: '显示标签栏',
        cache: '标签栏信息缓存',
        cacheTip: '离开页面后仍然保留标签栏信息',
        height: '标签栏高度',
        mode: {
          title: '标签栏风格',
          slider: '滑块风格',
          chrome: '谷歌风格',
          button: '按钮风格'
        },
        closeByMiddleClick: '鼠标中键关闭标签页',
        closeByMiddleClickTip: '启用后可以使用鼠标中键点击标签页进行关闭'
      },
      header: {
        title: '头部设置',
        height: '头部高度',
        breadcrumb: {
          visible: '显示面包屑',
          showIcon: '显示面包屑图标'
        }
      },
      sider: {
        title: '侧边栏设置',
        inverted: '深色侧边栏',
        width: '侧边栏宽度',
        collapsedWidth: '侧边栏折叠宽度',
        mixWidth: '混合布局侧边栏宽度',
        mixCollapsedWidth: '混合布局侧边栏折叠宽度',
        mixChildMenuWidth: '混合布局子菜单宽度',
        autoSelectFirstMenu: '自动选择第一个子菜单',
        autoSelectFirstMenuTip: '点击一级菜单时，自动选择并导航到第一个子菜单的最深层级'
      },
      footer: {
        title: '底部设置',
        visible: '显示底部',
        fixed: '固定底部',
        height: '底部高度',
        right: '底部居右'
      },
      content: {
        title: '内容区域设置',
        scrollMode: {
          title: '滚动模式',
          tip: '主题滚动仅 main 部分滚动，外层滚动可携带头部底部一起滚动',
          wrapper: '外层滚动',
          content: '主体滚动'
        },
        page: {
          animate: '页面切换动画',
          mode: {
            title: '页面切换动画类型',
            'fade-slide': '滑动',
            fade: '淡入淡出',
            'fade-bottom': '底部消退',
            'fade-scale': '缩放消退',
            'zoom-fade': '渐变',
            'zoom-out': '闪现',
            none: '无'
          }
        },
        fixedHeaderAndTab: '固定头部和标签栏'
      }
    },
    general: {
      title: '通用设置',
      watermark: {
        title: '水印设置',
        visible: '显示全屏水印',
        text: '自定义水印文本',
        enableUserName: '启用用户名水印',
        enableTime: '显示当前时间',
        timeFormat: '时间格式'
      },
      multilingual: {
        title: '多语言设置',
        visible: '显示多语言按钮'
      },
      globalSearch: {
        title: '全局搜索设置',
        visible: '显示全局搜索按钮'
      }
    },
    configOperation: {
      copyConfig: '复制配置',
      copySuccessMsg: '复制成功，请替换 src/theme/settings.ts 中的变量 themeSettings',
      resetConfig: '重置配置',
      resetSuccessMsg: '重置成功'
    }
  },
  route: {
    login: '登录',
    403: '无权限',
    404: '页面不存在',
    500: '服务器错误',
    'iframe-page': '外链页面',
    home: '首页',
    manage: '系统管理',
    manage_user: '用户管理',
    manage_role: '角色管理',
    manage_menu: '菜单管理',
    org: '组织权限',
    org_dept: '部门管理',
    org_post: '岗位管理',
    setting: '系统设置',
    setting_dict: '数据字典',
    setting_notice: '公告管理',
    'setting_notice-console': '公告控制台',
    setting_config: '系统信息',
    setting_file: '文件管理',
    ops: '安全运维',
    'ops_operate-log': '行为日志',
    ops_job: '定时任务',
    'ops_job-log': '任务日志',
    ops_filter: '访问控制',
    ops_online: '在线用户',
    dev: '开发工具',
    dev_apidoc: '接口文档',
    account: '个人',
    account_info: '个人中心',
    account_notice: '我的公告'
  },
  page: {
    manage: {
      common: {
        status: {
          enable: '启用',
          disable: '禁用'
        }
      },
      role: {
        title: '角色列表',
        roleName: '角色名称',
        roleCode: '角色编码',
        roleStatus: '角色状态',
        roleDesc: '角色描述',
        orderNum: '排序',
        menuAuth: '菜单权限',
        buttonAuth: '按钮权限',
        expandAll: '全部展开',
        collapseAll: '全部收起',
        checkAll: '全部选中',
        invertAll: '全部反选',
        form: {
          roleName: '请输入角色名称',
          roleCode: '请输入角色编码',
          roleStatus: '请选择角色状态',
          roleDesc: '请输入角色描述',
          orderNum: '请输入排序'
        },
        addRole: '新增角色',
        editRole: '编辑角色'
      },
      user: {
        title: '用户列表',
        userAccount: '登录账号',
        userName: '姓名',
        userGender: '性别',
        userSex: '性别',
        nickName: '昵称',
        userPhone: '手机号',
        userEmail: '邮箱',
        userStatus: '用户状态',
        userLock: '锁定状态',
        userRole: '用户角色',
        deptName: '部门',
        postName: '岗位',
        form: {
          userAccount: '请输入登录账号',
          userName: '请输入姓名',
          userGender: '请选择性别',
          userSex: '请选择性别',
          nickName: '请输入昵称',
          userPhone: '请输入手机号',
          userEmail: '请输入邮箱',
          userStatus: '请选择用户状态',
          userLock: '请选择锁定状态',
          userRole: '请选择用户角色',
          deptId: '请选择部门',
          postId: '请选择岗位',
          userPassword: '请输入新密码',
          confirmPassword: '请再次输入新密码'
        },
        addUser: '新增用户',
        editUser: '编辑用户',
        more: '更多',
        export: '导出',
        exportSuccess: '导出成功',
        resetPwd: '重置密码',
        userPassword: '新密码',
        confirmPassword: '确认密码',
        passwordPolicy: '密码须 8～64 位，且同时包含字母、数字和特殊字符',
        permDetail: '权限详情',
        permTree: '权限树',
        expandAll: '全部展开',
        collapseAll: '全部收起',
        kickUser: '踢下线',
        confirmKickUser: '确认强退账号「{account}」的全部会话吗？',
        gender: {
          male: '男',
          female: '女'
        },
        lock: {
          normal: '正常',
          locked: '锁定'
        }
      },
      menu: {
        home: '首页',
        title: '菜单列表',
        id: 'ID',
        parentId: '上级菜单',
        menuType: '菜单类型',
        menuName: '菜单名称',
        menuDesc: '菜单描述',
        routeName: '路由名称',
        routePath: '路由路径',
        routeComp: '组件',
        pathParam: '路径参数',
        layout: '布局',
        page: '页面组件',
        i18nKey: '国际化key',
        icon: '图标',
        localIcon: '本地图标',
        iconTypeTitle: '图标类型',
        order: '排序',
        keepAlive: '缓存路由',
        href: '外链',
        hideInMenu: '隐藏菜单',
        activeMenu: '高亮的菜单',
        query: '路由参数',
        permCode: '权限标识',
        isBlank: '新窗口',
        menuStatus: '菜单状态',
        form: {
          home: '请选择首页',
          menuType: '请选择菜单类型',
          menuName: '请输入菜单名称',
          menuDesc: '请输入菜单描述',
          routeName: '请输入路由名称',
          routePath: '请输入路由路径',
          routeComp: '请输入组件标识',
          pathParam: '请输入路径参数',
          page: '请选择页面组件',
          layout: '请选择布局组件',
          i18nKey: '请输入国际化key',
          icon: '搜索 MDI 图标，如 home',
          iconInvalid: '请从下拉选择本地 MDI 图标',
          localIcon: '请选择本地图标',
          localIconInvalid: '请从下拉选择本地图标',
          order: '请输入排序',
          keepAlive: '请选择是否缓存路由',
          href: '请输入外链',
          hideInMenu: '请选择是否隐藏菜单',
          activeMenu: '请选择高亮的菜单的路由名称',
          query: '请输入路由 query JSON',
          permCode: '请输入权限标识',
          menuStatus: '请选择菜单状态',
          parentId: '请选择上级菜单'
        },
        addMenu: '新增菜单',
        editMenu: '编辑菜单',
        addChildMenu: '新增子菜单',
        type: {
          directory: '目录',
          menu: '菜单',
          button: '按钮'
        },
        iconType: {
          iconify: 'iconify图标',
          local: '本地图标'
        }
      }
    },
    org: {
      post: {
        title: '岗位列表',
        postName: '岗位名称',
        postCode: '岗位编码',
        postType: '岗位类型',
        orderNum: '排序',
        remark: '备注',
        addPost: '新增岗位',
        editPost: '编辑岗位',
        form: {
          postName: '请输入岗位名称',
          postCode: '请输入岗位编码',
          postType: '请选择岗位类型',
          orderNum: '请输入排序',
          status: '请选择状态',
          remark: '请输入备注'
        },
        postTypeOptions: {
          manage: '管理岗',
          tech: '技术岗',
          ops: '运营岗',
          market: '市场岗'
        }
      },
      dept: {
        title: '部门列表',
        deptName: '部门名称',
        deptLeader: '负责人',
        leaderPhone: '联系电话',
        leaderEmail: '邮箱',
        orderNum: '排序',
        parentDept: '上级部门',
        addDept: '新增部门',
        editDept: '编辑部门',
        addChildDept: '新增子部门',
        form: {
          deptName: '请输入部门名称',
          deptLeader: '请输入负责人',
          leaderPhone: '请输入联系电话',
          leaderEmail: '请输入邮箱',
          orderNum: '请输入排序',
          status: '请选择状态'
        }
      }
    },
    setting: {
      dict: {
        typeTitle: '字典类型',
        dataTitle: '字典数据',
        dictTypeName: '字典名称',
        dictTypeCode: '字典编码',
        dictTypeDesc: '类型说明',
        dictDataLabel: '数据标签',
        dictDataValue: '数据值',
        dictDataDesc: '数据说明',
        isDefault: '默认',
        orderNum: '排序',
        selectType: '选中',
        selectedType: '已选',
        selectTypeFirst: '请先选择字典类型',
        codeExists: '字典编码已存在',
        labelExists: '同一字典类型下数据标签已存在',
        valueExists: '同一字典类型下数据值已存在',
        refreshCache: '刷新缓存',
        refreshSuccess: '缓存已刷新',
        addDictType: '新增字典类型',
        editDictType: '编辑字典类型',
        addDictData: '新增字典数据',
        editDictData: '编辑字典数据',
        form: {
          dictTypeName: '请输入字典名称',
          dictTypeCode: '请输入字典编码',
          dictTypeCodeInvalid: '字典编码只能包含字母、数字、下划线和连字符',
          dictDataLabel: '请输入数据标签',
          dictDataValue: '请输入数据值',
          dictTypeDesc: '请输入类型说明',
          dictDataDesc: '请输入数据说明',
          orderNum: '请输入排序',
          status: '请选择状态'
        }
      },
      config: {
        title: '系统信息',
        configMissing: '未找到该分组配置项',
        sectionBrand: '品牌信息',
        sectionContact: '联系与版本',
        sectionFiling: '备案信息',
        sectionOther: '其他',
        configGroup: '配置分组',
        configName: '配置名称',
        configCode: '配置编码',
        configValue: '配置值',
        valueType: '值类型',
        isBuiltin: '内置',
        configDesc: '配置说明',
        orderNum: '排序',
        editConfig: '编辑配置',
        form: {
          configName: '请输入配置名称',
          configCode: '请输入配置编码',
          configValue: '请输入配置值',
          status: '请选择状态'
        }
      },
      notice: {
        title: '公告列表',
        noticeTitle: '公告标题',
        noticeType: '公告类型',
        sendTime: '发送时间',
        expireTime: '过期时间',
        noticeConfig: '公告配置',
        addNotice: '新增公告',
        editNotice: '编辑公告',
        form: {
          noticeTitle: '请输入公告标题',
          noticeContent: '请输入公告内容',
          noticeType: '请选择公告类型',
          receiverType: '请选择接收范围',
          receiverIds: '请选择接收对象',
          noticeDesc: '请输入说明',
          orderNum: '请输入排序',
          expireTime: '请选择过期时间',
          isSend: '请选择发布状态',
          consoleReadState: '请选择阅读状态'
        },
        receiverType: '接收范围',
        noticeContent: '公告内容',
        noticeDesc: '说明',
        publishStatus: '发布状态',
        draft: '草稿',
        published: '已发布',
        publishedLocked: '已发布公告不能修改',
        detailTitle: '公告详情',
        console: '控制台',
        consoleStatsTab: '阅读统计',
        consoleReceiversTab: '接收人明细',
        consoleTotal: '应读人数',
        consoleReadCount: '已读',
        consoleUnreadCount: '未读',
        consoleReadRate: '阅读率',
        consoleTargets: '接收对象',
        consoleUserAccount: '账号',
        consoleUserName: '姓名',
        consoleDeptName: '部门',
        consoleReadState: '阅读状态',
        consoleReadTime: '阅读时间',
        consoleRead: '已读',
        consoleUnread: '未读',
        consoleDirectVisitTip: '请从公告管理列表进入控制台',
        consoleUnpublishedTip: '仅已发布公告可查看控制台',
        consoleOpenFailedTip: '无法打开控制台，公告不存在或未发布'
      },
      file: {
        title: '文件列表',
        folderTitle: '目录',
        rootFolder: '根目录',
        parentFolder: '上级目录',
        folderName: '目录名称',
        currentFolder: '当前目录',
        fileName: '文件名称',
        fileSize: '文件大小',
        sceneName: '业务场景',
        createTime: '上传时间',
        fileScene: '业务场景',
        needLogin: '需登录访问',
        pickFile: '选择文件',
        uploadFile: '上传文件',
        uploadConfig: '上传配置',
        fileDetail: '文件详情',
        download: '下载',
        overwrite: '覆盖上传',
        addFolder: '新增目录',
        editFolder: '编辑目录',
        orderNum: '排序',
        sceneImage: '图片',
        sceneDocument: '文档',
        sceneExcel: '表格',
        uploadToUnclassified: '根目录上传将保存到「未分类」',
        deleteFolderConfirm: '删除目录将同时软删其子目录与文件，确认继续？',
        deleteReferenced: '文件可能仍被引用，确认删除？',
        fileTooLarge: '文件超过大小限制（{size} MB）',
        uploadHint: '最大 {size} MB，允许后缀：{accept}',
        loadRulesFailed: '加载上传规则失败，请检查上传开关与配置',
        accessUrl: '访问地址',
        previewFailed: '预览失败',
        downloadFailed: '下载失败',
        form: {
          fileName: '请输入文件名称',
          folderName: '请输入目录名称',
          parentFolder: '请选择上级目录',
          pickFile: '请选择要上传的文件'
        }
      }
    },
    ops: {
      operateLog: {
        title: '行为日志',
        operateTitle: '操作模块',
        businessType: '业务类型',
        requestMethod: '请求方式',
        requestUri: '接口路径',
        operateMethod: '调用方法',
        requestParam: '请求参数',
        requestBody: '请求体',
        responseBody: '响应体',
        statusCode: '状态码',
        errorClass: '异常类',
        errorMsg: '错误信息',
        errorStack: '异常堆栈',
        userId: '用户ID',
        browser: '浏览器',
        systemOs: '操作系统',
        operateIp: '操作地址',
        serverIp: '服务端IP',
        userAgent: 'User-Agent',
        traceId: 'TraceId',
        operateName: '操作人',
        operateTime: '访问时间',
        timeRange: '时间范围',
        costTime: '耗时(ms)',
        visitStatus: '访问状态',
        visitSuccess: '成功',
        visitFail: '失败',
        detail: '详情',
        detailTitle: '日志详情',
        logConfig: '日志配置',
        tabOperate: '行为日志',
        tabLogin: '登录日志',
        tabConfig: '配置审计',
        auditAction: '变更动作',
        beforeValue: '变更前',
        afterValue: '变更后',
        beforeEnabled: '变更前启用',
        afterEnabled: '变更后启用',
        retainDays: '保留天数',
        cleanByRetention: '按保留天数清理',
        cleanSuccess: '已删除 {count} 条过期日志',
        configMissing: '未找到日志配置项，请检查系统配置',
        form: {
          operateTitle: '请输入操作模块',
          businessType: '请选择业务类型',
          operateName: '请输入操作人',
          operateIp: '请输入操作IP',
          requestUri: '请输入接口路径',
          isSuccess: '请选择访问状态',
          timeRange: '请选择时间范围'
        }
      },
      job: {
        title: '定时任务',
        jobName: '任务名称',
        jobCode: '任务编码',
        jobCronDesc: '执行周期',
        jobDesc: '任务说明',
        lastRunTime: '最近执行',
        runStatus: '执行结果',
        nextRunTime: '下次执行',
        runOnce: '执行一次',
        runOnceConfirm: '确认立即执行该任务？',
        runOnceSuccess: '已触发执行',
        viewLog: '日志',
        jobConfig: '任务配置',
        form: {
          jobName: '请输入任务名称',
          jobCode: '请输入任务编码',
          isEnabled: '请选择状态'
        }
      },
      jobLog: {
        title: '执行日志',
        jobName: '任务名称',
        jobCode: '任务编码',
        startTime: '开始时间',
        endTime: '结束时间',
        triggerType: '触发方式',
        costMs: '耗时(ms)',
        message: '消息',
        statusSuccess: '成功',
        statusFailed: '失败',
        statusSkipped: '跳过',
        missingJobId: '请从定时任务列表进入执行日志',
        useDrawerHint: '执行日志已改为任务列表内抽屉查看',
        form: {
          runStatus: '请选择执行结果'
        }
      },
      filter: {
        title: '访问控制',
        policyMode: '策略模式',
        filterType: '过滤类型',
        valueLabel: '过滤值',
        filterSource: '来源',
        filterDesc: '说明',
        expireTime: '过期时间',
        permanent: '永久',
        createTime: '创建时间',
        securityConfig: '安全配置',
        addFilter: '新增策略',
        editFilter: '编辑策略',
        typeIp: 'IP',
        typeUser: '用户',
        modeBlack: '黑名单',
        modeWhite: '白名单',
        sourceManual: '人工',
        sourceAuto: '自动',
        form: {
          filterType: '请选择过滤类型',
          filterValue: '请输入 IP 或过滤值',
          filterValueUser: '请输入用户 ID',
          policyMode: '请选择策略',
          filterSource: '请选择来源',
          isEnabled: '请选择状态',
          expireTime: '请选择过期时间',
          expireTimePast: '过期时间须晚于当前'
        }
      },
      online: {
        title: '在线用户',
        user: '用户',
        userAccount: '登录账号',
        userName: '用户名称',
        loginIp: '登录地址',
        loginTime: '登录时间',
        browser: '浏览器',
        systemOs: '操作系统',
        timeoutText: '剩余有效期',
        tokenDisplay: 'Token',
        currentSession: '当前',
        forceLogout: '强退',
        forceLogoutConfirm: '确认强退该会话？',
        forceLogoutSuccess: '已强退该会话',
        fingerprintConfig: '指纹配置',
        form: {
          user: '请输入账号或姓名搜索'
        }
      }
    },
    dev: {
      apidoc: {
        title: '接口文档',
        desc: '开发环境本页嵌入 Swagger UI；OpenAPI 与试调均携带登录 saToken。',
        openDoc: '打开 Swagger UI',
        devOnly: '接口文档仅在开发环境可用；生产环境已关闭 springdoc。'
      }
    },
    account: {
      infoTitle: '个人中心',
      noticeTitle: '我的公告',
      tabProfile: '基本资料',
      tabPassword: '修改密码',
      tabLoginLog: '最近登录',
      userName: '用户昵称',
      userAccount: '登录账号',
      userEmail: '邮箱',
      userPhone: '手机号',
      deptName: '部门',
      postName: '岗位',
      roleNames: '角色',
      createTime: '创建时间',
      lastLoginTime: '最近登录',
      oldPassword: '原密码',
      newPassword: '新密码',
      confirmPassword: '确认密码',
      changePassword: '修改密码',
      pwdChangedRelogin: '密码修改成功，请重新登录',
      avatarHint: '点击更换',
      avatarUploading: '上传中…',
      avatarUpdated: '头像已更新',
      sectionEditable: '可编辑资料',
      sectionReadonly: '组织与账号',
      pwdHint: '密码需 8～64 位，且包含字母、数字和特殊字符；修改成功后需重新登录。',
      loginTime: '登录时间',
      loginIp: '登录 IP',
      browser: '浏览器',
      systemOs: '操作系统',
      currentSession: '当前',
      form: {
        userName: '请输入用户昵称',
        userEmail: '请输入邮箱',
        userPhone: '请输入手机号',
        oldPassword: '请输入原密码',
        newPassword: '请输入新密码',
        confirmPassword: '请再次输入新密码',
        noticeReadState: '请选择阅读状态'
      },
      noticeReadState: '阅读状态',
      noticeUnread: '未读',
      noticeRead: '已读',
      noticeReadTime: '阅读时间',
      noticeView: '查看',
      noticeDetail: '公告详情',
      noticeReadAll: '全部已读',
      noticeReadAllSuccess: '已全部标为已读',
      noticeViewAll: '查看全部',
      noticeEmptyUnread: '暂无未读公告',
      noticeNotFound: '公告不存在或已过期'
    },

    login: {
      common: {
        loginOrRegister: '登录 / 注册',
        userAccountPlaceholder: '请输入账号',
        userNamePlaceholder: '请输入用户名',
        phonePlaceholder: '请输入手机号',
        codePlaceholder: '请输入验证码',
        passwordPlaceholder: '请输入密码',
        confirmPasswordPlaceholder: '请再次输入密码',
        codeLogin: '验证码登录',
        confirm: '确定',
        back: '返回',
        validateSuccess: '验证成功',
        loginSuccess: '登录成功',
        welcomeBack: '欢迎回来，{userName} ！'
      },
      pwdLogin: {
        title: '密码登录',
        rememberMe: '记住我',
        imageCodePlaceholder: '请输入图片验证码',
        imageCodeInvalid: '图片验证码不正确',
        refreshCaptcha: '点击刷新验证码',
        captchaLoadFailed: '验证码加载失败',
        captchaLimited: '验证码请求过于频繁，请稍后再试'
      },
      codeLogin: {
        title: '验证码登录',
        getCode: '获取验证码',
        reGetCode: '{time}秒后重新获取',
        sendCodeSuccess: '验证码发送成功',
        imageCodePlaceholder: '请输入图片验证码'
      },
      register: {
        title: '注册账号',
        agreement: '我已经仔细阅读并接受',
        protocol: '《用户协议》',
        policy: '《隐私权政策》'
      },
      resetPwd: {
        title: '重置密码'
      },
      bindWeChat: {
        title: '绑定微信'
      }
    },
    home: {
      greetingMorning: '上午好，{userName}',
      greetingAfternoon: '下午好，{userName}',
      greetingEvening: '晚上好，{userName}',
      lastLogin: '上次登录 {time}',
      lastLoginEmpty: '暂无上次登录记录',
      unreadNotices: '我的未读公告',
      viewAll: '查看全部',
      noticeNoPerm: '暂无公告收件箱权限',
      shortcuts: '快捷入口',
      shortcutAccount: '个人中心',
      shortcutPassword: '修改密码',
      shortcutNotice: '我的公告',
      shortcutFile: '文件管理',
      shortcutEmpty: '暂无可快捷进入的页面',
      recentLogins: '最近登录',
      loginLogNoPerm: '暂无个人中心权限',
      profileSummary: '账号摘要',
      goProfile: '去个人中心',
      accountStatus: '账号状态'
    }
  },
  form: {
    required: '不能为空',
    userName: {
      required: '请输入用户名',
      invalid: '用户名格式不正确'
    },
    phone: {
      required: '请输入手机号',
      invalid: '手机号格式不正确'
    },
    pwd: {
      required: '请输入密码',
      invalid: '密码格式不正确，6-18位字符，包含字母、数字、下划线'
    },
    confirmPwd: {
      required: '请输入确认密码',
      invalid: '两次输入密码不一致'
    },
    code: {
      required: '请输入验证码',
      invalid: '验证码格式不正确'
    },
    email: {
      required: '请输入邮箱',
      invalid: '邮箱格式不正确'
    }
  },
  dropdown: {
    closeCurrent: '关闭',
    closeOther: '关闭其它',
    closeLeft: '关闭左侧',
    closeRight: '关闭右侧',
    closeAll: '关闭所有',
    pin: '固定标签',
    unpin: '取消固定'
  },
  icon: {
    themeConfig: '主题配置',
    themeSchema: '主题模式',
    lang: '切换语言',
    fullscreen: '全屏',
    fullscreenExit: '退出全屏',
    reload: '刷新页面',
    collapse: '折叠菜单',
    expand: '展开菜单',
    pin: '固定',
    unpin: '取消固定'
  },
  datatable: {
    itemCount: '共 {total} 条',
    fixed: {
      left: '左固定',
      right: '右固定',
      unFixed: '取消固定'
    }
  }
};

export default local;
