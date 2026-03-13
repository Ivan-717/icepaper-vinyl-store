package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SystemConfigMapper {

    //获取系统配置
    @Select("SELECT * FROM system_config LIMIT 1")
    SystemConfig getConfig();

    //更新店铺状态
    @Update("UPDATE system_config SET shop_status=#{shopStatus},update_time=NOW()")
    void updateShopStatus(@Param("shopStatus") Integer shopStatus);
}
