package com.ylmao.admin.controller.admin.ops;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.OnlineDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.OnlineService;
import com.ylmao.admin.vo.OnlineVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "在线用户", description = "在线会话查询与强退")
@RestController
@RequestMapping("/api/admin/online")
@RequiredArgsConstructor
public class OnlineController extends BaseController {

    private final OnlineService onlineService;

    @Operation(summary = "在线用户分页列表")
    @Log(title = "在线用户分页查询", businessType = "QUERY")
    @SaCheckPermission("system:online:select")
    @GetMapping("/list")
    public R<?> onlineList(@Valid PageQuery pageQuery, @Valid OnlineDto.OnlineList onlineList) {
        IPage<OnlineVo.OnlineListVo> iPage = onlineService.selectPage(pageQuery, onlineList);
        return pageData(iPage.getRecords(), iPage.getTotal());
    }

    @Operation(summary = "按 Token 强退")
    @Log(title = "强退在线用户", businessType = "OTHER", isSaveResponseData = true)
    @SaCheckPermission("system:online:kick")
    @PatchMapping("/kick")
    public R<?> onlineKick(@Valid @RequestBody OnlineDto.OnlineKick onlineKick) {
        onlineService.kickByToken(onlineKick);
        return success();
    }

    @Operation(summary = "按用户强退全部会话")
    @Log(title = "按用户强退全部会话", businessType = "OTHER", isSaveResponseData = true)
    @SaCheckPermission("system:online:kick")
    @PatchMapping("/kickUser")
    public R<?> onlineKickUser(@Valid @RequestBody OnlineDto.OnlineKickUser onlineKickUser) {
        onlineService.kickByUserId(onlineKickUser);
        return success();
    }
}
