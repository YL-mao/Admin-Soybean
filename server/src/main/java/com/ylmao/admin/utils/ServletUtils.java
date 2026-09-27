package com.ylmao.admin.utils;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.convert.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端工具类
 * 2018年9月30日 下午2:10:48
 */
public class ServletUtils
{
    private static final Logger log = LoggerFactory.getLogger(ServletUtils.class);

    /** 可信反代对端；由 {@code app.client-ip.trusted-proxies} 注入。 */
    private static final Set<String> TRUSTED_PROXIES = ConcurrentHashMap.newKeySet();

    static {
        TRUSTED_PROXIES.add("127.0.0.1");
        TRUSTED_PROXIES.add("::1");
        TRUSTED_PROXIES.add("0:0:0:0:0:0:0:1");
    }

    /** 启动时刷新可信反代列表（含回环与 IPv4 映射形式）。 */
    public static void setTrustedProxies(Iterable<String> proxies) {
        TRUSTED_PROXIES.clear();
        TRUSTED_PROXIES.add("127.0.0.1");
        TRUSTED_PROXIES.add("::1");
        TRUSTED_PROXIES.add("0:0:0:0:0:0:0:1");
        if (proxies == null) {
            return;
        }
        for (String proxy : proxies) {
            if (StrUtil.isBlank(proxy)) {
                continue;
            }
            String normalized = normalizeRemoteAddr(proxy.trim());
            TRUSTED_PROXIES.add(normalized);
            if (normalized.startsWith("::ffff:")) {
                TRUSTED_PROXIES.add(normalized.substring("::ffff:".length()));
            }
        }
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name)
    {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue)
    {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name)
    {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue)
    {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        return getRequestAttributes().getResponse();
    }

    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     * 
     * @param response 渲染对象
     * @param string 待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string)
    {
        try
        {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            log.error("响应 JSON 写入失败", e);
        }
        return null;
    }

    /**
     * 是否是Ajax异步请求
     *
     */
    public static boolean isAjaxRequest(HttpServletRequest request)
    {

        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("application/json"))
        {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest"))
        {
            return true;
        }

        String uri = request.getRequestURI();
        if (StrUtil.equalsAnyIgnoreCase(uri, ".json", ".xml"))
        {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        return StrUtil.equalsAnyIgnoreCase(ajax, "json", "xml");
    }


	private static boolean checkIp(String ip) {
        return !SaFoxUtil.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip);
    }

	/** 是否为单个合法 IPv4/IPv6（拒绝逗号列表、空格串、垃圾头）。 */
	private static boolean isSingleValidIp(String ip) {
		if (SaFoxUtil.isEmpty(ip)) {
			return false;
		}
		String trimmed = ip.trim();
		if (trimmed.isEmpty() || trimmed.indexOf(',') >= 0 || trimmed.indexOf(' ') >= 0) {
			return false;
		}
		try {
			InetAddress.getByName(trimmed);
			return true;
		} catch (UnknownHostException ex) {
			return false;
		}
	}

	/** 规范化对端地址（小写；剥掉 IPv4 映射前缀便于匹配）。 */
	private static String normalizeRemoteAddr(String remoteAddr) {
		if (remoteAddr == null) {
			return "";
		}
		String addr = remoteAddr.trim().toLowerCase(Locale.ROOT);
		if (addr.startsWith("::ffff:")) {
			return addr.substring("::ffff:".length());
		}
		return addr;
	}

	/** 对端是否在可信反代列表（含回环与 ::ffff:127.0.0.1）。 */
	private static boolean isTrustedProxy(String remoteAddr) {
		String normalized = normalizeRemoteAddr(remoteAddr);
		return TRUSTED_PROXIES.contains(normalized)
				|| TRUSTED_PROXIES.contains(remoteAddr == null ? "" : remoteAddr.trim());
	}
    
	/**
	 * 返回请求端 IP。
	 * 仅当对端属于 app.client-ip.trusted-proxies 时采信 X-Real-IP，防直连伪造。
	 */
	public static String getIP(HttpServletRequest request) {
		String remote = request.getRemoteAddr();
		String ip = null;
		if (isTrustedProxy(remote)) {
			ip = request.getHeader("X-Real-IP");
		}
		// 头非法（多 IP / 垃圾串）时回退 remoteAddr，避免污染限流键。
		if (!checkIp(ip) || !isSingleValidIp(ip)) {
			ip = remote;
		}
		if (ip != null) {
			ip = ip.trim();
		}
		String normalized = normalizeRemoteAddr(ip);
		return normalized.isEmpty() ? ip : (
				"0:0:0:0:0:0:0:1".equals(ip) || "::1".equalsIgnoreCase(ip) ? "127.0.0.1" : normalized);
	}

}
