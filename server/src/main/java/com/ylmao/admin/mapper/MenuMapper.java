package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> queryMenuCheckArrByRoleId(String roleId);

    /** 多角色合并查询启用菜单，侧栏与鉴权共用。 */
    List<Menu> selectMenusByRoleIds(@Param("roleIds") List<String> roleIds);
}
