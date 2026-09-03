package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.JobDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.JobService;
import com.ylmao.admin.vo.JobVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController extends BaseController {

    private final JobService jobService;

    @Log(title = "定时任务分页查询", businessType = "QUERY")
    @SaCheckPermission("system:job:select")
    @GetMapping("/list")
    public R<?> jobList(@Valid PageQuery pageQuery, @Valid JobDto.JobList jobList) {
        IPage<JobVo.JobListVo> iPage = jobService.selectPage(pageQuery, jobList);
        return pageData(iPage.getRecords(), iPage.getTotal());
    }

    @Log(title = "修改定时任务状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:job:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updateJobEnabled(@Valid @RequestBody JobDto.UpdateEnabled updateEnabled) {
        jobService.updateJobEnabled(updateEnabled);
        return success();
    }

    @Log(title = "手动执行定时任务", businessType = "OTHER", isSaveResponseData = true)
    @SaCheckPermission("system:job:run")
    @PostMapping("/run")
    public R<?> jobRun(@Valid @RequestBody JobDto.JobRun jobRun) {
        jobService.runManual(jobRun);
        return success();
    }

    @Log(title = "定时任务执行日志", businessType = "QUERY")
    @SaCheckPermission("system:job:log")
    @GetMapping("/logList")
    public R<?> jobLogList(@Valid PageQuery pageQuery, @Valid JobDto.JobLogList jobLogList) {
        IPage<JobVo.JobLogListVo> iPage = jobService.selectLogPage(pageQuery, jobLogList);
        return pageData(iPage.getRecords(), iPage.getTotal());
    }
}
