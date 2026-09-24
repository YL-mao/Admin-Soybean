package com.ylmao.admin.controller.admin.setting;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.FolderDto;
import com.ylmao.admin.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "文件夹", description = "文件目录树维护")
@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController extends BaseController {

    private final FolderService folderService;

    @Operation(summary = "目录树")
    @Log(title = "目录树查询", businessType = "QUERY")
    @SaCheckPermission("system:file:tree")
    @GetMapping("/tree")
    public R<?> folderTree() {
        return okData(folderService.listOptions());
    }

    @Operation(summary = "新增目录")
    @Log(title = "新增目录数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:file:folderInsert")
    @PostMapping("/add")
    public R<?> folderInsert(@Valid @RequestBody FolderDto.FolderInsert folderInsert) {
        folderService.insert(folderInsert);
        return success();
    }

    @Operation(summary = "修改目录")
    @Log(title = "修改目录数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:file:folderUpdate")
    @PutMapping("/update")
    public R<?> folderUpdate(@Valid @RequestBody FolderDto.FolderUpdate folderUpdate) {
        folderService.updateById(folderUpdate);
        return success();
    }

    @Operation(summary = "删除目录")
    @Log(title = "删除目录数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:file:folderDelete")
    @DeleteMapping("/delete")
    public R<?> folderDelete(String ids) {
        folderService.deleteByIds(ids);
        return success();
    }
}
