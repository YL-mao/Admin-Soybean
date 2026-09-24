package com.ylmao.admin.controller.admin.account;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.UserInfoDto;
import com.ylmao.admin.service.UserInfoService;
import com.ylmao.admin.vo.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "个人中心", description = "当前用户资料与密码")
@RestController
@RequestMapping("/user/info")
@RequiredArgsConstructor
public class UserInfoController extends BaseController {

    private final UserInfoService userInfoService;

    @Operation(summary = "个人资料详情")
    @Log(title = "个人资料详情", businessType = "QUERY")
    @SaCheckPermission("user:info:view")
    @GetMapping("/detail")
    public R<?> userInfoDetail() {
        UserInfoVo.ProfileDetailVo profileDetail = userInfoService.getCurrentProfileDetail();
        return R.ok(profileDetail);
    }

    @Operation(summary = "个人最近登录")
    @Log(title = "个人最近登录", businessType = "QUERY")
    @SaCheckPermission("user:info:view")
    @GetMapping("/loginLog")
    public R<?> userInfoLoginLog() {
        List<UserInfoVo.LoginLogVo> loginLogs = userInfoService.getCurrentLoginLogs();
        return R.ok(loginLogs);
    }

    @Operation(summary = "保存个人资料")
    @Log(title = "保存个人资料", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("user:info:view")
    @PutMapping("/update")
    public R<?> userInfoUpdate(@Valid @RequestBody UserInfoDto.ProfileSave profileSave) {
        // 请求体不含 userId，Service 仅更新当前登录用户允许自助修改的字段。
        userInfoService.updateCurrentProfile(profileSave);
        return success();
    }

    @Operation(summary = "修改个人密码")
    @Log(title = "修改个人密码", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("user:info:view")
    @PatchMapping("/updatePwd")
    public R<?> updateUserInfoPwd(@Valid @RequestBody UserInfoDto.UpdatePwd updatePwd) {
        // 请求体不含 userId，Service 仅修改当前登录用户密码。
        userInfoService.updateCurrentPassword(updatePwd);
        return success("密码修改成功，请重新登录");
    }

    @Operation(summary = "修改个人头像")
    @Log(title = "修改个人头像", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("user:info:view")
    @PatchMapping("/updateAvatar")
    public R<?> updateUserInfoAvatar(@Valid @RequestBody UserInfoDto.UpdateAvatar updateAvatar) {
        userInfoService.updateCurrentAvatar(updateAvatar);
        return success();
    }
}
