package com.ylmao.admin.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.MenuDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_menu")
@EqualsAndHashCode(callSuper = false)
public final class Menu {
    @TableId(type = IdType.ASSIGN_ID)
    private String menuId;
    private String parentId;
    private String menuPath;
    private String menuName;
    private String menuDesc;
    private Integer menuType;
    private String routeName;
    private String routePath;
    private String routeComp;
    private String routeQuery;
    private String menuHref;
    private Integer isBlank;
    /** 空标识存 NULL，配合库唯一索引允许多个目录无码。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String permCode;
    private String menuIcon;
    private Integer iconType;
    private String i18nKey;
    private Integer keepAlive;
    private Integer isShow;
    private String activeMenu;
    private Integer orderNum;
    private Integer isEnabled;
    /** 审计字段由 MyBatis-Plus 自动填充创建人。 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 审计字段由 MyBatis-Plus 自动填充更新人。 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDel;

    /** 角色授权树勾选回显，非数据库字段。 */
    @TableField(exist = false)
    private String checkArr = "0";

    public Menu(MenuDto.MenuInsert menuInsert) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.parentId = menuInsert.parentId();
        this.menuName = menuInsert.menuName();
        this.menuDesc = menuInsert.menuDesc();
        this.menuType = menuInsert.menuType();
        this.routeName = menuInsert.routeName();
        this.routePath = menuInsert.routePath();
        this.routeComp = menuInsert.routeComp();
        this.routeQuery = menuInsert.routeQuery();
        this.menuHref = menuInsert.menuHref();
        this.isBlank = menuInsert.isBlank();
        this.permCode = normalizePermCode(menuInsert.permCode());
        this.menuIcon = menuInsert.menuIcon();
        this.iconType = menuInsert.iconType();
        this.i18nKey = menuInsert.i18nKey();
        this.keepAlive = menuInsert.keepAlive();
        this.isShow = menuInsert.isShow();
        this.activeMenu = menuInsert.activeMenu();
        this.orderNum = menuInsert.orderNum();
        this.isEnabled = menuInsert.isEnabled();
    }

    public Menu(MenuDto.MenuUpdate menuUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.menuId = menuUpdate.menuId();
        this.parentId = menuUpdate.parentId();
        this.menuName = menuUpdate.menuName();
        this.menuDesc = menuUpdate.menuDesc();
        this.menuType = menuUpdate.menuType();
        this.routeName = menuUpdate.routeName();
        this.routePath = menuUpdate.routePath();
        this.routeComp = menuUpdate.routeComp();
        this.routeQuery = menuUpdate.routeQuery();
        this.menuHref = menuUpdate.menuHref();
        this.isBlank = menuUpdate.isBlank();
        this.permCode = normalizePermCode(menuUpdate.permCode());
        this.menuIcon = menuUpdate.menuIcon();
        this.iconType = menuUpdate.iconType();
        this.i18nKey = menuUpdate.i18nKey();
        this.keepAlive = menuUpdate.keepAlive();
        this.isShow = menuUpdate.isShow();
        this.activeMenu = menuUpdate.activeMenu();
        this.orderNum = menuUpdate.orderNum();
        this.isEnabled = menuUpdate.isEnabled();
    }

    /** 空白权限标识统一为 NULL，避免空串撞唯一索引。 */
    private static String normalizePermCode(String permCode) {
        return StrUtil.isBlank(permCode) ? null : permCode.trim();
    }
}
