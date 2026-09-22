import type { ProxyOptions } from 'vite';
import { bgRed, bgYellow, green, lightBlue } from 'kolorist';
import { consola } from 'consola';
import { createServiceConfig } from '../../src/utils/service';

/**
 * Set http proxy
 *
 * @param env - The current env
 * @param enable - If enable http proxy
 */
export function createViteProxy(env: Env.ImportMeta, enable: boolean) {
  const isEnableHttpProxy = enable && env.VITE_HTTP_PROXY === 'Y';

  if (!isEnableHttpProxy) return undefined;

  const isEnableProxyLog = env.VITE_PROXY_LOG === 'Y';

  const { baseURL, proxyPattern, other } = createServiceConfig(env);

  const proxy: Record<string, ProxyOptions> = createProxyItem({ baseURL, proxyPattern }, isEnableProxyLog);

  other.forEach(item => {
    Object.assign(proxy, createProxyItem(item, isEnableProxyLog));
  });

  // accessUrl 形如 /upload/{fileId}；开发态在前端域名直接打开时须转发到后端，
  // 否则会进 Vue history 路由被鉴权守卫当成「未登录」。
  Object.assign(proxy, createUploadProxy(baseURL, isEnableProxyLog));

  return proxy;
}

/** 把 /upload/** 原样代理到后端，不经 /proxy-default 前缀 */
function createUploadProxy(target: string, enableLog: boolean) {
  const proxy: Record<string, ProxyOptions> = {
    '/upload': {
      target,
      changeOrigin: true,
      configure: (_proxy, options) => {
        _proxy.on('proxyReq', (_proxyReq, req) => {
          if (!enableLog) return;
          consola.log(
            `${lightBlue('[upload proxy]')}: ${bgYellow(` ${req.method} `)} ${green(`${options.target}${req.url}`)}`
          );
        });
      }
    }
  };
  return proxy;
}

function createProxyItem(item: App.Service.ServiceConfigItem, enableLog: boolean) {
  const proxy: Record<string, ProxyOptions> = {};

  proxy[item.proxyPattern] = {
    target: item.baseURL,
    changeOrigin: true,
    configure: (_proxy, options) => {
      _proxy.on('proxyReq', (_proxyReq, req, _res) => {
        if (!enableLog) return;

        const requestUrl = `${lightBlue('[proxy url]')}: ${bgYellow(` ${req.method} `)} ${green(`${item.proxyPattern}${req.url}`)}`;

        const proxyUrl = `${lightBlue('[real request url]')}: ${green(`${options.target}${req.url}`)}`;

        consola.log(`${requestUrl}\n${proxyUrl}`);
      });
      _proxy.on('error', (_err, req, _res) => {
        if (!enableLog) return;
        consola.log(bgRed(`Error: ${req.method} `), green(`${options.target}${req.url}`));
      });
    },
    rewrite: path => path.replace(new RegExp(`^${item.proxyPattern}`), '')
  };

  return proxy;
}
