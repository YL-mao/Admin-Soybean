/** autobox 后台静态页 Mock（字段语义对齐后端 VO / Layui 列） */

export interface MockPost {
  postId: string;
  postName: string;
  postCode: string;
  postTypeName: string;
  orderNum: number;
  isEnabled: 0 | 1;
  remark: string;
}

export const mockPosts: MockPost[] = [
  {
    postId: '1',
    postName: '开发工程师',
    postCode: 'dev',
    postTypeName: '技术岗',
    orderNum: 1,
    isEnabled: 1,
    remark: '研发岗位'
  },
  {
    postId: '2',
    postName: '运维工程师',
    postCode: 'ops',
    postTypeName: '技术岗',
    orderNum: 2,
    isEnabled: 1,
    remark: ''
  },
  {
    postId: '3',
    postName: '行政专员',
    postCode: 'admin_staff',
    postTypeName: '职能岗',
    orderNum: 3,
    isEnabled: 0,
    remark: '示例停用'
  }
];

export interface MockUser {
  userId: string;
  userName: string;
  userAccount: string;
  userSexName: string;
  userEmail: string;
  userPhone: string;
  deptName: string;
  postName: string;
  roleNames: string;
  online: 0 | 1;
  isEnabled: 0 | 1;
  isLock: 0 | 1;
}

export const mockUsers: MockUser[] = [
  {
    userId: '1',
    userName: '管理员',
    userAccount: 'admin',
    userSexName: '男',
    userEmail: 'admin@example.com',
    userPhone: '13800000000',
    deptName: '总部',
    postName: '开发工程师',
    roleNames: '超级管理员',
    online: 1,
    isEnabled: 1,
    isLock: 0
  },
  {
    userId: '2',
    userName: '演示用户',
    userAccount: 'demo',
    userSexName: '女',
    userEmail: 'demo@example.com',
    userPhone: '13900000000',
    deptName: '研发部',
    postName: '运维工程师',
    roleNames: '普通角色',
    online: 0,
    isEnabled: 1,
    isLock: 0
  }
];

export interface MockRole {
  roleId: string;
  roleName: string;
  roleCode: string;
  orderNum: number;
  isEnabled: 0 | 1;
  remark: string;
}

export const mockRoles: MockRole[] = [
  { roleId: '1', roleName: '超级管理员', roleCode: 'super_admin', orderNum: 1, isEnabled: 1, remark: '' },
  { roleId: '2', roleName: '普通角色', roleCode: 'common', orderNum: 2, isEnabled: 1, remark: '示例' }
];

export interface MockPermNode {
  permId: string;
  permName: string;
  permCode: string;
  permUrl: string;
  permIcon: string;
  permType: 0 | 1 | 2;
  orderNum: number;
  isEnabled: 0 | 1;
  children?: MockPermNode[];
}

export const mockPermTree: MockPermNode[] = [
  {
    permId: 'w1',
    permName: '工作空间',
    permCode: '',
    permUrl: '',
    permIcon: 'mdi:view-dashboard',
    permType: 0,
    orderNum: 1,
    isEnabled: 1,
    children: [
      {
        permId: 'w1-1',
        permName: '后台首页',
        permCode: 'home:view',
        permUrl: '/home/view',
        permIcon: 'mdi:home',
        permType: 1,
        orderNum: 1,
        isEnabled: 1
      }
    ]
  },
  {
    permId: 'o1',
    permName: '组织权限',
    permCode: '',
    permUrl: '',
    permIcon: 'mdi:account-group',
    permType: 0,
    orderNum: 2,
    isEnabled: 1,
    children: [
      {
        permId: 'o1-1',
        permName: '用户管理',
        permCode: 'system:user:view',
        permUrl: '/user/listView',
        permIcon: 'mdi:account',
        permType: 1,
        orderNum: 1,
        isEnabled: 1,
        children: [
          {
            permId: 'o1-1-1',
            permName: '用户添加',
            permCode: 'system:user:insert',
            permUrl: '/user/add',
            permIcon: '',
            permType: 2,
            orderNum: 1,
            isEnabled: 1
          }
        ]
      }
    ]
  }
];

export interface MockDeptNode {
  deptId: string;
  deptName: string;
  deptLeader: string;
  leaderPhone: string;
  leaderEmail: string;
  orderNum: number;
  isEnabled: 0 | 1;
  children?: MockDeptNode[];
}

export const mockDeptTree: MockDeptNode[] = [
  {
    deptId: '1',
    deptName: '总部',
    deptLeader: '张三',
    leaderPhone: '13800000001',
    leaderEmail: 'hq@example.com',
    orderNum: 1,
    isEnabled: 1,
    children: [
      {
        deptId: '1-1',
        deptName: '研发部',
        deptLeader: '李四',
        leaderPhone: '13800000002',
        leaderEmail: 'dev@example.com',
        orderNum: 1,
        isEnabled: 1
      },
      {
        deptId: '1-2',
        deptName: '运维部',
        deptLeader: '王五',
        leaderPhone: '13800000003',
        leaderEmail: 'ops@example.com',
        orderNum: 2,
        isEnabled: 1
      }
    ]
  }
];

export interface MockDictType {
  dictTypeId: string;
  dictTypeName: string;
  dictTypeCode: string;
  dictTypeDesc: string;
  orderNum: number;
  isEnabled: 0 | 1;
}

export interface MockDictData {
  dictDataId: string;
  dictTypeId: string;
  dictDataLabel: string;
  dictDataValue: string;
  dictDataDesc: string;
  orderNum: number;
  isDefault: 0 | 1;
  isEnabled: 0 | 1;
}

export const mockDictTypes: MockDictType[] = [
  {
    dictTypeId: '1',
    dictTypeName: '用户性别',
    dictTypeCode: 'sys_user_sex',
    dictTypeDesc: '用户性别字典',
    orderNum: 1,
    isEnabled: 1
  },
  {
    dictTypeId: '2',
    dictTypeName: '岗位类型',
    dictTypeCode: 'sys_post_type',
    dictTypeDesc: '',
    orderNum: 2,
    isEnabled: 1
  }
];

export const mockDictData: MockDictData[] = [
  {
    dictDataId: 'd1',
    dictTypeId: '1',
    dictDataLabel: '男',
    dictDataValue: '1',
    dictDataDesc: '',
    orderNum: 1,
    isDefault: 1,
    isEnabled: 1
  },
  {
    dictDataId: 'd2',
    dictTypeId: '1',
    dictDataLabel: '女',
    dictDataValue: '2',
    dictDataDesc: '',
    orderNum: 2,
    isDefault: 0,
    isEnabled: 1
  }
];

export interface MockConfigGroup {
  configGroup: string;
  configCount: number;
}

export const mockConfigGroups: MockConfigGroup[] = [
  { configGroup: 'system', configCount: 5 },
  { configGroup: 'security', configCount: 8 }
];

export interface MockConfigItem {
  configId: string;
  configGroup: string;
  configName: string;
  configCode: string;
  configValue: string;
  valueType: string;
  isBuiltin: 0 | 1;
  isEnabled: 0 | 1;
  orderNum: number;
  configDesc: string;
}

export const mockConfigItems: MockConfigItem[] = [
  {
    configId: '1',
    configGroup: 'system',
    configName: '系统名称',
    configCode: 'sys.name',
    configValue: 'AutoBox',
    valueType: 'string',
    isBuiltin: 1,
    isEnabled: 1,
    orderNum: 1,
    configDesc: '后台展示名称'
  },
  {
    configId: '2',
    configGroup: 'security',
    configName: '密码最小长度',
    configCode: 'security.pwd.min',
    configValue: '8',
    valueType: 'number',
    isBuiltin: 0,
    isEnabled: 1,
    orderNum: 2,
    configDesc: ''
  }
];

export interface MockOperateLog {
  logId: string;
  operateTitle: string;
  businessType: string;
  requestMethod: string;
  requestUri: string;
  browser: string;
  systemOs: string;
  operateIp: string;
  operateName: string;
  costTime: number;
  isSuccess: 0 | 1;
  operateTime: string;
}

export const mockOperateLogs: MockOperateLog[] = [
  {
    logId: '1',
    operateTitle: '用户管理页面',
    businessType: 'QUERY',
    requestMethod: 'GET',
    requestUri: '/user/listView',
    browser: 'Chrome',
    systemOs: 'Windows',
    operateIp: '127.0.0.1',
    operateName: 'admin',
    costTime: 5,
    isSuccess: 1,
    operateTime: '2026-06-20 12:00:34'
  }
];

export interface MockLoginLog {
  logId: string;
  operateTitle: string;
  requestMethod: string;
  requestUri: string;
  operateName: string;
  operateIp: string;
  browser: string;
  operateTime: string;
  isSuccess: 0 | 1;
}

export const mockLoginLogs: MockLoginLog[] = [
  {
    logId: 'l1',
    operateTitle: '登录',
    requestMethod: 'POST',
    requestUri: '/login',
    operateName: 'admin',
    operateIp: '127.0.0.1',
    browser: 'Chrome',
    operateTime: '2026-06-20 09:00:00',
    isSuccess: 1
  }
];

export interface MockNotice {
  noticeId: string;
  noticeTitle: string;
  noticeTypeName: string;
  orderNum: number;
  isSend: 0 | 1;
  sendTime: string;
  expireTime: string;
}

export const mockNotices: MockNotice[] = [
  {
    noticeId: '1',
    noticeTitle: '系统维护通知',
    noticeTypeName: '通知',
    orderNum: 1,
    isSend: 1,
    sendTime: '2026-06-20 10:00:00',
    expireTime: '2026-12-31 23:59:59'
  }
];

export interface MockJob {
  jobId: string;
  jobName: string;
  jobCode: string;
  jobCronDesc: string;
  cronExpression: string;
  jobDesc: string;
  isEnabled: 0 | 1;
  lastRunTime: string;
  runStatus: string;
  nextRunTime: string;
}

export const mockJobs: MockJob[] = [
  {
    jobId: '1',
    jobName: '清理临时文件',
    jobCode: 'cleanTemp',
    jobCronDesc: '每天 02:00',
    cronExpression: '0 0 2 * * ?',
    jobDesc: '清理上传临时目录',
    isEnabled: 1,
    lastRunTime: '2026-06-20 02:00:00',
    runStatus: 'SUCCESS',
    nextRunTime: '2026-06-21 02:00:00'
  }
];

export interface MockOnlineUser {
  tokenId: string;
  userAccount: string;
  userName: string;
  loginIp: string;
  loginTime: string;
  browser: string;
  systemOs: string;
  timeoutText: string;
  tokenDisplay: string;
  self: 0 | 1;
}

export const mockOnlineUsers: MockOnlineUser[] = [
  {
    tokenId: '1',
    userAccount: 'admin',
    userName: '管理员',
    loginIp: '127.0.0.1',
    loginTime: '2026-06-20 15:00:00',
    browser: 'Chrome',
    systemOs: 'Windows',
    timeoutText: '25分钟',
    tokenDisplay: 'a1b2…c3d4',
    self: 1
  }
];

export interface MockFileItem {
  fileId: string;
  fileName: string;
  fileSizeLabel: string;
  sceneName: string;
  createTime: string;
  isImage: boolean;
}

export const mockFiles: MockFileItem[] = [
  {
    fileId: '1',
    fileName: 'avatar.png',
    fileSizeLabel: '12 KB',
    sceneName: '头像',
    createTime: '2026-06-20',
    isImage: true
  },
  {
    fileId: '2',
    fileName: 'manual.pdf',
    fileSizeLabel: '1.2 MB',
    sceneName: '文档',
    createTime: '2026-06-19',
    isImage: false
  },
  {
    fileId: '3',
    fileName: 'banner.jpg',
    fileSizeLabel: '256 KB',
    sceneName: '宣传图',
    createTime: '2026-06-18',
    isImage: true
  },
  {
    fileId: '4',
    fileName: 'contract.docx',
    fileSizeLabel: '88 KB',
    sceneName: '合同',
    createTime: '2026-06-17',
    isImage: false
  },
  {
    fileId: '5',
    fileName: 'report.xlsx',
    fileSizeLabel: '64 KB',
    sceneName: '报表',
    createTime: '2026-06-16',
    isImage: false
  },
  {
    fileId: '6',
    fileName: 'logo.svg',
    fileSizeLabel: '8 KB',
    sceneName: '品牌',
    createTime: '2026-06-15',
    isImage: true
  },
  {
    fileId: '7',
    fileName: 'backup.zip',
    fileSizeLabel: '15.6 MB',
    sceneName: '备份',
    createTime: '2026-06-14',
    isImage: false
  },
  {
    fileId: '8',
    fileName: 'screenshot.webp',
    fileSizeLabel: '320 KB',
    sceneName: '截图',
    createTime: '2026-06-13',
    isImage: true
  }
];

export const mockHome = {
  runtime: '12小时30分',
  cpuUsePercent: 18,
  jvmUsePercent: 42,
  jvmUsedMemory: '256 MB',
  jvmTotalMemory: '512 MB',
  memUsePercent: 62,
  memUsed: '9.8 GB',
  memTotal: '16 GB',
  osName: 'Windows 10',
  osIp: '192.168.1.2',
  jdkName: 'OpenJDK',
  jdkVersion: '21.0.2',
  diskUsePercent: 42,
  appStartTime: '2026-06-20 08:00:00',
  threadCount: 48,
  daemonThreadCount: 12,
  peakThreadCount: 52,
  dsActive: 1,
  dsIdle: 4,
  dsTotal: 5,
  dsMaxPoolSize: 10,
  dsWaiting: 0,
  diskInfos: [
    { mount: 'C:', usePercent: 42, used: '120 GB', size: '256 GB' },
    { mount: 'D:', usePercent: 65, used: '320 GB', size: '500 GB' }
  ],
  notices: [{ noticeTitle: '系统维护通知', sendTime: '2026-06-20 10:00' }],
  operLogs: [{ operateName: 'admin', operateTitle: '用户管理页面', operateTime: '2026-06-20 12:00' }]
};
