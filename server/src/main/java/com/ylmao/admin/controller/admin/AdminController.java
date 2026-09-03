package com.ylmao.admin.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.model.Menu;
import com.ylmao.admin.service.PermService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController extends BaseController {

    private final PermService permService;

    @GetMapping("/permMenu")
    public R<List<Menu>> getUserPermMenu() {
        return okData(permService.getUserPermMenu(SaTokenUtil.getUserId()));
    }

    @Log(title = "用户注销", loggingType = "LOGIN", businessType = "LOGOUT")
    @PostMapping("/loginOut")
    public R<?> loginOut() {
        StpUtil.logout();
        return success("注销成功");
    }
}
