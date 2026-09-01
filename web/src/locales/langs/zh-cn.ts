const local: App.I18n.Schema = {
  system: {
    title: 'Soybean 管理系统',
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
    lookForward: '敬请期待',
    modify: '修改',
    modifySuccess: '修改成功',
    noData: '无数据',
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
    logoutMsg: '用户状态失效，请重新登录',
    logoutWithModal: '请求失败后弹出模态框再登出用户',
    logoutWithModalMsg: '用户状态失效，请重新登录',
    refreshToken: '请求的token已过期，刷新token',
    tokenExpired: 'token已过期'
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
    setting_config: '系统配置',
    setting_file: '文件管理',
    ops: '安全运维',
    'ops_operate-log': '行为日志',
    ops_job: '定时任务',
    'ops_job-log': '任务日志',
    ops_filter: '访问控制',
    ops_online: '在线用户',
    dev: '开发工具',
    dev_gen: '代码生成',
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
        menuAuth: '菜单权限',
        buttonAuth: '按钮权限',
        form: {
          roleName: '请输入角色名称',
          roleCode: '请输入角色编码',
          roleStatus: '请选择角色状态',
          roleDesc: '请输入角色描述'
        },
        addRole: '新增角色',
        editRole: '编辑角色'
      },
      user: {
        title: '用户列表',
        userName: '用户名',
        userGender: '性别',
        nickName: '昵称',
        userPhone: '手机号',
        userEmail: '邮箱',
        userStatus: '用户状态',
        userRole: '用户角色',
        form: {
          userName: '请输入用户名',
          userGender: '请选择性别',
          nickName: '请输入昵称',
          userPhone: '请输入手机号',
          userEmail: '请输入邮箱',
          userStatus: '请选择用户状态',
          userRole: '请选择用户角色'
        },
        addUser: '新增用户',
        editUser: '编辑用户',
        gender: {
          male: '男',
          female: '女'
        }
      },
      menu: {
        home: '首页',
        title: '菜单列表',
        id: 'ID',
        parentId: '父级菜单ID',
        menuType: '菜单类型',
        menuName: '菜单名称',
        routeName: '路由名称',
        routePath: '路由路径',
        pathParam: '路径参数',
        layout: '布局',
        page: '页面组件',
        i18nKey: '国际化key',
        icon: '图标',
        localIcon: '本地图标',
        iconTypeTitle: '图标类型',
        order: '排序',
        constant: '常量路由',
        keepAlive: '缓存路由',
        href: '外链',
        hideInMenu: '隐藏菜单',
        activeMenu: '高亮的菜单',
        multiTab: '支持多页签',
        fixedIndexInTab: '固定在页签中的序号',
        query: '路由参数',
        button: '按钮',
        buttonCode: '按钮编码',
        buttonDesc: '按钮描述',
        menuStatus: '菜单状态',
        form: {
          home: '请选择首页',
          menuType: '请选择菜单类型',
          menuName: '请输入菜单名称',
          routeName: '请输入路由名称',
          routePath: '请输入路由路径',
          pathParam: '请输入路径参数',
          page: '请选择页面组件',
          layout: '请选择布局组件',
          i18nKey: '请输入国际化key',
          icon: '请输入图标',
          localIcon: '请选择本地图标',
          order: '请输入排序',
          keepAlive: '请选择是否缓存路由',
          href: '请输入外链',
          hideInMenu: '请选择是否隐藏菜单',
          activeMenu: '请选择高亮的菜单的路由名称',
          multiTab: '请选择是否支持多标签',
          fixedInTab: '请选择是否固定在页签中',
          fixedIndexInTab: '请输入固定在页签中的序号',
          queryKey: '请输入路由参数Key',
          queryValue: '请输入路由参数Value',
          button: '请选择是否按钮',
          buttonCode: '请输入按钮编码',
          buttonDesc: '请输入按钮描述',
          menuStatus: '请选择菜单状态'
        },
        addMenu: '新增菜单',
        editMenu: '编辑菜单',
        addChildMenu: '新增子菜单',
        type: {
          directory: '目录',
          menu: '菜单'
        },
        iconType: {
          iconify: 'iconify图标',
          local: '本地图标'
        }
      }
    },
    autobox: {
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
          tech: '技术岗',
          func: '职能岗'
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
      },
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
        addDictType: '新增字典类型',
        editDictType: '编辑字典类型',
        addDictData: '新增字典数据',
        editDictData: '编辑字典数据',
        form: {
          dictTypeName: '请输入字典名称',
          dictTypeCode: '请输入字典编码',
          dictDataLabel: '请输入数据标签',
          dictDataValue: '请输入数据值',
          status: '请选择状态'
        }
      },
      config: {
        title: '配置列表',
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
        addNotice: '新增公告',
        editNotice: '编辑公告',
        form: {
          noticeTitle: '请输入公告标题',
          status: '请选择状态'
        }
      },
      operateLog: {
        title: '行为日志',
        operateTitle: '操作模块',
        businessType: '业务类型',
        requestMethod: '请求方式',
        requestUri: '接口路径',
        browser: '浏览器',
        systemOs: '操作系统',
        operateIp: '操作地址',
        operateName: '操作人',
        operateTime: '访问时间',
        costTime: '耗时(ms)',
        visitStatus: '访问状态',
        detail: '详情',
        detailTitle: '日志详情',
        logConfig: '日志配置',
        tabOperate: '行为日志',
        tabLogin: '登录日志',
        tabConfig: '配置审计',
        form: {
          operateTitle: '请输入操作模块',
          businessType: '请选择业务类型',
          operateName: '请输入操作人',
          operateIp: '请输入操作IP'
        }
      },
      job: {
        title: '定时任务',
        jobName: '任务名称',
        jobCode: '任务编码',
        jobCronDesc: '执行周期',
        cronExpression: 'Cron 表达式',
        jobDesc: '任务说明',
        lastRunTime: '最近执行',
        runStatus: '执行结果',
        nextRunTime: '下次执行',
        runOnce: '执行一次',
        viewLog: '日志',
        jobConfig: '任务配置',
        addJob: '新增任务',
        editJob: '编辑任务',
        form: {
          jobName: '请输入任务名称',
          jobCode: '请输入任务编码',
          status: '请选择状态'
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
        form: {
          runStatus: '请选择执行结果'
        }
      },
      filter: {
        title: '过滤策略',
        policyMode: '策略模式',
        filterType: '过滤类型',
        valueLabel: '过滤值',
        filterSource: '来源',
        filterDesc: '说明',
        expireTime: '过期时间',
        createTime: '创建时间',
        addFilter: '新增策略',
        editFilter: '编辑策略',
        form: {
          valueLabel: '请输入过滤值',
          status: '请选择状态'
        }
      },
      online: {
        title: '在线用户',
        userAccount: '登录账号',
        userName: '用户名称',
        loginIp: '登录地址',
        loginTime: '登录时间',
        browser: '浏览器',
        systemOs: '操作系统',
        timeoutText: '剩余有效期',
        tokenDisplay: 'Token',
        forceLogout: '强退',
        form: {
          userAccount: '请输入登录账号',
          userName: '请输入用户名称',
          loginIp: '请输入登录地址'
        }
      },
      file: {
        title: '文件列表',
        fileName: '文件名称',
        fileSize: '文件大小',
        sceneName: '业务场景',
        createTime: '上传时间',
        form: {
          fileName: '请输入文件名称',
          sceneName: '请输入业务场景'
        }
      },
      gen: {
        title: '代码生成',
        columnTitle: '字段配置',
        tableName: '数据表',
        moduleName: '模块名',
        businessName: '业务名',
        functionName: '功能名',
        author: '作者',
        parentMenu: '上级菜单',
        generate: '生成代码',
        preview: '预览',
        downloadZip: '下载 ZIP',
        columnName: '列名',
        columnComment: '注释',
        javaType: 'Java类型',
        primaryKey: '主键',
        formField: '表单',
        listDisplay: '展示',
        listQuery: '查询'
      },
      apidoc: {
        title: '接口文档',
        desc: '开发环境可通过 SpringDoc 查看 OpenAPI 文档。',
        openDoc: '打开 Swagger UI'
      },
      account: {
        infoTitle: '个人中心',
        noticeTitle: '我的公告',
        userName: '用户昵称',
        userAccount: '登录账号',
        userEmail: '邮箱',
        userPhone: '手机号',
        deptName: '部门',
        postName: '岗位',
        form: {
          userName: '请输入用户昵称',
          userEmail: '请输入邮箱',
          userPhone: '请输入手机号'
        }
      }
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
      branchDesc:
        '为了方便大家开发和更新合并，我们对main分支的代码进行了精简，只保留了首页菜单，其余内容已移至example分支进行维护。预览地址显示的内容即为example分支的内容。',
      greeting: '早安，{userName}, 今天又是充满活力的一天!',
      weatherDesc: '今日多云转晴，20℃ - 25℃!',
      projectCount: '项目数',
      todo: '待办',
      message: '消息',
      downloadCount: '下载量',
      registerCount: '注册量',
      schedule: '作息安排',
      study: '学习',
      work: '工作',
      rest: '休息',
      entertainment: '娱乐',
      visitCount: '访问量',
      turnover: '成交额',
      dealCount: '成交量',
      projectNews: {
        title: '项目动态',
        moreNews: '更多动态',
        desc1: 'Soybean 在2021年5月28日创建了开源项目 soybean-admin!',
        desc2: 'Yanbowe 向 soybean-admin 提交了一个bug，多标签栏不会自适应。',
        desc3: 'Soybean 准备为 soybean-admin 的发布做充分的准备工作!',
        desc4: 'Soybean 正在忙于为soybean-admin写项目说明文档！',
        desc5: 'Soybean 刚才把工作台页面随便写了一些，凑合能看了！'
      },
      creativity: '创意'
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
