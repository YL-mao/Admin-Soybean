import type { FlatResponseData } from '@sa/axios';
import {
  mockDeptTree,
  mockFiles,
  mockFilters,
  mockJobs,
  mockNotices,
  mockOnlineUsers,
  mockPosts
} from '@/mock/autobox/data';
import { createStaticListApi, matchLike } from '@/mock/autobox/list-api';
import { enabledToStatus, mapDeptTree, mapPost, stableId } from '@/mock/autobox/mappers';
import { request } from '../request';

type PaginatingRecord<T> = Api.Common.PaginatingQueryRecord<T>;

function statusMatch(rowStatus: Api.Common.EnableStatus | null, queryStatus: unknown) {
  if (queryStatus === null || queryStatus === undefined || queryStatus === '') return true;
  return rowStatus === queryStatus;
}

export function fetchPostList(params?: Api.AutoboxScaffold.PostSearchParams) {
  const api = createStaticListApi<Api.AutoboxScaffold.Post & Record<string, unknown>>(
    () => mockPosts.map(p => mapPost(p)) as (Api.AutoboxScaffold.Post & Record<string, unknown>)[],
    (row, q) =>
      matchLike(row.postName, q.postName) && matchLike(row.postCode, q.postCode) && statusMatch(row.status, q.status)
  );
  return api(params ?? {});
}

export async function fetchDeptTreeList(
  _params?: Api.AutoboxScaffold.DeptSearchParams
): Promise<FlatResponseData<unknown, PaginatingRecord<Api.AutoboxScaffold.Dept>>> {
  await new Promise(resolve => {
    setTimeout(resolve, 120);
  });
  const records = mapDeptTree(mockDeptTree);
  return {
    data: { records, current: 1, size: records.length, total: records.length },
    error: null,
    response: {} as any
  };
}

export function fetchNoticeList(params?: Api.AutoboxScaffold.NoticeSearchParams) {
  const api = createStaticListApi<Api.AutoboxScaffold.Notice & Record<string, unknown>>(
    () =>
      mockNotices.map(n => ({
        id: stableId(n.noticeId),
        noticeId: n.noticeId,
        noticeTitle: n.noticeTitle,
        noticeTypeName: n.noticeTypeName,
        orderNum: n.orderNum,
        isSend: n.isSend,
        sendTime: n.sendTime,
        expireTime: n.expireTime,
        status: n.isSend === 1 ? '1' : '2'
      })) as (Api.AutoboxScaffold.Notice & Record<string, unknown>)[],
    (row, q) => matchLike(row.noticeTitle, q.noticeTitle) && statusMatch(row.status, q.status)
  );
  return api(params ?? {});
}

export function fetchOperateLogList(params?: Api.AutoboxScaffold.OperateLogSearchParams) {
  const { current, size, ...rest } = params || {};
  return request<Api.AutoboxScaffold.OperateLog[]>({
    url: '/operateLog/list',
    method: 'get',
    params: {
      ...rest,
      page: current,
      limit: size
    }
  });
}

/** 日志配置分组（log.loginEn / log.operEn / log.retainDays） */
export function fetchLogConfigGroup() {
  return request<Api.SystemManage.ConfigGroupItem[]>({
    url: '/config/group',
    method: 'get',
    params: { configGroup: 'log' }
  });
}

/** 保存日志配置分组 */
export function fetchUpdateLogConfigGroup(data: {
  configGroup: string;
  configs: Api.SystemManage.ConfigGroupSaveItem[];
}) {
  return request<null>({
    url: '/config/updateGroup',
    method: 'put',
    data
  });
}

/** 按保留天数清理过期日志 */
export function fetchCleanOperateLogByRetention() {
  return request<number>({
    url: '/operateLog/cleanByRetention',
    method: 'delete'
  });
}

export function fetchJobList(params?: Api.AutoboxScaffold.JobSearchParams) {
  const api = createStaticListApi<Api.AutoboxScaffold.Job & Record<string, unknown>>(
    () =>
      mockJobs.map(j => ({
        id: stableId(j.jobId),
        jobId: j.jobId,
        jobName: j.jobName,
        jobCode: j.jobCode,
        jobCronDesc: j.jobCronDesc,
        cronExpression: j.cronExpression,
        jobDesc: j.jobDesc,
        lastRunTime: j.lastRunTime,
        runStatus: j.runStatus,
        nextRunTime: j.nextRunTime,
        status: enabledToStatus(j.isEnabled)
      })) as (Api.AutoboxScaffold.Job & Record<string, unknown>)[],
    (row, q) =>
      matchLike(row.jobName, q.jobName) && matchLike(row.jobCode, q.jobCode) && statusMatch(row.status, q.status)
  );
  return api(params ?? {});
}

const mockJobLogs: Api.AutoboxScaffold.JobLog[] = [
  {
    id: 1,
    createBy: '',
    createTime: '',
    updateBy: '',
    updateTime: '',
    jobLogId: '1',
    jobId: '1',
    runStatus: 'SUCCESS',
    triggerType: 'CRON',
    costMs: 120,
    startTime: '2026-06-20 02:00:00',
    endTime: '2026-06-20 02:00:02',
    message: '执行成功',
    status: '1'
  }
];

export function fetchJobLogList(params?: Api.AutoboxScaffold.JobLogSearchParams) {
  const api = createStaticListApi<Api.AutoboxScaffold.JobLog & Record<string, unknown>>(
    () => mockJobLogs as (Api.AutoboxScaffold.JobLog & Record<string, unknown>)[],
    (row, q) => {
      if (q.jobId && row.jobId !== q.jobId) return false;
      if (q.runStatus && row.runStatus !== q.runStatus) return false;
      return true;
    }
  );
  return api(params ?? {});
}

export function fetchFilterList(params?: Api.AutoboxScaffold.FilterSearchParams) {
  const api = createStaticListApi<Api.AutoboxScaffold.Filter & Record<string, unknown>>(
    () =>
      mockFilters.map(f => ({
        id: stableId(f.filterId),
        filterId: f.filterId,
        policyModeName: f.policyModeName,
        filterTypeName: f.filterTypeName,
        valueLabel: f.valueLabel,
        filterSourceName: f.filterSourceName,
        filterDesc: f.filterDesc,
        expireTime: f.expireTime,
        createTime: f.createTime,
        status: enabledToStatus(f.isEnabled)
      })) as (Api.AutoboxScaffold.Filter & Record<string, unknown>)[],
    (row, q) =>
      matchLike(row.valueLabel, q.valueLabel) &&
      matchLike(row.filterTypeName, q.filterTypeName) &&
      statusMatch(row.status, q.status)
  );
  return api(params ?? {});
}

export function fetchOnlineUserList(params?: Api.AutoboxScaffold.OnlineSearchParams) {
  const api = createStaticListApi<Api.AutoboxScaffold.OnlineUser & Record<string, unknown>>(
    () =>
      mockOnlineUsers.map(u => ({
        id: stableId(u.tokenId),
        tokenId: u.tokenId,
        userAccount: u.userAccount,
        userName: u.userName,
        loginIp: u.loginIp,
        loginTime: u.loginTime,
        browser: u.browser,
        systemOs: u.systemOs,
        timeoutText: u.timeoutText,
        tokenDisplay: u.tokenDisplay,
        self: u.self,
        status: '1'
      })) as (Api.AutoboxScaffold.OnlineUser & Record<string, unknown>)[],
    (row, q) =>
      matchLike(row.userAccount, q.userAccount) &&
      matchLike(row.userName, q.userName) &&
      matchLike(row.loginIp, q.loginIp)
  );
  return api(params ?? {});
}

export function fetchFileList(params?: Api.AutoboxScaffold.FileSearchParams) {
  const api = createStaticListApi<Api.AutoboxScaffold.FileItem & Record<string, unknown>>(
    () =>
      mockFiles.map(f => ({
        id: stableId(f.fileId),
        fileId: f.fileId,
        fileName: f.fileName,
        fileSizeLabel: f.fileSizeLabel,
        sceneName: f.sceneName,
        isImage: f.isImage,
        status: '1',
        createTime: f.createTime
      })) as (Api.AutoboxScaffold.FileItem & Record<string, unknown>)[],
    (row, q) => matchLike(row.fileName, q.fileName) && matchLike(row.sceneName, q.sceneName)
  );
  return api(params ?? {});
}
