package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MenuDto {

    public record MenuList(
            @Size(max = 64, message = "菜单名称参数不合法") String menuName,
            @Size(max = 128, message = "权限标识参数不合法") String permCode,
            @Size(max = 255, message = "路由路径参数不合法") String routePath
    ) {
    }

    public record MenuInsert(
            String parentId,
            @NotBlank(message = "菜单名称不能为空") String menuName,
            String menuDesc,
            @NotNull(message = "菜单类型参数不合法") @Min(value = 0, message = "菜单类型参数不合法") @Max(value = 2, message = "菜单类型参数不合法")
            Integer menuType,
            String routeName,
            String routePath,
            String routeComp,
            String routeQuery,
            String menuHref,
            Integer isBlank,
            String permCode,
            String menuIcon,
            Integer iconType,
            String i18nKey,
            Integer keepAlive,
            Integer isShow,
            String activeMenu,
            @NotNull(message = "菜单排序不能为空") Integer orderNum,
            @NotNull(message = "菜单状态参数不合法") @Min(value = 0, message = "菜单状态参数不合法") @Max(value = 1, message = "菜单状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record MenuUpdate(
            @NotBlank(message = "菜单ID不能为空") String menuId,
            String parentId,
            @NotBlank(message = "菜单名称不能为空") String menuName,
            String menuDesc,
            @NotNull(message = "菜单类型参数不合法") @Min(value = 0, message = "菜单类型参数不合法") @Max(value = 2, message = "菜单类型参数不合法")
            Integer menuType,
            String routeName,
            String routePath,
            String routeComp,
            String routeQuery,
            String menuHref,
            Integer isBlank,
            String permCode,
            String menuIcon,
            Integer iconType,
            String i18nKey,
            Integer keepAlive,
            Integer isShow,
            String activeMenu,
            @NotNull(message = "菜单排序不能为空") Integer orderNum,
            @NotNull(message = "菜单状态参数不合法") @Min(value = 0, message = "菜单状态参数不合法") @Max(value = 1, message = "菜单状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "菜单ID不能为空") String menuId,
            @NotNull(message = "菜单状态参数不合法") @Min(value = 0, message = "菜单状态参数不合法") @Max(value = 1, message = "菜单状态参数不合法")
            Integer isEnabled
    ) {
    }

    /** 角色授权保存：角色 ID 必填，菜单 ID 列表允许为空表示清空授权。 */
    public record RoleMenuSave(
            @NotBlank(message = "角色ID不能为空") String roleId,
            String menuIds
    ) {
    }
}
