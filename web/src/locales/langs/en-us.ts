const local: App.I18n.Schema = {
  system: {
    title: 'SoybeanAdmin',
    updateTitle: 'System Version Update Notification',
    updateContent: 'A new version of the system has been detected. Do you want to refresh the page immediately?',
    updateConfirm: 'Refresh immediately',
    updateCancel: 'Later'
  },
  common: {
    action: 'Action',
    add: 'Add',
    addSuccess: 'Add Success',
    backToHome: 'Back to home',
    batchDelete: 'Batch Delete',
    cancel: 'Cancel',
    close: 'Close',
    check: 'Check',
    selectAll: 'Select All',
    expandColumn: 'Expand Column',
    columnSetting: 'Column Setting',
    config: 'Config',
    confirm: 'Confirm',
    delete: 'Delete',
    deleteSuccess: 'Delete Success',
    confirmDelete: 'Are you sure you want to delete?',
    edit: 'Edit',
    warning: 'Warning',
    error: 'Error',
    index: 'Index',
    keywordSearch: 'Please enter keyword',
    logout: 'Logout',
    logoutConfirm: 'Are you sure you want to log out?',
    logoutSuccess: 'Logged out successfully',
    lookForward: 'Coming soon',
    modify: 'Modify',
    modifySuccess: 'Modify Success',
    noData: 'No Data',
    noPermission: 'No Permission',
    operate: 'Operate',
    pleaseCheckValue: 'Please check whether the value is valid',
    refresh: 'Refresh',
    reset: 'Reset',
    search: 'Search',
    switch: 'Switch',
    tip: 'Tip',
    trigger: 'Trigger',
    update: 'Update',
    updateSuccess: 'Update Success',
    userCenter: 'User Center',
    yesOrNo: {
      yes: 'Yes',
      no: 'No'
    }
  },
  request: {
    logout: 'Logout user after request failed',
    logoutMsg: 'Login has expired, please sign in again',
    logoutWithModal: 'Pop up modal after request failed and then log out user',
    logoutWithModalMsg: 'Login has expired, please sign in again',
    refreshToken: 'The requested token has expired, refresh the token',
    tokenExpired: 'The requested token has expired'
  },
  theme: {
    themeDrawerTitle: 'Theme Configuration',
    tabs: {
      appearance: 'Appearance',
      layout: 'Layout',
      general: 'General',
      preset: 'Preset'
    },
    appearance: {
      themeSchema: {
        title: 'Theme Schema',
        light: 'Light',
        dark: 'Dark',
        auto: 'Follow System'
      },
      grayscale: 'Grayscale',
      colourWeakness: 'Colour Weakness',
      themeColor: {
        title: 'Theme Color',
        primary: 'Primary',
        info: 'Info',
        success: 'Success',
        warning: 'Warning',
        error: 'Error',
        followPrimary: 'Follow Primary'
      },
      themeRadius: {
        title: 'Theme Radius'
      },
      recommendColor: 'Apply Recommended Color Algorithm',
      recommendColorDesc: 'The recommended color algorithm refers to',
      preset: {
        title: 'Theme Presets',
        apply: 'Apply',
        applySuccess: 'Preset applied successfully',
        default: {
          name: 'Default Preset',
          desc: 'Default theme preset with balanced settings'
        },
        dark: {
          name: 'Dark Preset',
          desc: 'Dark theme preset for night time usage'
        },
        compact: {
          name: 'Compact Preset',
          desc: 'Compact layout preset for small screens'
        },
        azir: {
          name: "Azir's Preset",
          desc: 'It is a cold and elegant preset that Azir likes'
        }
      }
    },
    layout: {
      layoutMode: {
        title: 'Layout Mode',
        vertical: 'Vertical Mode',
        horizontal: 'Horizontal Mode',
        'vertical-mix': 'Vertical Mix Mode',
        'vertical-hybrid-header-first': 'Left Hybrid Header-First',
        'top-hybrid-sidebar-first': 'Top-Hybrid Sidebar-First',
        'top-hybrid-header-first': 'Top-Hybrid Header-First',
        vertical_detail: 'Vertical menu layout, with the menu on the left and content on the right.',
        'vertical-mix_detail':
          'Vertical mix-menu layout, with the primary menu on the dark left side and the secondary menu on the lighter left side.',
        'vertical-hybrid-header-first_detail':
          'Left hybrid layout, with the primary menu at the top, the secondary menu on the dark left side, and the tertiary menu on the lighter left side.',
        horizontal_detail: 'Horizontal menu layout, with the menu at the top and content below.',
        'top-hybrid-sidebar-first_detail':
          'Top hybrid layout, with the primary menu on the left and the secondary menu at the top.',
        'top-hybrid-header-first_detail':
          'Top hybrid layout, with the primary menu at the top and the secondary menu on the left.'
      },
      tab: {
        title: 'Tab Settings',
        visible: 'Tab Visible',
        cache: 'Tag Bar Info Cache',
        cacheTip: 'Keep the tab bar information after leaving the page',
        height: 'Tab Height',
        mode: {
          title: 'Tab Mode',
          slider: 'Slider',
          chrome: 'Chrome',
          button: 'Button'
        },
        closeByMiddleClick: 'Close Tab by Middle Click',
        closeByMiddleClickTip: 'Enable closing tabs by clicking with the middle mouse button'
      },
      header: {
        title: 'Header Settings',
        height: 'Header Height',
        breadcrumb: {
          visible: 'Breadcrumb Visible',
          showIcon: 'Breadcrumb Icon Visible'
        }
      },
      sider: {
        title: 'Sider Settings',
        inverted: 'Dark Sider',
        width: 'Sider Width',
        collapsedWidth: 'Sider Collapsed Width',
        mixWidth: 'Mix Sider Width',
        mixCollapsedWidth: 'Mix Sider Collapse Width',
        mixChildMenuWidth: 'Mix Child Menu Width',
        autoSelectFirstMenu: 'Auto Select First Submenu',
        autoSelectFirstMenuTip:
          'When a first-level menu is clicked, the first submenu is automatically selected and navigated to the deepest level'
      },
      footer: {
        title: 'Footer Settings',
        visible: 'Footer Visible',
        fixed: 'Fixed Footer',
        height: 'Footer Height',
        right: 'Right Footer'
      },
      content: {
        title: 'Content Area Settings',
        scrollMode: {
          title: 'Scroll Mode',
          tip: 'The theme scroll only scrolls the main part, the outer scroll can carry the header and footer together',
          wrapper: 'Wrapper',
          content: 'Content'
        },
        page: {
          animate: 'Page Animate',
          mode: {
            title: 'Page Animate Mode',
            fade: 'Fade',
            'fade-slide': 'Slide',
            'fade-bottom': 'Fade Zoom',
            'fade-scale': 'Fade Scale',
            'zoom-fade': 'Zoom Fade',
            'zoom-out': 'Zoom Out',
            none: 'None'
          }
        },
        fixedHeaderAndTab: 'Fixed Header And Tab'
      }
    },
    general: {
      title: 'General Settings',
      watermark: {
        title: 'Watermark Settings',
        visible: 'Watermark Full Screen Visible',
        text: 'Custom Watermark Text',
        enableUserName: 'Enable User Name Watermark',
        enableTime: 'Show Current Time',
        timeFormat: 'Time Format'
      },
      multilingual: {
        title: 'Multilingual Settings',
        visible: 'Display multilingual button'
      },
      globalSearch: {
        title: 'Global Search Settings',
        visible: 'Display GlobalSearch button'
      }
    },
    configOperation: {
      copyConfig: 'Copy Config',
      copySuccessMsg: 'Copy Success, Please replace the variable "themeSettings" in "src/theme/settings.ts"',
      resetConfig: 'Reset Config',
      resetSuccessMsg: 'Reset Success'
    }
  },
  route: {
    login: 'Login',
    403: 'No Permission',
    404: 'Page Not Found',
    500: 'Server Error',
    'iframe-page': 'Iframe',
    home: 'Home',
    manage: 'System Manage',
    manage_user: 'User Manage',
    manage_role: 'Role Manage',
    manage_menu: 'Menu Manage',
    org: 'Organization',
    org_dept: 'Department',
    org_post: 'Post',
    setting: 'Settings',
    setting_dict: 'Dictionary',
    setting_notice: 'Notice',
    'setting_notice-console': 'Notice Console',
    setting_config: 'System Info',
    setting_file: 'Files',
    ops: 'Security Ops',
    'ops_operate-log': 'Operation Log',
    ops_job: 'Scheduled Jobs',
    'ops_job-log': 'Job Log',
    ops_filter: 'Access Control',
    ops_online: 'Online Users',
    dev: 'Development',
    dev_apidoc: 'API Docs',
    account: 'Account',
    account_info: 'Profile',
    account_notice: 'My Notices'
  },
  page: {
    manage: {
      common: {
        status: {
          enable: 'Enable',
          disable: 'Disable'
        }
      },
      menu: {
        home: 'Home',
        title: 'Menu List',
        id: 'ID',
        parentId: 'Parent Menu',
        menuType: 'Menu Type',
        menuName: 'Menu Name',
        menuDesc: 'Description',
        routeName: 'Route Name',
        routePath: 'Route Path',
        routeComp: 'Component',
        pathParam: 'Path Param',
        layout: 'Layout',
        page: 'Page',
        i18nKey: 'I18n Key',
        icon: 'Icon',
        localIcon: 'Local Icon',
        iconTypeTitle: 'Icon Type',
        order: 'Order',
        keepAlive: 'Keep Alive',
        href: 'Href',
        hideInMenu: 'Hide In Menu',
        activeMenu: 'Active Menu',
        query: 'Route Query',
        permCode: 'Permission Code',
        isBlank: 'Open In New Window',
        menuStatus: 'Menu Status',
        form: {
          home: 'Please select home',
          menuType: 'Please select menu type',
          menuName: 'Please enter menu name',
          menuDesc: 'Please enter description',
          routeName: 'Please enter route name',
          routePath: 'Please enter route path',
          routeComp: 'Please enter component',
          pathParam: 'Please enter path param',
          page: 'Please select page',
          layout: 'Please select layout',
          i18nKey: 'Please enter i18n key',
          icon: 'Please enter icon',
          localIcon: 'Please select local icon',
          order: 'Please enter order',
          keepAlive: 'Please select keep alive',
          href: 'Please enter href',
          hideInMenu: 'Please select hide in menu',
          activeMenu: 'Please select active menu',
          query: 'Please enter route query JSON',
          permCode: 'Please enter permission code',
          menuStatus: 'Please select menu status',
          parentId: 'Please select parent menu'
        },
        addMenu: 'Add Menu',
        editMenu: 'Edit Menu',
        addChildMenu: 'Add Child Menu',
        type: {
          directory: 'Directory',
          menu: 'Menu',
          button: 'Button'
        },
        iconType: {
          iconify: 'Iconify',
          local: 'Local'
        }
      },
      role: {
        title: 'Role List',
        roleName: 'Role Name',
        roleCode: 'Role Code',
        roleStatus: 'Role Status',
        roleDesc: 'Role Description',
        orderNum: 'Order',
        menuAuth: 'Menu Auth',
        buttonAuth: 'Button Auth',
        expandAll: 'Expand All',
        collapseAll: 'Collapse All',
        checkAll: 'Check All',
        invertAll: 'Invert Selection',
        form: {
          roleName: 'Please enter role name',
          roleCode: 'Please enter role code',
          roleStatus: 'Please select role status',
          roleDesc: 'Please enter role description',
          orderNum: 'Please enter order'
        },
        addRole: 'Add Role',
        editRole: 'Edit Role'
      },
      user: {
        title: 'User List',
        userAccount: 'Account',
        userName: 'Name',
        userGender: 'Gender',
        userSex: 'Gender',
        nickName: 'Nick Name',
        userPhone: 'Phone',
        userEmail: 'Email',
        userStatus: 'User Status',
        userLock: 'Lock Status',
        userRole: 'User Role',
        deptName: 'Department',
        postName: 'Post',
        form: {
          userAccount: 'Please enter account',
          userName: 'Please enter name',
          userGender: 'Please select gender',
          userSex: 'Please select gender',
          nickName: 'Please enter nick name',
          userPhone: 'Please enter phone',
          userEmail: 'Please enter email',
          userStatus: 'Please select user status',
          userLock: 'Please select lock status',
          userRole: 'Please select user role',
          deptId: 'Please select department',
          postId: 'Please select post',
          userPassword: 'Please enter new password',
          confirmPassword: 'Please confirm new password'
        },
        addUser: 'Add User',
        editUser: 'Edit User',
        more: 'More',
        export: 'Export',
        exportSuccess: 'Exported successfully',
        resetPwd: 'Reset Password',
        userPassword: 'New Password',
        confirmPassword: 'Confirm Password',
        passwordPolicy: 'Password must be 8-64 chars and include letters, digits and special characters',
        permDetail: 'Permission Detail',
        permTree: 'Permission Tree',
        expandAll: 'Expand All',
        collapseAll: 'Collapse All',
        kickSessions: 'Force Logout',
        confirmKickSessions: 'Force logout all sessions of account "{account}"?',
        gender: {
          male: 'Male',
          female: 'Female'
        },
        lock: {
          normal: 'Normal',
          locked: 'Locked'
        }
      }
    },
    autobox: {
      post: {
        title: 'Post List',
        postName: 'Post Name',
        postCode: 'Post Code',
        postType: 'Post Type',
        orderNum: 'Order',
        remark: 'Remark',
        addPost: 'Add Post',
        editPost: 'Edit Post',
        form: {
          postName: 'Please enter post name',
          postCode: 'Please enter post code',
          postType: 'Please select post type',
          orderNum: 'Please enter order',
          status: 'Please select status',
          remark: 'Please enter remark'
        },
        postTypeOptions: {
          manage: 'Management',
          tech: 'Technical',
          ops: 'Operations',
          market: 'Marketing'
        }
      },
      dept: {
        title: 'Department List',
        deptName: 'Department Name',
        deptLeader: 'Leader',
        leaderPhone: 'Phone',
        leaderEmail: 'Email',
        orderNum: 'Order',
        parentDept: 'Parent',
        addDept: 'Add Department',
        editDept: 'Edit Department',
        addChildDept: 'Add Child',
        form: {
          deptName: 'Please enter department name',
          deptLeader: 'Please enter leader',
          leaderPhone: 'Please enter phone',
          leaderEmail: 'Please enter email',
          orderNum: 'Please enter order',
          status: 'Please select status'
        }
      },
      dict: {
        typeTitle: 'Dict Types',
        dataTitle: 'Dict Data',
        dictTypeName: 'Dict Name',
        dictTypeCode: 'Dict Code',
        dictTypeDesc: 'Description',
        dictDataLabel: 'Label',
        dictDataValue: 'Value',
        dictDataDesc: 'Description',
        isDefault: 'Default',
        orderNum: 'Order',
        selectType: 'Select',
        selectedType: 'Selected',
        selectTypeFirst: 'Select a dict type first',
        codeExists: 'Dict code already exists',
        labelExists: 'Label already exists in this dict type',
        valueExists: 'Value already exists in this dict type',
        refreshCache: 'Refresh Cache',
        refreshSuccess: 'Cache refreshed',
        addDictType: 'Add Dict Type',
        editDictType: 'Edit Dict Type',
        addDictData: 'Add Dict Data',
        editDictData: 'Edit Dict Data',
        form: {
          dictTypeName: 'Please enter dict name',
          dictTypeCode: 'Please enter dict code',
          dictTypeCodeInvalid: 'Code allows letters, digits, underscore and hyphen only',
          dictDataLabel: 'Please enter label',
          dictDataValue: 'Please enter value',
          dictTypeDesc: 'Please enter description',
          dictDataDesc: 'Please enter description',
          orderNum: 'Please enter order',
          status: 'Please select status'
        }
      },
      config: {
        title: 'System Info',
        configMissing: 'No config items in this group',
        sectionBrand: 'Brand',
        sectionContact: 'Contact & Version',
        sectionFiling: 'ICP Filing',
        sectionOther: 'Other',
        configGroup: 'Group',
        configName: 'Name',
        configCode: 'Code',
        configValue: 'Value',
        valueType: 'Value Type',
        isBuiltin: 'Builtin',
        configDesc: 'Description',
        orderNum: 'Order',
        editConfig: 'Edit Config',
        form: {
          configName: 'Please enter name',
          configCode: 'Please enter code',
          configValue: 'Please enter value',
          status: 'Please select status'
        }
      },
      notice: {
        title: 'Notice List',
        noticeTitle: 'Title',
        noticeType: 'Type',
        sendTime: 'Send Time',
        expireTime: 'Expire Time',
        noticeConfig: 'Notice Config',
        addNotice: 'Add Notice',
        editNotice: 'Edit Notice',
        form: {
          noticeTitle: 'Please enter title',
          noticeContent: 'Please enter content',
          noticeType: 'Please select type',
          receiverType: 'Please select audience',
          receiverIds: 'Please select targets',
          noticeDesc: 'Please enter description',
          orderNum: 'Please enter order',
          expireTime: 'Please select expire time',
          isSend: 'Please select publish status'
        },
        receiverType: 'Audience',
        noticeContent: 'Content',
        noticeDesc: 'Description',
        publishStatus: 'Publish',
        draft: 'Draft',
        published: 'Published',
        publishedLocked: 'Published notices cannot be edited',
        detailTitle: 'Notice Detail'
      },
      operateLog: {
        title: 'Operation Logs',
        operateTitle: 'Module',
        businessType: 'Business Type',
        requestMethod: 'Method',
        requestUri: 'URI',
        operateMethod: 'Handler',
        requestParam: 'Query Params',
        requestBody: 'Request Body',
        responseBody: 'Response Body',
        statusCode: 'Status Code',
        errorClass: 'Error Class',
        errorMsg: 'Error',
        errorStack: 'Stack Trace',
        userId: 'User ID',
        browser: 'Browser',
        systemOs: 'OS',
        operateIp: 'IP',
        serverIp: 'Server IP',
        userAgent: 'User-Agent',
        traceId: 'TraceId',
        operateName: 'Operator',
        operateTime: 'Time',
        timeRange: 'Time Range',
        costTime: 'Cost(ms)',
        visitStatus: 'Status',
        visitSuccess: 'Success',
        visitFail: 'Failed',
        detail: 'Detail',
        detailTitle: 'Log Detail',
        logConfig: 'Log Config',
        tabOperate: 'Operation',
        tabLogin: 'Login',
        tabConfig: 'Config Audit',
        auditAction: 'Action',
        beforeValue: 'Before',
        afterValue: 'After',
        beforeEnabled: 'Before Enabled',
        afterEnabled: 'After Enabled',
        retainDays: 'Retention Days',
        cleanByRetention: 'Clean by Retention',
        cleanSuccess: 'Deleted {count} expired log(s)',
        configMissing: 'Log config items not found',
        form: {
          operateTitle: 'Please enter module',
          businessType: 'Please select type',
          operateName: 'Please enter operator',
          operateIp: 'Please enter IP',
          requestUri: 'Please enter URI',
          isSuccess: 'Please select status',
          timeRange: 'Please select time range'
        }
      },
      job: {
        title: 'Scheduled Jobs',
        jobName: 'Job Name',
        jobCode: 'Job Code',
        jobCronDesc: 'Schedule',
        jobDesc: 'Description',
        lastRunTime: 'Last Run',
        runStatus: 'Result',
        nextRunTime: 'Next Run',
        runOnce: 'Run Once',
        runOnceConfirm: 'Run this job now?',
        runOnceSuccess: 'Job triggered',
        viewLog: 'Logs',
        jobConfig: 'Job Config',
        form: {
          jobName: 'Please enter job name',
          jobCode: 'Please enter job code',
          isEnabled: 'Please select status'
        }
      },
      jobLog: {
        title: 'Job Logs',
        jobName: 'Job Name',
        jobCode: 'Job Code',
        startTime: 'Start',
        endTime: 'End',
        triggerType: 'Trigger',
        costMs: 'Cost(ms)',
        message: 'Message',
        statusSuccess: 'Success',
        statusFailed: 'Failed',
        statusSkipped: 'Skipped',
        missingJobId: 'Open job logs from the scheduled jobs list',
        useDrawerHint: 'Job logs are now shown in a drawer on the jobs page',
        form: {
          runStatus: 'Please select result'
        }
      },
      filter: {
        title: 'Access Control',
        policyMode: 'Policy',
        filterType: 'Type',
        valueLabel: 'Value',
        filterSource: 'Source',
        filterDesc: 'Description',
        expireTime: 'Expire',
        permanent: 'Permanent',
        createTime: 'Created',
        securityConfig: 'Security Config',
        addFilter: 'Add Filter',
        editFilter: 'Edit Filter',
        typeIp: 'IP',
        typeUser: 'User',
        modeBlack: 'Blacklist',
        modeWhite: 'Whitelist',
        sourceManual: 'Manual',
        sourceAuto: 'Auto',
        form: {
          filterType: 'Please select type',
          filterValue: 'Please enter IP or value',
          filterValueUser: 'Please enter user ID',
          policyMode: 'Please select policy',
          filterSource: 'Please select source',
          isEnabled: 'Please select status',
          expireTime: 'Please select expire time',
          expireTimePast: 'Expire time must be in the future'
        }
      },
      online: {
        title: 'Online Users',
        accountOrName: 'Account / Name',
        userAccount: 'Account',
        userName: 'Name',
        loginIp: 'IP',
        loginTime: 'Login Time',
        browser: 'Browser',
        systemOs: 'OS',
        timeoutText: 'Timeout',
        tokenDisplay: 'Token',
        currentSession: 'Current',
        forceLogout: 'Force Logout',
        forceLogoutConfirm: 'Force logout this session?',
        forceLogoutSuccess: 'Session logged out',
        form: {
          accountOrName: 'Please enter account or name',
          loginIp: 'Please enter IP'
        }
      },
      file: {
        title: 'Files',
        folderTitle: 'Folders',
        rootFolder: 'Root',
        parentFolder: 'Parent Folder',
        folderName: 'Folder Name',
        currentFolder: 'Current Folder',
        fileName: 'File Name',
        fileSize: 'Size',
        sceneName: 'Scene',
        createTime: 'Uploaded',
        fileScene: 'Scene',
        needLogin: 'Require Login',
        pickFile: 'Choose File',
        uploadFile: 'Upload',
        uploadConfig: 'Upload Config',
        fileDetail: 'File Detail',
        download: 'Download',
        overwrite: 'Overwrite',
        addFolder: 'Add Folder',
        editFolder: 'Edit Folder',
        orderNum: 'Order',
        sceneImage: 'Image',
        sceneDocument: 'Document',
        sceneExcel: 'Spreadsheet',
        uploadToUnclassified: 'Uploads under Root go to Unclassified',
        overwriteConfirm: 'A file with the same name exists. Overwrite?',
        deleteFolderConfirm: 'Deleting a folder soft-deletes its subfolders and files. Continue?',
        deleteReferenced: 'This file may still be referenced. Delete anyway?',
        fileTooLarge: 'File exceeds the size limit ({size} MB)',
        uploadHint: 'Max {size} MB. Allowed: {accept}',
        loadRulesFailed: 'Failed to load upload rules. Check upload switch and config.',
        accessUrl: 'Access URL',
        previewFailed: 'Preview failed',
        downloadFailed: 'Download failed',
        form: {
          fileName: 'Please enter file name',
          folderName: 'Please enter folder name',
          parentFolder: 'Please select parent folder',
          pickFile: 'Please choose a file'
        }
      },
      apidoc: {
        title: 'API Docs',
        desc: 'Swagger UI is embedded here in development; OpenAPI fetch and Try it out send saToken.',
        openDoc: 'Open Swagger UI',
        group: 'Doc group',
        devOnly: 'API docs are available in development only; springdoc is disabled in production.'
      },

      account: {
        infoTitle: 'Profile',
        noticeTitle: 'My Notices',
        userName: 'Nickname',
        userAccount: 'Account',
        userEmail: 'Email',
        userPhone: 'Phone',
        deptName: 'Department',
        postName: 'Post',
        form: {
          userName: 'Please enter nickname',
          userEmail: 'Please enter email',
          userPhone: 'Please enter phone'
        }
      }
    },
    login: {
      common: {
        loginOrRegister: 'Login / Register',
        userAccountPlaceholder: 'Please enter account',
        userNamePlaceholder: 'Please enter user name',
        phonePlaceholder: 'Please enter phone number',
        codePlaceholder: 'Please enter verification code',
        passwordPlaceholder: 'Please enter password',
        confirmPasswordPlaceholder: 'Please enter password again',
        codeLogin: 'Verification code login',
        confirm: 'Confirm',
        back: 'Back',
        validateSuccess: 'Verification passed',
        loginSuccess: 'Login successfully',
        welcomeBack: 'Welcome back, {userName} !'
      },
      pwdLogin: {
        title: 'Password Login',
        rememberMe: 'Remember me',
        imageCodePlaceholder: 'Please enter image verification code',
        imageCodeInvalid: 'Incorrect image verification code',
        refreshCaptcha: 'Click to refresh captcha',
        captchaLoadFailed: 'Failed to load captcha',
        captchaLimited: 'Too many captcha requests, please try again later'
      },
      codeLogin: {
        title: 'Verification Code Login',
        getCode: 'Get verification code',
        reGetCode: 'Reacquire after {time}s',
        sendCodeSuccess: 'Verification code sent successfully',
        imageCodePlaceholder: 'Please enter image verification code'
      },
      register: {
        title: 'Register',
        agreement: 'I have read and agree to',
        protocol: '《User Agreement》',
        policy: '《Privacy Policy》'
      },
      resetPwd: {
        title: 'Reset Password'
      },
      bindWeChat: {
        title: 'Bind WeChat'
      }
    },
    home: {
      branchDesc:
        'For the convenience of everyone in developing and updating the merge, we have streamlined the code of the main branch, only retaining the homepage menu, and the rest of the content has been moved to the example branch for maintenance. The preview address displays the content of the example branch.',
      greeting: 'Good morning, {userName}, today is another day full of vitality!',
      weatherDesc: 'Today is cloudy to clear, 20℃ - 25℃!',
      projectCount: 'Project Count',
      todo: 'Todo',
      message: 'Message',
      downloadCount: 'Download Count',
      registerCount: 'Register Count',
      schedule: 'Work and rest Schedule',
      study: 'Study',
      work: 'Work',
      rest: 'Rest',
      entertainment: 'Entertainment',
      visitCount: 'Visit Count',
      turnover: 'Turnover',
      dealCount: 'Deal Count',
      projectNews: {
        title: 'Project News',
        moreNews: 'More News',
        desc1: 'Soybean created the open source project soybean-admin on May 28, 2021!',
        desc2: 'Yanbowe submitted a bug to soybean-admin, the multi-tab bar will not adapt.',
        desc3: 'Soybean is ready to do sufficient preparation for the release of soybean-admin!',
        desc4: 'Soybean is busy writing project documentation for soybean-admin!',
        desc5: 'Soybean just wrote some of the workbench pages casually, and it was enough to see!'
      },
      creativity: 'Creativity'
    }
  },
  form: {
    required: 'Cannot be empty',
    userName: {
      required: 'Please enter user name',
      invalid: 'User name format is incorrect'
    },
    phone: {
      required: 'Please enter phone number',
      invalid: 'Phone number format is incorrect'
    },
    pwd: {
      required: 'Please enter password',
      invalid: '6-18 characters, including letters, numbers, and underscores'
    },
    confirmPwd: {
      required: 'Please enter password again',
      invalid: 'The two passwords are inconsistent'
    },
    code: {
      required: 'Please enter verification code',
      invalid: 'Verification code format is incorrect'
    },
    email: {
      required: 'Please enter email',
      invalid: 'Email format is incorrect'
    }
  },
  dropdown: {
    closeCurrent: 'Close Current',
    closeOther: 'Close Other',
    closeLeft: 'Close Left',
    closeRight: 'Close Right',
    closeAll: 'Close All',
    pin: 'Pin Tab',
    unpin: 'Unpin Tab'
  },
  icon: {
    themeConfig: 'Theme Configuration',
    themeSchema: 'Theme Schema',
    lang: 'Switch Language',
    fullscreen: 'Fullscreen',
    fullscreenExit: 'Exit Fullscreen',
    reload: 'Reload Page',
    collapse: 'Collapse Menu',
    expand: 'Expand Menu',
    pin: 'Pin',
    unpin: 'Unpin'
  },
  datatable: {
    itemCount: 'Total {total} items',
    fixed: {
      left: 'Left Fixed',
      right: 'Right Fixed',
      unFixed: 'Unfixed'
    }
  }
};

export default local;
