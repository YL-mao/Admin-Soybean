package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.Menu;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MenuVo {

    public record MenuListVo(String menuId, String parentId, String menuPath, String menuName, String menuDesc,
                             Integer menuType, String routeName, String routePath, String routeComp, String routeQuery,
                             String menuHref, Integer isBlank, String permCode, String menuIcon, Integer iconType,
                             String i18nKey, Integer keepAlive, Integer isShow, String activeMenu,
                             Integer orderNum, Integer isEnabled,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createTime) {

        public static MenuListVo from(Menu menu) {
            return new MenuListVo(menu.getMenuId(), menu.getParentId(), menu.getMenuPath(), menu.getMenuName(),
                    menu.getMenuDesc(), menu.getMenuType(), menu.getRouteName(), menu.getRoutePath(),
                    menu.getRouteComp(), menu.getRouteQuery(), menu.getMenuHref(), menu.getIsBlank(),
                    menu.getPermCode(), menu.getMenuIcon(), menu.getIconType(), menu.getI18nKey(),
                    menu.getKeepAlive(), menu.getIsShow(), menu.getActiveMenu(), menu.getOrderNum(),
                    menu.getIsEnabled(), menu.getCreateTime());
        }
    }

    /** 角色授权树平铺节点，含勾选回显字段 checkArr。 */
    public record MenuCheckVo(String menuId, String parentId, String menuName, String checkArr) {

        public static MenuCheckVo from(Menu menu) {
            return new MenuCheckVo(
                    menu.getMenuId(),
                    menu.getParentId(),
                    menu.getMenuName(),
                    menu.getCheckArr()
            );
        }
    }

    /** 上级菜单下拉树节点。 */
    public record MenuParentVo(String menuId, String parentId, String menuName, String menuPath) {

        public static MenuParentVo from(Menu menu) {
            return new MenuParentVo(
                    menu.getMenuId(),
                    menu.getParentId(),
                    menu.getMenuName(),
                    menu.getMenuPath()
            );
        }
    }
}
