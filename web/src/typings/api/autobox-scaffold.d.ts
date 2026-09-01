declare namespace Api {
  /** autobox 静态脚手架模块（本地 Mock，字段对齐后台 VO） */
  namespace AutoboxScaffold {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    type Post = Common.CommonRecord<{
      postId: string;
      postName: string;
      postCode: string;
      postTypeName: string;
      orderNum: number;
      remark: string;
    }>;

    type PostSearchParams = CommonType.RecordNullable<
      Pick<Post, 'postName' | 'postCode' | 'status'> & CommonSearchParams
    >;

    type Dept = Common.CommonRecord<{
      deptId: string;
      deptName: string;
      deptLeader: string;
      leaderPhone: string;
      leaderEmail: string;
      orderNum: number;
      children?: Dept[];
    }>;

    type DeptSearchParams = CommonType.RecordNullable<Pick<Dept, 'deptName' | 'status'> & CommonSearchParams>;

    type DictType = Common.CommonRecord<{
      dictTypeId: string;
      dictTypeName: string;
      dictTypeCode: string;
      dictTypeDesc: string;
      orderNum: number;
    }>;

    type DictTypeSearchParams = CommonType.RecordNullable<
      Pick<DictType, 'dictTypeName' | 'dictTypeCode' | 'status'> & CommonSearchParams
    >;

    type DictData = Common.CommonRecord<{
      dictDataId: string;
      dictTypeId: string;
      dictDataLabel: string;
      dictDataValue: string;
      dictDataDesc: string;
      orderNum: number;
      isDefault: CommonType.YesOrNo;
    }>;

    type DictDataSearchParams = CommonType.RecordNullable<
      Pick<DictData, 'dictDataLabel' | 'dictDataValue' | 'status'> & CommonSearchParams & { dictTypeId?: string | null }
    >;

    type ConfigItem = Common.CommonRecord<{
      configId: string;
      configGroup: string;
      configName: string;
      configCode: string;
      configValue: string;
      valueType: string;
      isBuiltin: 0 | 1;
      orderNum: number;
      configDesc: string;
    }>;

    type ConfigSearchParams = CommonType.RecordNullable<
      Pick<ConfigItem, 'configGroup' | 'configName' | 'configCode' | 'status'> & CommonSearchParams
    >;

    type Notice = Common.CommonRecord<{
      noticeId: string;
      noticeTitle: string;
      noticeTypeName: string;
      orderNum: number;
      isSend: 0 | 1;
      sendTime: string;
      expireTime: string;
    }>;

    type NoticeSearchParams = CommonType.RecordNullable<Pick<Notice, 'noticeTitle' | 'status'> & CommonSearchParams>;

    type OperateLog = Common.CommonRecord<{
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
    }>;

    type OperateLogSearchParams = CommonType.RecordNullable<
      Pick<OperateLog, 'operateTitle' | 'businessType' | 'operateName' | 'operateIp'> & CommonSearchParams
    >;

    type Job = Common.CommonRecord<{
      jobId: string;
      jobName: string;
      jobCode: string;
      jobCronDesc: string;
      cronExpression: string;
      jobDesc: string;
      lastRunTime: string;
      runStatus: string;
      nextRunTime: string;
    }>;

    type JobSearchParams = CommonType.RecordNullable<Pick<Job, 'jobName' | 'jobCode' | 'status'> & CommonSearchParams>;

    type JobLog = Common.CommonRecord<{
      jobLogId: string;
      jobId: string;
      runStatus: string;
      triggerType: string;
      costMs: number;
      startTime: string;
      endTime: string;
      message: string;
    }>;

    type JobLogSearchParams = CommonType.RecordNullable<Pick<JobLog, 'runStatus' | 'jobId'> & CommonSearchParams>;

    type Filter = Common.CommonRecord<{
      filterId: string;
      policyModeName: string;
      filterTypeName: string;
      valueLabel: string;
      filterSourceName: string;
      filterDesc: string;
      expireTime: string;
      createTime: string;
    }>;

    type FilterSearchParams = CommonType.RecordNullable<
      Pick<Filter, 'valueLabel' | 'filterTypeName' | 'status'> & CommonSearchParams
    >;

    type OnlineUser = Common.CommonRecord<{
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
    }>;

    type OnlineSearchParams = CommonType.RecordNullable<
      Pick<OnlineUser, 'userAccount' | 'userName' | 'loginIp'> & CommonSearchParams
    >;

    type FileItem = Common.CommonRecord<{
      fileId: string;
      fileName: string;
      fileSizeLabel: string;
      sceneName: string;
      isImage: boolean;
    }>;

    type FileSearchParams = CommonType.RecordNullable<Pick<FileItem, 'fileName' | 'sceneName'> & CommonSearchParams>;
  }
}
