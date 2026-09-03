package com.ylmao.admin.controller.user;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.NoticeDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.NoticeService;
import com.ylmao.admin.service.NoticeUserService;
import com.ylmao.admin.vo.NoticeVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/notice")
@RequiredArgsConstructor
public class UserNoticeController extends BaseController {

    private final NoticeService noticeService;
    private final NoticeUserService noticeUserService;

    @Log(title = "用户公告头部消息", businessType = "QUERY")
    @GetMapping("/header")
    public R<List<NoticeVo.HeaderMessageTabVo>> userNoticeHeader() {
        // 无权限时仍返回空列表，顶部铃铛显示为空。
        if (!StpUtil.hasPermission("user:notice:view")) {
            return okData(noticeService.buildEmptyNoticeHeaderTabs());
        }
        return okData(noticeService.buildUserNoticeHeader(8));
    }

    @Log(title = "用户公告分页查询", businessType = "QUERY")
    @SaCheckPermission("user:notice:view")
    @GetMapping("/list")
    public R<?> userNoticeList(@Valid PageQuery pageQuery, @Valid NoticeDto.UserNoticeList userNoticeList) {
        // 查询条件不含 userId，Service 仅返回当前登录用户收件箱。
        IPage<NoticeVo.UserInboxVo> noticePage = noticeService.selectUserInboxPageList(pageQuery, userNoticeList);
        return pageData(noticePage.getRecords(), noticePage.getTotal());
    }

    @Log(title = "用户公告标记已读", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("user:notice:view")
    @PatchMapping("/updateRead")
    public R<?> updateUserNoticeRead(@Valid @RequestBody NoticeDto.UpdateRead updateRead) {
        // 仅 noticeId，实际 userId 由 Service 从 Session 取，只能改当前登录用户收件箱。
        noticeUserService.updateUserNoticeRead(updateRead.noticeId());
        return success();
    }

    @Log(title = "用户公告全部已读", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("user:notice:view")
    @PatchMapping("/readAll")
    public R<?> readAllUserNotices() {
        // 无请求体，Service 仅处理当前登录用户可见未读公告。
        noticeUserService.readAllUserNotices();
        return success();
    }
}
