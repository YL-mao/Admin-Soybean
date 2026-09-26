package com.ylmao.admin.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Menu {
    /**
     * 菜单编号
     * */
    private String id;

    /**
     * 父节点
     * */
    private String parentId;

    /**
     * 标题
     * */
    private String title;

    /**
     * 菜单类型 类型   0：目录   1：菜单   2：按钮
     * */
    private Integer type;

    /**
     * 打开方式：0 本页，1 新窗口（对应 is_blank）
     * */
    private Integer isBlank;

    /**
     * 图标
     * */
    private String icon;

    /**
     * 跳转路径
     * */
    private String href;

    /**
     * 子菜单
     * */
    private List<Menu> children = new ArrayList<>();


    /**
     * 计算列 提供给前端组件
     * */
    private String checkArr = "0";

    @Override
    public String toString() {
        return "Menu{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", isBlank=" + isBlank +
                ", icon='" + icon + '\'' +
                ", href='" + href + '\'' +
                ", children=" + children +
                ", checkArr='" + checkArr + '\'' +
                '}';
    }

}
