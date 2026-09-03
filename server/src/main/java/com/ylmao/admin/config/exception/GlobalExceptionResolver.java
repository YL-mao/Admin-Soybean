package com.ylmao.admin.config.exception;

import cn.dev33.satoken.exception.*;
import com.ylmao.admin.common.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理：一律返回 {@link R} JSON（前后端分离，不再跳转页面）。
 * <p>
 * Ajax 错误约定：HTTP 状态码与 {@code R.code} 一致；400 参数校验，500 业务/系统异常（系统异常固定 {@link #SYSTEM_BUSY_MSG}）。
 * {@link #handleRuntimeException} 与 {@link #handleException} 分别兜底运行时异常与受检异常，响应逻辑需保持一致。
 */
@RestControllerAdvice
public class GlobalExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    private static final String SYSTEM_BUSY_MSG = "系统繁忙，请稍后重试";

    /** Sa-Token 鉴权异常：未登录 401，无权限 403，其它框架内部异常 500。 */
    @ExceptionHandler(SaTokenException.class)
    public ResponseEntity<R<Void>> handleAuthorizationException(HttpServletRequest request, SaTokenException e) {
        if (e instanceof NotLoginException) {
            logger.warn("登录校验异常: {}", e.getMessage());
            return ajaxError(401, e.getMessage());
        }
        if (e instanceof NotPermissionException || e instanceof NotRoleException || e instanceof NotSafeException) {
            logger.warn("权限校验异常: {}", e.getMessage());
            return ajaxError(403, e.getMessage());
        }
        // 序列化等框架内部异常只记日志，不向用户暴露技术细节
        logger.error("Sa-Token异常:", e);
        return ajaxError(500, SYSTEM_BUSY_MSG);
    }

    /** 静态资源 404；{@code .map} 为 DevTools 自动请求，静默返回不记日志。 */
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(NoResourceFoundException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path != null && path.endsWith(".map")) {
            return ResponseEntity.notFound().build();
        }
        logger.debug("资源不存在: {}", path);
        return ajaxError(404, "资源不存在");
    }

    /** Controller 路由不存在，返回 404 而非 500。 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<R<Void>> handleNoHandlerFound(NoHandlerFoundException e, HttpServletRequest request) {
        logger.debug("路由不存在: {}", request.getRequestURI());
        return ajaxError(404, "请求地址不存在");
    }

    /** {@code @Valid @RequestBody} 校验失败，HTTP 400。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        logger.warn("参数校验异常: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ajaxError(400, message);
    }

    /** 表单/Query 参数 {@code @Valid} 校验失败，HTTP 400。 */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<R<Void>> validatedBindException(BindException e) {
        logger.warn("参数校验异常: {}", e.getMessage());
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ajaxError(400, message);
    }

    /** HTTP 方法不匹配，HTTP 405。 */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<R<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        logger.warn("请求方式不支持: {}", e.getMethod());
        return ajaxError(405, "不支持「" + e.getMethod() + "」请求");
    }

    /** Service 业务规则失败，HTTP 500，{@code msg} 为业务文案。 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<R<Void>> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        return ajaxError(500, e.getMessage());
    }

    /** 容器层文件过大（尚未进入业务校验）。 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<R<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.warn("上传文件过大: {}", e.getMessage());
        return ajaxError(500, "文件大小超出限制");
    }

    /** 运行时异常兜底，不向用户暴露内部信息。 */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<R<Void>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("系统运行时异常", e);
        return ajaxError(500, SYSTEM_BUSY_MSG);
    }

    /** 受检异常兜底，不向用户暴露内部信息。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Void>> handleException(Exception e, HttpServletRequest request) {
        logger.error("系统异常", e);
        return ajaxError(500, SYSTEM_BUSY_MSG);
    }

    private ResponseEntity<R<Void>> ajaxError(int code, String message) {
        return ResponseEntity.status(code).body(R.fail(code, message));
    }
}
