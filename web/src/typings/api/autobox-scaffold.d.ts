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
