import type { MockDeptNode, MockPost } from '@/mock/autobox/data';

/** autobox isEnabled(0/1) → Soybean EnableStatus('1'/'2') */
export function enabledToStatus(isEnabled: 0 | 1): Api.Common.EnableStatus {
  return isEnabled === 1 ? '1' : '2';
}

/** 树节点或带字符串主键的记录 → 表格数字 id */
export function stableId(key: string): number {
  const n = Number(key);
  if (!Number.isNaN(n)) return n;
  let hash = 0;
  for (let i = 0; i < key.length; i += 1) {
    hash = (hash << 5) - hash + key.charCodeAt(i);
    hash |= 0;
  }
  return Math.abs(hash);
}

/** 静态 Mock 无审计字段，用空串占位以满足 CommonRecord */
const emptyAudit = {
  createBy: '',
  createTime: '',
  updateBy: '',
  updateTime: ''
} as const;

export function mapPost(row: MockPost): Api.AutoboxScaffold.Post {
  return {
    ...emptyAudit,
    id: stableId(row.postId),
    postId: row.postId,
    postName: row.postName,
    postCode: row.postCode,
    postTypeName: row.postTypeName,
    orderNum: row.orderNum,
    remark: row.remark,
    status: enabledToStatus(row.isEnabled)
  };
}

export function mapDeptTree(nodes: MockDeptNode[]): Api.AutoboxScaffold.Dept[] {
  return nodes.map(node => ({
    ...emptyAudit,
    id: stableId(node.deptId),
    deptId: node.deptId,
    deptName: node.deptName,
    deptLeader: node.deptLeader,
    leaderPhone: node.leaderPhone,
    leaderEmail: node.leaderEmail,
    orderNum: node.orderNum,
    status: enabledToStatus(node.isEnabled),
    children: node.children ? mapDeptTree(node.children) : undefined
  }));
}
