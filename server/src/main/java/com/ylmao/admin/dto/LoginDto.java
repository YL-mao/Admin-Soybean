package com.ylmao.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    public record LoginRequest(
            @NotBlank(message = "账号不能为空") String userAccount,
            @NotBlank(message = "密码不能为空") String userPassword,
            @NotBlank(message = "验证码不能为空") String captcha,
            /** 前端「记住我」仅本地记账号密码，后端忽略，不影响 token 存活。 */
            Boolean rememberMe
    ) {
    }
}
