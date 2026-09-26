package com.ylmao.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OnlineDto {

    public record OnlineList(
            @Size(max = 64, message = "用户ID参数不合法") String userId
    ) {
    }

    public record OnlineKick(
            @NotBlank(message = "Token不能为空") String tokenValue
    ) {
    }

    public record OnlineKickUser(
            @NotBlank(message = "用户ID不能为空") String userId
    ) {
    }
}
