export interface RequestInstanceState {
  /** 业务错误提示去重栈 */
  errMsgStack: string[];
  /** 兼容 createFlatRequest 的 state 约束 */
  [key: string]: unknown;
}
