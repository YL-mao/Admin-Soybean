import type { FlatResponseData } from '@sa/axios';

type PaginatingRecord<T> = Api.Common.PaginatingQueryRecord<T>;

/** 本地分页列表，模拟后端 `records/current/size/total` 结构 */
export function createStaticListApi<T extends Record<string, unknown>>(
  getSource: () => T[],
  filter: (row: T, query: Record<string, unknown>) => boolean
) {
  return async (
    query: Record<string, unknown> & { current?: number | null; size?: number | null }
  ): Promise<FlatResponseData<unknown, PaginatingRecord<T>>> => {
    await new Promise(resolve => {
      setTimeout(resolve, 120);
    });

    const current = Number(query.current ?? 1);
    const size = Number(query.size ?? 10);
    const { current: _c, size: _s, ...rest } = query;

    const all = getSource().filter(row => filter(row, rest));
    const start = (current - 1) * size;
    const records = all.slice(start, start + size);

    return {
      data: {
        records,
        current,
        size,
        total: all.length
      },
      error: null,
      response: {} as any
    };
  };
}

/** 字符串字段模糊匹配（空则忽略） */
export function matchLike(value: unknown, keyword: unknown) {
  if (keyword === null || keyword === undefined || keyword === '') return true;
  if (value === null || value === undefined) return false;
  return String(value).toLowerCase().includes(String(keyword).toLowerCase());
}
