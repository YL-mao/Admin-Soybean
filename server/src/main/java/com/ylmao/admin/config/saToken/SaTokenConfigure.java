package com.ylmao.admin.config.saToken;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.*;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.FingerprintKeys;
import com.ylmao.admin.common.R;
import com.ylmao.admin.service.FingerprintService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SaTokenConfigure.class);

    private static final List<String> corsOriginsDev = List.of(
            "http://localhost:9527");
    /** 生产部署前改为真实域名（含 https://，无路径） */
    private static final List<String> corsOriginsProd = List.of(
            "https://www.example.com",
            "https://example.com");

    private final JsonMapper jsonMapper;
    private final List<String> corsOrigins;
    private final FingerprintService fingerprintService;

    public SaTokenConfigure(JsonMapper jsonMapper, Environment env, FingerprintService fingerprintService) {
        this.jsonMapper = jsonMapper;
        this.corsOrigins = env.acceptsProfiles(Profiles.of("prod")) ? corsOriginsProd : corsOriginsDev;
        this.fingerprintService = fingerprintService;
    }

    /**
     * CORS 跨域策略：由 Sa-Token 内置 CorsFilter 调用，按 Origin 白名单回写响应头。
     */
    @Bean
    public SaCorsHandleFunction corsHandle() {
        return (req, res, sto) -> {
            String origin = req.getHeader("Origin");
            if (StrUtil.isNotBlank(origin) && corsOrigins.contains(origin)) {
                res.setHeader("Access-Control-Allow-Origin", origin)
                        .setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS")
                        .setHeader("Access-Control-Allow-Headers",
                                "x-requested-with,content-type,saToken," + FingerprintKeys.Admin.DEVICE_ID_HEADER)
                        // 验证码 Cookie / withCredentials 跨域需要；Origin 已白名单，不可用 *。
                        .setHeader("Access-Control-Allow-Credentials", "true")
                        .setHeader("Access-Control-Max-Age", String.valueOf(3600))
                        .setHeader("Vary", "Origin");
            }
            // 预检请求直接返回，不进入鉴权
            if (SaHttpMethod.OPTIONS.name().equalsIgnoreCase(req.getMethod())) {
                SaRouter.back();
            }
        };
    }

    // 开放权限的 url（管理端认证与上传白名单）
    private final String[] excludePaths = {
            "/favicon.ico", "/ico/favicon.ico", "/static/**",
            "/api/admin/auth/login",
            "/api/admin/auth/captchaImage",
            // 登录前品牌引导：系统名称 / Logo / 版权等
            "/api/admin/auth/branding",
            // 注销幂等：无会话 / 已失效 / 已冻结也放行，避免再抛 token 文案
            "/api/admin/auth/logout",
            // 文件预览：匿名/登录校验在控制器内按 need_login 判断
            "/upload/**"};

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public SaServletFilter saServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude(excludePaths)
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    // 白名单外全部要登录
                    SaRouter.match("/**", StpUtil::checkLogin);
                    // 仅管理端 API 做指纹比对（其它端自行决定是否调用）
                    SaRouter.match("/api/admin/**", () -> {
                        HttpServletRequest request = (HttpServletRequest) SaHolder.getRequest().getSource();
                        fingerprintService.checkOrKickAsNotLogin(request);
                    });
                })
                // 异常处理函数：统一 JSON
                .setError(e -> {
                    try {
                        R<Void> result = toAuthErrorR(e);
                        SaHolder.getResponse()
                                .setStatus(result.code())
                                .setHeader("Content-Type", "application/json;charset=utf-8");
                        return jsonMapper.writeValueAsString(result);
                    } catch (JacksonException ex) {
                        log.warn("Failed to serialize R", ex);
                        return "{\"code\":500,\"msg\":\"error\"}";
                    }
                })
                .setBeforeAuth(r -> {
                    // 安全响应头（CORS 见 corsHandle Bean）
                    SaHolder.getResponse()
                            // 是否可以在 iframe 显示：DENY=不可以 | SAMEORIGIN=同域下可以
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            // 启用浏览器 XSS 防护并在检测到攻击时停止渲染
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff");
                });
    }

    private R<Void> toAuthErrorR(Throwable e) {
        if (e instanceof NotLoginException) {
            return R.fail(401, SaAuthMessages.NOT_LOGIN);
        }
        if (e instanceof NotPermissionException || e instanceof NotRoleException) {
            return R.fail(403, SaAuthMessages.NO_PERMISSION);
        }
        return R.fail(e.getMessage());
    }

}
