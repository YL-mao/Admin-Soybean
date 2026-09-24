package com.ylmao.admin.controller.admin.setting;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.FileResourceDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.FileResourceService;
import com.ylmao.admin.vo.FileResourceVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件", description = "文件资源上传与维护")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController extends BaseController {

    private final FileResourceService fileResourceService;

    @Operation(summary = "文件分页列表")
    @Log(title = "文件分页查询", businessType = "QUERY")
    @SaCheckPermission("system:file:select")
    @GetMapping("/list")
    public R<?> fileList(@Valid PageQuery pageQuery, @Valid FileResourceDto.FileList fileList) {
        IPage<FileResourceVo.FileListVo> page = fileResourceService.selectPage(pageQuery, fileList);
        return pageData(page.getRecords(), page.getTotal());
    }

    /** 公共上传：仅校验登录，不要求文件管理权限。 */
    @Operation(summary = "上传文件")
    @Log(title = "上传文件", businessType = "ADD", isSaveResponseData = true)
    @SaCheckLogin
    @PostMapping("/upload")
    public R<?> fileUpload(@RequestParam("file") MultipartFile file, @Valid FileResourceDto.FileUpload fileUpload) {
        return okData(fileResourceService.upload(file, fileUpload));
    }

    /** 上传规则（后缀/大小），供选择器限定与失败提示。 */
    @Operation(summary = "上传规则")
    @SaCheckLogin
    @GetMapping("/uploadRules")
    public R<?> fileUploadRules() {
        return okData(fileResourceService.uploadRules());
    }

    /**
     * 公共覆盖：仅校验登录；Service 内限制为持有 system:file:update、本人创建，或当前用户头像引用。
     * 文件管理详情与个人中心换头像共用。
     */
    @Operation(summary = "覆盖上传文件")
    @Log(title = "覆盖上传文件", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckLogin
    @PostMapping("/overwrite")
    public R<?> fileOverwrite(@RequestParam("fileId") String fileId,
                              @RequestParam("file") MultipartFile file) {
        return okData(fileResourceService.overwrite(fileId, file));
    }

    @Operation(summary = "修改文件元数据")
    @Log(title = "修改文件数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:file:update")
    @PutMapping("/update")
    public R<?> fileUpdate(@Valid @RequestBody FileResourceDto.FileUpdate fileUpdate) {
        fileResourceService.updateMetadata(fileUpdate);
        return success();
    }

    @Operation(summary = "删除文件")
    @Log(title = "删除文件数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:file:delete")
    @DeleteMapping("/delete")
    public R<?> fileDelete(String ids) {
        fileResourceService.softDeleteFiles(ids);
        return success();
    }

    @Operation(summary = "查询文件引用")
    @Log(title = "查询文件引用", businessType = "QUERY")
    @SaCheckPermission("system:file:delete")
    @GetMapping("/checkRef")
    public R<?> checkFileRef(String ids) {
        return okData(fileResourceService.checkRef(ids));
    }
}
