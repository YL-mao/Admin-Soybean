import { localStg } from '@/utils/storage';
import type { RequestInstanceState } from './type';

export function getAuthorization() {
  const token = localStg.get('token');

  return token || null;
}

/** Sa-Token 使用 auto-renew 续签，无独立 refresh 接口。 */
export async function handleExpiredRequest(_state: RequestInstanceState) {
  return false;
}

export function showErrorMsg(state: RequestInstanceState, message: string) {
  if (!state.errMsgStack?.length) {
    state.errMsgStack = [];
  }

  const isExist = state.errMsgStack.includes(message);

  if (!isExist) {
    state.errMsgStack.push(message);

    window.$message?.error(message, {
      onLeave: () => {
        state.errMsgStack = state.errMsgStack.filter(msg => msg !== message);

        setTimeout(() => {
          state.errMsgStack = [];
        }, 5000);
      }
    });
  }
}
