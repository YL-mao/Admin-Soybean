package com.ylmao.admin.controller.admin.system;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController extends BaseController {

    private final FileResourceService fileResourceService;

    @Log(title = "文件分页查询", businessType = "QUERY")
    @SaCheckPermission("system:file:select")
    @GetMapping("/list")
    public R<?> fileList(@Valid PageQuery pageQuery, @Valid FileResourceDto.FileList fileList) {
        IPage<FileResourceVo.FileListVo> page = fileResourceService.selectPage(pageQuery, fileList);
        return pageData(page.getRecords(), page.getTotal());
    }

    /** 公共上传：仅校验登录，不要求文件管理权限。 */
    @Log(title = "上传文件", businessType = "ADD", isSaveResponseData = true)
    @SaCheckLogin
    @PostMapping("/upload")
    public R<?> fileUpload(@RequestParam("file") MultipartFile file, @Valid FileResourceDto.FileUpload fileUpload) {
        return okData(fileResourceService.upload(file, fileUpload));
    }

    /** 上传规则（后缀/大小），供选择器限定与失败提示。 */
    @SaCheckLogin
    @GetMapping("/uploadRules")
    public R<?> fileUploadRules() {
        return okData(fileResourceService.uploadRules());
    }

    @Log(title = "覆盖上传文件", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:file:update")
    @PostMapping("/overwrite")
    public R<?> fileOverwrite(@RequestParam("fileId") String fileId,
                              @RequestParam("file") MultipartFile file) {
        return okData(fileResourceService.overwrite(fileId, file));
    }

    @Log(title = "修改文件数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:file:update")
    @PutMapping("/update")
    public R<?> fileUpdate(@Valid @RequestBody FileResourceDto.FileUpdate fileUpdate) {
        fileResourceService.updateMetadata(fileUpdate);
        return success();
    }

    @Log(title = "删除文件数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:file:delete")
    @DeleteMapping("/delete")
    public R<?> fileDelete(String ids) {
        fileResourceService.softDeleteFiles(ids);
        return success();
    }

    @Log(title = "查询文件名是否唯一", businessType = "QUERY")
    @SaCheckLogin
    @GetMapping("/checkName")
    public R<Boolean> checkFileNameUnique(String folderId, String originalName) {
        return R.ok(fileResourceService.checkOriginalNameUnique(folderId, originalName) == null);
    }

    @Log(title = "查询文件引用", businessType = "QUERY")
    @SaCheckPermission("system:file:delete")
    @GetMapping("/checkRef")
    public R<?> checkFileRef(String ids) {
        return okData(fileResourceService.checkRef(ids));
    }
}
