package com.ylmao.admin.controller.admin.setting;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.NoticeDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.NoticeService;
import com.ylmao.admin.service.UserService;
import com.ylmao.admin.vo.NoticeVo;
import com.ylmao.admin.vo.UserVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController extends BaseController {

    private final NoticeService noticeService;
    private final UserService userService;

    @Log(title = "公告分页查询", businessType = "QUERY")
    @SaCheckPermission("system:notice:select")
    @GetMapping("/list")
    public R<?> noticeList(@Valid PageQuery pageQuery, @Valid NoticeDto.NoticeList noticeList) {
        IPage<NoticeVo.NoticeListVo> noticePage = noticeService.selectPageList(pageQuery, noticeList);
        return pageData(noticePage.getRecords(), noticePage.getTotal());
    }

    @Log(title = "公告用户检索", businessType = "QUERY")
    @SaCheckPermission(value = {"system:notice:insert", "system:notice:update"}, mode = SaMode.OR)
    @GetMapping("/searchUser")
    public R<?> noticeSearchUser(String keyword) {
        List<UserVo.UserOptionVo> users = userService.searchForNotice(keyword);
        return R.ok(users);
    }

    @Log(title = "新增公告数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:notice:insert")
    @PostMapping("/add")
    public R<?> noticeInsert(@Valid @RequestBody NoticeDto.NoticeInsert noticeInsert) {
        noticeService.insert(noticeInsert);
        return success();
    }

    @Log(title = "修改公告数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:notice:update")
    @PutMapping("/update")
    public R<?> noticeUpdate(@Valid @RequestBody NoticeDto.NoticeUpdate noticeUpdate) {
        noticeService.updateById(noticeUpdate);
        return success();
    }

    @Log(title = "删除公告数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:notice:delete")
    @DeleteMapping("/delete")
    public R<?> noticeDelete(String ids) {
        noticeService.deleteById(ids);
        return success();
    }

    @Log(title = "修改公告发布状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:notice:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateNoticeEnabled(@Valid @RequestBody NoticeDto.UpdateEnabled updateEnabled) {
        noticeService.updateEnabled(updateEnabled);
        return success();
    }

    @Log(title = "公告控制台统计", businessType = "QUERY")
    @SaCheckPermission("system:notice:console")
    @GetMapping("/consoleStats")
    public R<?> noticeConsoleStats(String noticeId) {
        return okData(noticeService.getConsoleStats(noticeId));
    }

    @Log(title = "公告控制台接收人", businessType = "QUERY")
    @SaCheckPermission("system:notice:console")
    @GetMapping("/receiverList")
    public R<?> noticeReceiverList(@Valid PageQuery pageQuery, @Valid NoticeDto.ConsoleReceiverList consoleReceiverList) {
        IPage<NoticeVo.ConsoleReceiverVo> receiverPage = noticeService.selectConsoleReceiverPage(pageQuery, consoleReceiverList);
        return pageData(receiverPage.getRecords(), receiverPage.getTotal());
    }
}
