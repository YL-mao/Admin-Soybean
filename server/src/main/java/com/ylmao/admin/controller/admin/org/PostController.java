package com.ylmao.admin.controller.admin.org;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.PostDto;
import com.ylmao.admin.service.PostService;
import com.ylmao.admin.vo.PostVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "岗位", description = "岗位 CRUD 与下拉")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController extends BaseController {

    private final PostService postService;

    @Operation(summary = "岗位分页列表")
    @Log(title = "岗位分页查询", businessType = "QUERY")
    @SaCheckPermission("system:post:select")
    @GetMapping("/list")
    public R<?> postList(@Valid PageQuery pageQuery, @Valid PostDto.PostList postList) {
        IPage<PostVo.PostListVo> iPage = postService.selectPage(pageQuery, postList);
        return pageData(iPage.getRecords(), iPage.getTotal());
    }

    @Operation(summary = "新增岗位")
    @Log(title = "新增岗位数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:post:insert")
    @PostMapping("/add")
    public R<?> postInsert(@Valid @RequestBody PostDto.PostInsert postInsert) {
        postService.insert(postInsert);
        return success();
    }

    @Operation(summary = "修改岗位")
    @Log(title = "修改岗位数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:post:update")
    @PutMapping("/update")
    public R<?> postUpdate(@Valid @RequestBody PostDto.PostUpdate postUpdate) {
        postService.updateById(postUpdate);
        return success();
    }

    @Operation(summary = "删除岗位")
    @Log(title = "删除岗位数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:post:delete")
    @DeleteMapping("/delete")
    public R<?> postDelete(String ids) {
        postService.deleteById(ids);
        return success();
    }

    @Operation(summary = "岗位编码是否唯一")
    @Log(title = "查询岗位编码是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:post:insert", "system:post:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    public R<Boolean> checkPostCodeUnique(String postCode) {
        return R.ok(postService.checkPostCodeUnique(postCode) == null);
    }

    @Operation(summary = "岗位名称是否唯一")
    @Log(title = "查询岗位名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:post:insert", "system:post:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    public R<Boolean> checkPostNameUnique(String postName) {
        return R.ok(postService.checkPostNameUnique(postName) == null);
    }

    @Operation(summary = "修改岗位启停状态")
    @Log(title = "修改岗位状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:post:updateEnabled")
    @PatchMapping("/updateEnabled")
    public R<?> updatePostEnabled(@Valid @RequestBody PostDto.UpdateEnabled updateEnabled) {
        postService.updatePostEnabled(updateEnabled);
        return success();
    }

    /** 用户分配等下拉：返回 postId + postName */
    @Operation(summary = "岗位下拉选项")
    @SaCheckPermission(value = {"system:user:insert", "system:user:update"}, mode = SaMode.OR)
    @GetMapping("/options")
    public R<?> postOptions() {
        return okData(postService.listOptions());
    }
}
