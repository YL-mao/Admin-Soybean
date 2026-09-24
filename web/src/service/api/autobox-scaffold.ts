import type { FlatResponseData } from '@sa/axios';
import {
  mockDeptTree,
  mockFiles,
  mockNotices,
  mockPosts
} from '@/mock/autobox/data';
import { createStaticListApi, matchLike } from '@/mock/autobox/list-api';
import { mapDeptTree, mapPost, stableId } from '@/mock/autobox/mappers';

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

/** 我的公告仍用本地假数据；管理端列表已改走 /notice/list */
export function fetchNoticeList(params?: {
  current?: number | null;
  size?: number | null;
  noticeTitle?: string | null;
  status?: Api.Common.EnableStatus | null;
  isSend?: number | null;
}) {
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
    (row, q) =>
      matchLike(row.noticeTitle, q.noticeTitle) &&
      (q.isSend === null || q.isSend === undefined || q.isSend === ''
        ? statusMatch(row.status, q.status)
        : Number(row.isSend) === Number(q.isSend))
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
