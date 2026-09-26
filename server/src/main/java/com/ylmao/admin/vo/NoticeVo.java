package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ylmao.admin.common.TextSafeUtils;
import com.ylmao.admin.entity.Notice;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class NoticeVo {

    public NoticeVo() {
    }

    public record NoticeListVo(
            String noticeId,
            String noticeTitle,
            String noticeContent,
            Integer noticeType,
            String noticeTypeName,
            Integer receiverType,
            String receiverTypeName,
            String receiverIds,
            String noticeDesc,
            Integer isSend,
            String isSendName,
            Integer orderNum,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime sendTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime expireTime,
            String createBy,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime createTime,
            /* 是否已写入用户公告关联表，有记录时不允许改回草稿。 */
            Boolean delivered
    ) {

        public static NoticeListVo from (Notice notice,boolean delivered, String noticeTypeName, String receiverTypeName)
        {
            return new NoticeListVo(
                    notice.getNoticeId(),
                    TextSafeUtils.sanitizeNoticeHtml(notice.getNoticeTitle()),
                    TextSafeUtils.sanitizeNoticeHtml(notice.getNoticeContent()),
                    notice.getNoticeType(),
                    noticeTypeName,
                    notice.getReceiverType(),
                    receiverTypeName,
                    notice.getReceiverIds(),
                    notice.getNoticeDesc(),
                    notice.getIsSend(),
                    notice.getIsSend() != null && notice.getIsSend() == 1 ? "已发布" : "草稿",
                    notice.getOrderNum(),
                    notice.getSendTime(),
                    notice.getExpireTime(),
                    notice.getCreateBy(),
                    notice.getCreateTime(),
                    delivered
            );
        }
    }

    /**
     * 个人管理收件箱列表项（接口返回）。
     */
    public record UserInboxVo(
            String noticeId,
            String noticeTitle,
            String noticeContent,
            Integer noticeType,
            String noticeTypeName,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime sendTime,
            Integer readState,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime readTime
    ) {

        public static UserInboxVo from (UserInboxQueryVo query, String noticeTypeName){
            return new UserInboxVo(
                    query.getNoticeId(),
                    TextSafeUtils.sanitizeNoticeHtml(query.getNoticeTitle()),
                    TextSafeUtils.sanitizeNoticeHtml(query.getNoticeContent()),
                    query.getNoticeType(),
                    noticeTypeName,
                    query.getSendTime(),
                    query.getReadState(),
                    query.getReadTime()
            );
        }
    }

    /**
     * 收件箱联表查询映射（MyBatis），不含 noticeTypeName。
     */
    @Data
    public static class UserInboxQueryVo {
        private String noticeId;
        private String noticeTitle;
        private String noticeContent;
        private Integer noticeType;
        private LocalDateTime sendTime;
        private Integer readState;
        private LocalDateTime readTime;
    }

    /**
     * 顶栏/首页短列表：单条未读公告。
     */
    public record HeaderMessageItemVo(
            String id,
            /* 类型图标标识（历史字段名 icon，前端可按需映射）。 */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String icon,
            /* 公告类型，用于图标底色样式。 */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Integer noticeType,
            String title,
            String context,
            String form,
            String time
    ) {
    }

    /**
     * 顶栏短列表：按公告类型分 Tab。
     */
    public record HeaderMessageTabVo(
            Integer id,
            String title,
            List<HeaderMessageItemVo> children
    ) {
    }

    /**
     * 顶部铃铛 / 首页未读：真实未读总数 + 按类型截断的短列表。
     */
    public record HeaderMessageVo(
            long unreadCount,
            List<HeaderMessageTabVo> tabs
    ) {
    }

    /**
     * 公告控制台：阅读统计与公告摘要。
     */
    public record ConsoleStatsVo(
            String noticeId,
            String noticeTitle,
            Integer noticeType,
            String noticeTypeName,
            Integer receiverType,
            String receiverTypeName,
            /* 指定角色/部门时的目标名称列表；全体与指定个人为空。 */
            List<String> receiverTargets,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime sendTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime expireTime,
            long totalCount,
            long readCount,
            long unreadCount,
            /* 阅读率百分比文本，如 75%。 */
            String readRate
    ) {
    }

    /**
     * 控制台接收人列表项。
     */
    public record ConsoleReceiverVo(
            String userId,
            String userAccount,
            String userName,
            String deptName,
            Integer readState,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime readTime
    ) {
        public static ConsoleReceiverVo from (ConsoleReceiverQueryVo query, String deptName){
            return new ConsoleReceiverVo(
                    query.getUserId(),
                    query.getUserAccount(),
                    query.getUserName(),
                    deptName != null && !deptName.isBlank() ? deptName : "无部门",
                    query.getReadState(),
                    query.getReadTime()
            );
        }
    }

    /**
     * 控制台接收人联表查询映射（MyBatis）。
     */
    @Data
    public static class ConsoleReceiverQueryVo {
        private String userId;
        private String userAccount;
        private String userName;
        private String deptId;
        private Integer readState;
        private LocalDateTime readTime;
    }

    /**
     * 控制台阅读计数查询映射（MyBatis）。
     */
    @Data
    public static class ConsoleCountQueryVo {
        private Long totalCount;
        private Long readCount;
        private Long unreadCount;
    }

    /**
     * 公告类型对应图标标识（兼容历史 layui-icon-* 字符串）。
     */
    private static final Map<Integer, String> NOTICE_TYPE_ICONS = Map.of(
            1, "layui-icon-notice",
            2, "layui-icon-fire",
            3, "layui-icon-speaker",
            4, "layui-icon-email"
    );

    public static String noticeTypeIcon(Integer noticeType) {
        return NOTICE_TYPE_ICONS.getOrDefault(noticeType, NOTICE_TYPE_ICONS.get(1));
    }
}
