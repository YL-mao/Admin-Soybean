package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    /** 按配置分组汇总条数；configGroup 非空时模糊匹配分组名。 */
    List<Map<String, Object>> selectGroupList(@Param("configGroup") String configGroup);
}
