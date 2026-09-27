package com.ylmao.admin.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ylmao.admin.common.FileNameSafeUtils;
import com.ylmao.admin.common.UploadConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.dto.FileResourceDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.FileResource;
import com.ylmao.admin.entity.Folder;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.FileResourceMapper;
import com.ylmao.admin.mapper.FolderMapper;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.utils.ServletUtils;
import com.ylmao.admin.vo.FileResourceVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileResourceService {

    private static final Logger log = LoggerFactory.getLogger(FileResourceService.class);
    private static final String DEFAULT_AVATAR_CLASSPATH = "static/admin/images/avatar.jpg";
    private static final DateTimeFormatter YEAR_MONTH_DAY = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final FileResourceMapper fileResourceMapper;
    private final FolderMapper folderMapper;
    private final UserMapper userMapper;
    private final UploadConfigService uploadConfigService;
    private final FolderService folderService;
    private final FingerprintService fingerprintService;

    public IPage<FileResourceVo.FileListVo> selectPage(PageQuery pageQuery, FileResourceDto.FileList fileList) {
        LambdaQueryWrapper<FileResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileResource::getIsDel, 0);
        if (fileList != null) {
            if (StrUtil.isNotBlank(fileList.folderId())) {
                wrapper.eq(FileResource::getFolderId, fileList.folderId());
            }
            if (StrUtil.isNotBlank(fileList.originalName())) {
                wrapper.like(FileResource::getOriginalName, fileList.originalName());
            }
        }
        wrapper.orderByDesc(FileResource::getCreateTime).orderByDesc(FileResource::getFileId);
        return fileResourceMapper.selectPage(pageQuery.toMpPage(), wrapper)
                .convert(file -> FileResourceVo.FileListVo.from(file, uploadConfigService.buildAccessUrl(file.getFileId())));
    }

    public FileResourceVo.UploadRulesVo uploadRules() {
        return uploadConfigService.uploadRules();
    }

    /**
     * 上传文件；folderId 为空时挂到「未分类」。
     * 先落盘再入库：入库失败则删除刚写入的磁盘文件（不把磁盘 IO 包进 DB 事务）。
     */
    public FileResourceVo.FileListVo upload(MultipartFile file, FileResourceDto.FileUpload fileUpload) {
        uploadConfigService.assertUploadEnabled();
        String storageType = uploadConfigService.currentStorageType();
        if (fileUpload == null) {
            throw new BusinessException("参数不合法");
        }
        String scene = uploadConfigService.normalizeScene(fileUpload.fileScene());
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        long maxBytes = uploadConfigService.maxBytes();
        if (file.getSize() > maxBytes) {
            throw new BusinessException("文件大小超出限制（最大 " + (maxBytes / 1024 / 1024) + "MB）");
        }
        // 展示名规范化（去路径段/控制字符）；磁盘仍用 storage_key。
        String originalName = FileNameSafeUtils.normalizeOriginalName(file.getOriginalFilename());
        String suffix = extractSuffix(originalName);
        Set<String> allowed = uploadConfigService.extensionsForScene(scene);
        if (!allowed.contains(suffix)) {
            throw new BusinessException("文件类型不被允许，仅支持：" + String.join("、", allowed));
        }
        String targetFolderId = resolveUploadFolderId(fileUpload.folderId());
        folderService.requireActiveFolder(targetFolderId);

        // 头像等图片场景默认需登录；未传时 document/excel 也默认需登录更安全。
        int loginFlag = fileUpload.needLogin() == null ? 1 : fileUpload.needLogin();
        if (loginFlag != 0 && loginFlag != 1) {
            throw new BusinessException("参数不合法");
        }

        // 上传永远新建：展示名可同名并存；覆盖请走 overwrite。
        String fileId = IdWorker.getIdStr();
        String storageKey = buildStorageKey(fileId, suffix);
        Path diskPath = resolveDiskPath(storageKey);
        try {
            Files.createDirectories(diskPath.getParent());
            file.transferTo(diskPath);
        } catch (IOException e) {
            log.error("写入本地文件失败 storageKey={}", storageKey, e);
            throw new BusinessException("文件保存失败");
        }

        FileResource entity = new FileResource();
        entity.setFileId(fileId);
        entity.setFolderId(targetFolderId);
        entity.setOriginalName(originalName);
        entity.setStorageKey(storageKey);
        entity.setStorageType(storageType);
        entity.setFileSuffix(suffix);
        // 入库 MIME 按后缀推导，不采信 multipart 自报类型。
        entity.setContentType(resolveStoredContentType(suffix));
        entity.setFileSize(file.getSize());
        entity.setFileScene(scene);
        entity.setNeedLogin(loginFlag);
        try {
            int rows = fileResourceMapper.insert(entity);
            if (rows <= 0) {
                throw new BusinessException("文件保存失败");
            }
        } catch (RuntimeException e) {
            // 入库失败时回滚刚写入的磁盘文件，避免孤儿文件。
            deleteQuietly(diskPath);
            throw e;
        }
        return FileResourceVo.FileListVo.from(entity, uploadConfigService.buildAccessUrl(fileId));
    }

    /** 覆盖重传：保持同一 fileId，校验场景后缀；展示名可随新文件更新且允许同名并存。 */
    public FileResourceVo.FileListVo overwrite(String fileId, MultipartFile file) {
        uploadConfigService.assertUploadEnabled();
        uploadConfigService.requireLocalStorage();
        FileResource existing = fileResourceMapper.selectById(fileId);
        if (existing == null || !Integer.valueOf(0).equals(existing.getIsDel())) {
            throw new BusinessException("文件不存在");
        }
        assertCanOverwrite(existing);
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        if (file.getSize() > uploadConfigService.maxBytes()) {
            long maxMb = uploadConfigService.maxBytes() / 1024 / 1024;
            throw new BusinessException("文件大小超出限制（最大 " + maxMb + "MB）");
        }
        String originalName = StrUtil.isBlank(file.getOriginalFilename())
                ? existing.getOriginalName()
                : file.getOriginalFilename();
        originalName = FileNameSafeUtils.normalizeOriginalName(originalName);
        String suffix = extractSuffix(originalName);
        Set<String> allowed = uploadConfigService.extensionsForScene(existing.getFileScene());
        if (!allowed.contains(suffix)) {
            throw new BusinessException("文件类型不被允许，仅支持：" + String.join("、", allowed));
        }
        Path oldPath = resolveDiskPath(existing.getStorageKey());
        String storageKey = existing.getStorageKey();
        if (StrUtil.isBlank(storageKey) || !suffix.equalsIgnoreCase(existing.getFileSuffix())) {
            storageKey = buildStorageKey(existing.getFileId(), suffix);
        }
        Path newPath = resolveDiskPath(storageKey);
        boolean samePath = Objects.equals(oldPath, newPath);
        // 始终先落 staging；同路径时用 .bak 保住旧文件，先完成盘替换再 updateById，失败可回滚盘。
        Path stagingPath = newPath.resolveSibling(newPath.getFileName() + ".uploading-" + IdWorker.getIdStr());
        Path backupPath = null;
        try {
            Files.createDirectories(stagingPath.getParent());
            file.transferTo(stagingPath);
            if (samePath && Files.isRegularFile(newPath)) {
                backupPath = newPath.resolveSibling(newPath.getFileName() + ".bak-" + IdWorker.getIdStr());
                Files.move(newPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(stagingPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            stagingPath = null;
        } catch (IOException e) {
            log.error("覆盖写入本地文件失败 fileId={}", existing.getFileId(), e);
            deleteQuietly(stagingPath);
            if (backupPath != null) {
                try {
                    Files.move(backupPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException restoreEx) {
                    log.error("覆盖失败后恢复备份失败 fileId={}", existing.getFileId(), restoreEx);
                }
            }
            throw new BusinessException("文件保存失败");
        }
        existing.setStorageKey(storageKey);
        existing.setFileSuffix(suffix);
        existing.setContentType(resolveStoredContentType(suffix));
        existing.setFileSize(file.getSize());
        existing.setFileScene(existing.getFileScene());
        existing.setStorageType(uploadConfigService.currentStorageType());
        existing.setOriginalName(originalName);
        // 当前用户头像文件强制可直链，否则 <img> 无 saToken 会 401
        if (isCurrentUserAvatarFile(existing.getFileId())) {
            existing.setNeedLogin(0);
        }
        try {
            int rows = fileResourceMapper.updateById(existing);
            if (rows <= 0) {
                throw new BusinessException("文件不存在或修改失败");
            }
        } catch (RuntimeException e) {
            // 库失败：同路径恢复备份；换路径删掉新文件，旧路径不动。
            if (backupPath != null) {
                try {
                    Files.move(backupPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                    backupPath = null;
                } catch (IOException restoreEx) {
                    log.error("覆盖入库失败后恢复备份失败 fileId={}", existing.getFileId(), restoreEx);
                }
            } else if (!samePath) {
                deleteQuietly(newPath);
            }
            throw e;
        }
        deleteQuietly(backupPath);
        if (!samePath) {
            deleteQuietly(oldPath);
        }
        return FileResourceVo.FileListVo.from(existing, uploadConfigService.buildAccessUrl(existing.getFileId()));
    }

    /** 修改文件名、需登录；folderId 变化即移动到目标虚拟目录（物理路径不变）。同名允许并存。 */
    @Transactional
    public void updateMetadata(FileResourceDto.FileUpdate fileUpdate) {
        FileResource existing = fileResourceMapper.selectById(fileUpdate.fileId());
        if (existing == null || !Integer.valueOf(0).equals(existing.getIsDel())) {
            throw new BusinessException("文件不存在");
        }
        String targetFolderId = resolveUploadFolderId(fileUpdate.folderId());
        folderService.requireActiveFolder(targetFolderId);
        String originalName = FileNameSafeUtils.normalizeOriginalName(fileUpdate.originalName());
        existing.setFolderId(targetFolderId);
        existing.setOriginalName(originalName);
        existing.setNeedLogin(fileUpdate.needLogin());
        int rows = fileResourceMapper.updateById(existing);
        if (rows <= 0) {
            throw new BusinessException("文件不存在或修改失败");
        }
    }

    public FileResourceVo.CheckRefResult checkRef(String ids) {
        List<String> idList = splitIds(ids);
        for (String fileId : idList) {
            if (isReferencedByAvatar(fileId)) {
                return new FileResourceVo.CheckRefResult(true, "文件仍被用户头像引用，确认后将删除磁盘文件且不可恢复");
            }
        }
        return new FileResourceVo.CheckRefResult(false, "未被引用");
    }

    public boolean isReferencedByAvatar(String fileId) {
        if (StrUtil.isBlank(fileId)) {
            return false;
        }
        // 与覆盖鉴权一致：去 query 后须以 /upload/{fileId} 结尾，避免 LIKE 子串误判。
        String suffix = "/upload/" + fileId;
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .apply("SUBSTRING_INDEX(IFNULL(user_avatar,''), '?', 1) LIKE CONCAT('%', {0})", suffix));
        return count != null && count > 0;
    }

    @Transactional
    public void softDeleteFiles(String ids) {
        List<String> idList = splitIds(ids);
        if (idList.isEmpty()) {
            throw new BusinessException("请选择要删除的文件");
        }
        List<FileResource> files = new ArrayList<>();
        for (String fileId : idList) {
            FileResource file = fileResourceMapper.selectById(fileId);
            if (file == null || !Integer.valueOf(0).equals(file.getIsDel())) {
                throw new BusinessException("文件不存在");
            }
            files.add(file);
        }
        // 先逻辑删入库；提交成功后再删磁盘，避免事务回滚后文件已丢。
        int rows = fileResourceMapper.softDeleteByIds(idList, LocalDateTime.now());
        if (rows <= 0) {
            throw new BusinessException("文件不存在或删除失败");
        }
        List<Path> diskPaths = files.stream()
                .map(file -> resolveDiskPath(file.getStorageKey()))
                .toList();
        deleteDiskAfterCommit(diskPaths);
    }

    /** 级联逻辑删目录（含子孙目录与其下文件），事务提交后再删磁盘；内置「未分类」禁止删除。 */
    @Transactional
    public void softDeleteFolders(String ids) {
        List<String> idList = splitIds(ids);
        if (idList.isEmpty()) {
            throw new BusinessException("请选择要删除的目录");
        }
        Set<String> folderIdsToDelete = new LinkedHashSet<>();
        for (String folderId : idList) {
            Folder folder = folderMapper.selectById(folderId);
            if (folder == null || !Integer.valueOf(0).equals(folder.getIsDel())) {
                throw new BusinessException("目录不存在");
            }
            folderService.assertNotBuiltin(folder);
            folderIdsToDelete.add(folder.getFolderId());
            String pathPrefix = buildDescendantPathPrefix(folder);
            List<Folder> descendants = folderMapper.selectDescendantFoldersByPath(pathPrefix);
            for (Folder child : descendants) {
                if (child.getIsDel() == null || child.getIsDel() == 0) {
                    folderIdsToDelete.add(child.getFolderId());
                }
            }
        }
        List<FileResource> files = fileResourceMapper.selectList(new LambdaQueryWrapper<FileResource>()
                .eq(FileResource::getIsDel, 0)
                .in(FileResource::getFolderId, folderIdsToDelete));
        // 单文件删除走 checkRef 确认；目录级联删无该步骤，有头像引用时直接拒绝。
        for (FileResource file : files) {
            if (isReferencedByAvatar(file.getFileId())) {
                throw new BusinessException("目录下存在仍被用户头像引用的文件，请先在文件列表中处理后再删除目录");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        // 先逻辑删库，提交后再删磁盘，避免回滚后盘文件已丢。
        if (!files.isEmpty()) {
            fileResourceMapper.softDeleteByIds(files.stream().map(FileResource::getFileId).toList(), now);
        }
        int rows = folderMapper.softDeleteByIds(folderIdsToDelete, now);
        if (rows <= 0) {
            throw new BusinessException("目录不存在或删除失败");
        }
        List<Path> diskPaths = files.stream()
                .map(file -> resolveDiskPath(file.getStorageKey()))
                .toList();
        deleteDiskAfterCommit(diskPaths);
    }

    /**
     * 预览：needLogin=1 且未登录 → 401；
     * 记录不存在 → 404（避免把随机/错误 id 回成默认头像造成「串图」错觉）；
     * 已登记的图片软删或磁盘缺失 → 默认头像字节；文档类 → 404。
     */
    public ResponseEntity<Resource> preview(String fileId) {
        if (StrUtil.isBlank(fileId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        FileResource file = fileResourceMapper.selectByIdIncludeDeleted(fileId);
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // /upload/** 已从全局登录拦截排除；needLogin=1 时补登录 + 管理端指纹校验。
        if (Integer.valueOf(1).equals(file.getNeedLogin())) {
            StpUtil.checkLogin();
            fingerprintService.checkOrKickAsNotLogin(ServletUtils.getRequest());
        }
        boolean deleted = file.getIsDel() != null && file.getIsDel() == 1;
        Path diskPath = resolveDiskPath(file.getStorageKey());
        boolean missingDisk = StrUtil.isBlank(file.getStorageKey()) || !Files.isRegularFile(diskPath);
        boolean imageScene = isImageSceneSafe(file.getFileScene());
        if (deleted || missingDisk) {
            if (imageScene) {
                return defaultAvatarResponse();
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        org.springframework.core.io.FileSystemResource resource =
                new org.springframework.core.io.FileSystemResource(diskPath);
        // 预览 MIME 只认后缀白名单，不回显客户端/库里的 Content-Type，避免 text/html 等被当页执行。
        MediaType mediaType = resolvePreviewMediaType(file.getFileSuffix());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }

    /**
     * 覆盖鉴权：文件修改权 / 本人创建 / 当前用户头像引用该 fileId。
     * 避免登录用户凭 fileId 覆盖他人文件。
     */
    private void assertCanOverwrite(FileResource existing) {
        if (StpUtil.hasPermission("system:file:update")) {
            return;
        }
        String userId = SaTokenUtil.getUserId();
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户未登录");
        }
        if (Objects.equals(existing.getCreateBy(), userId)) {
            return;
        }
        if (isCurrentUserAvatarFile(existing.getFileId())) {
            return;
        }
        throw new BusinessException("无权覆盖该文件");
    }

    /** 当前登录用户的 user_avatar 是否指向该 fileId */
    private boolean isCurrentUserAvatarFile(String fileId) {
        if (StrUtil.isBlank(fileId)) {
            return false;
        }
        String userId = SaTokenUtil.getUserId();
        if (StrUtil.isBlank(userId)) {
            return false;
        }
        User user = userMapper.selectById(userId);
        return user != null && StrUtil.isNotBlank(user.getUserAvatar())
                && stripUploadQuery(user.getUserAvatar()).endsWith("/upload/" + fileId);
    }

    private static String stripUploadQuery(String url) {
        int q = url.indexOf('?');
        return q >= 0 ? url.substring(0, q) : url;
    }

    private String resolveUploadFolderId(String folderId) {
        // 未指定或选中虚拟根时，落到内置「未分类」。
        if (StrUtil.isBlank(folderId) || "0".equals(folderId.trim())) {
            return UploadConfigCodes.UNCLASSIFIED_FOLDER_ID;
        }
        return folderId.trim();
    }

    private String buildStorageKey(String fileId, String suffix) {
        // 按日分目录；物理文件名用雪花 fileId，与主键一致且并发唯一。
        String ymd = LocalDate.now().format(YEAR_MONTH_DAY);
        return ymd + "/" + fileId + "." + suffix;
    }

    private Path resolveDiskPath(String storageKey) {
        if (StrUtil.isBlank(storageKey)) {
            return uploadConfigService.resolveLocalRoot().resolve("__missing__");
        }
        // 防止 storage_key 越出本地根目录。
        Path root = uploadConfigService.resolveLocalRoot().toAbsolutePath().normalize();
        Path target = root.resolve(storageKey).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException("参数不合法");
        }
        return target;
    }

    private String buildDescendantPathPrefix(Folder folder) {
        String path = StrUtil.blankToDefault(folder.getFolderPath(), "0");
        return path + "," + folder.getFolderId();
    }

    private String extractSuffix(String originalName) {
        int idx = originalName.lastIndexOf('.');
        if (idx < 0 || idx == originalName.length() - 1) {
            throw new BusinessException("文件类型不被允许");
        }
        return originalName.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private List<String> splitIds(String ids) {
        if (StrUtil.isBlank(ids)) {
            return List.of();
        }
        return StrUtil.splitTrim(ids, ',').stream().filter(StrUtil::isNotBlank).distinct().toList();
    }

    private boolean isImageSceneSafe(String fileScene) {
        if (StrUtil.isBlank(fileScene)) {
            return true;
        }
        return "image".equalsIgnoreCase(fileScene.trim());
    }

    private ResponseEntity<Resource> defaultAvatarResponse() {
        ClassPathResource resource = new ClassPathResource(DEFAULT_AVATAR_CLASSPATH);
        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }

    /** 预览响应 MIME：仅按后缀映射；未识别或高风险后缀一律二进制流。 */
    private static MediaType resolvePreviewMediaType(String fileSuffix) {
        String suffix = normalizeSuffix(fileSuffix);
        return switch (suffix) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            case "bmp" -> MediaType.parseMediaType("image/bmp");
            case "ico" -> MediaType.parseMediaType("image/x-icon");
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "txt", "log", "csv", "md" -> MediaType.TEXT_PLAIN;
            case "json" -> MediaType.APPLICATION_JSON;
            // svg/html 等可执行标记语言不按文档类型回显，降为下载流。
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    /** 入库 content_type：与预览映射一致，避免库内残留客户端伪造 MIME。 */
    private static String resolveStoredContentType(String fileSuffix) {
        return resolvePreviewMediaType(fileSuffix).toString();
    }

    private static String normalizeSuffix(String fileSuffix) {
        if (StrUtil.isBlank(fileSuffix)) {
            return "";
        }
        String suffix = fileSuffix.trim().toLowerCase(Locale.ROOT);
        return suffix.startsWith(".") ? suffix.substring(1) : suffix;
    }

    /** 事务提交后再删磁盘；无事务时立即删。 */
    private void deleteDiskAfterCommit(Collection<Path> paths) {
        if (paths == null || paths.isEmpty()) {
            return;
        }
        List<Path> copy = List.copyOf(paths);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (Path path : copy) {
                        deleteQuietly(path);
                    }
                }
            });
            return;
        }
        for (Path path : copy) {
            deleteQuietly(path);
        }
    }

    private void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("删除磁盘文件失败 path={}", path, e);
        }
    }
}
