package com.ylmao.admin.controller.admin.manage;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.config.saToken.StpInterfaceImpl;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.UserDto;
import com.ylmao.admin.service.UserService;
import com.ylmao.admin.vo.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户", description = "用户管理与权限")
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    @Operation(summary = "用户分页列表")
    @Log(title = "用户分页查询", businessType = "QUERY")
    @SaCheckPermission("system:user:select")
    @GetMapping(value = "/list")
    public R<?> userList(@Valid PageQuery pageQuery, @Valid UserDto.UserList userDto ) {

        IPage<UserVo.UserListVo> userIPage = userService.selectPage(pageQuery, userDto);
        return pageData(userIPage.getRecords(), userIPage.getTotal());
    }

    @Operation(summary = "导出用户列表")
    @Log(title = "导出用户列表", businessType = "EXPORT")
    @SaCheckPermission("system:user:export")
    @GetMapping("/export")
    public void userExport(@Valid UserDto.UserList userDto, HttpServletResponse response) throws IOException {
        userService.exportUserList(userDto, response);
    }

    @Operation(summary = "用户远程检索")
    @Log(title = "用户远程检索", businessType = "QUERY")
    @SaCheckPermission("system:user:search")
    @GetMapping("/search")
    public R<?> userSearch(String keyword) {
        return R.ok(userService.searchUsers(keyword));
    }

    @Operation(summary = "账户是否唯一")
    @Log(title = "查询账户是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:user:insert", "system:user:update"}, mode = SaMode.OR)
    @GetMapping("/checkAccount")
    public R<Boolean> checkAccountUnique(String userAccount) {
        return R.ok(userService.getUserByAccount(userAccount) == null);
    }

    @Operation(summary = "新增用户")
    @Log(title = "新增用户数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:user:insert")
    @PostMapping("/add")
    public R<?> userInsert(@Valid @RequestBody UserDto.UserInsert userInsert) {
        userService.insertUserRoles(userInsert);
        return success();
    }

    @Operation(summary = "修改用户")
    @Log(title = "修改用户数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:user:update")
    @PutMapping("/update")
    public R<?> userUpdate(@Valid @RequestBody UserDto.UserUpdate userUpdate) {
        userService.userUpdate(userUpdate);
        // 角色变更后清理该用户 Session 中的角色与权限码缓存。
        SaSession userSession = StpUtil.getSessionByLoginId(userUpdate.userId(),false);
        if (userSession != null) {
            userSession.delete(StpInterfaceImpl.ROLE_LIST);
            userSession.delete(StpInterfaceImpl.PERM_LIST);
        }
        return success();
    }


    @Operation(summary = "管理员重置密码")
    @Log(title = "管理员重置密码", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:user:updatePwd")
    @PatchMapping("/updatePwd")
    public R<?> updateUserPwd(@Valid @RequestBody UserDto.UpdatePwd updatePwd) {
        userService.updateUserPwd(updatePwd);
        return success();
    }

    @Operation(summary = "修改用户启停状态")
    @Log(title = "修改用户状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:user:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateUserEnabled(@Valid @RequestBody UserDto.UpdateEnabled updateEnabled) {
        // 状态参数含义由 Service 统一校验，Controller 只负责转交 DTO。
        userService.updateUserEnabled(updateEnabled);
        return success();
    }

    @Operation(summary = "修改用户锁定状态")
    @Log(title = "修改用户锁定状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:user:updateLock")
    @PatchMapping("/updateLock")
    public R<?> updateUserLock(@Valid @RequestBody UserDto.UpdateLock updateLock) {
        userService.updateUserLock(updateLock);
        return success();
    }

    @Operation(summary = "删除用户")
    @Log(title = "删除用户数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/delete")
    public R<?> userDelete(String ids) {
        userService.deleteUsers(ids);
        return success();
    }

    @Operation(summary = "用户权限详情")
    @Log(title = "用户权限详情", businessType = "QUERY")
    @SaCheckPermission("system:user:permDetail")
    @GetMapping("/permDetail")
    public R<?> userPermDetail(String userId) {
        return okData(userService.getUserPermDetail(userId));
    }

}
