package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {
    //查询用户的所有地址
    @Select("SELECT * FROM address WHERE user_id=#{userId} Order by is_default ASC ,id ASC")
    List<Address> findByUserId(@Param("userId") Long userId);

    //根据ID查询地址
    @Select("SELECT * FROM address WHERE id=#{id}")
    Address findById(@Param("id") Long id);

    //新增地址
    @Insert("INSERT INTO address (user_id, receiver, phone, province, city, district, detail, is_default, create_time, update_time) " +
            "VALUES (#{userId}, #{receiver}, #{phone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Address address);

    //更新地址
    @Update("UPDATE address SET receiver = #{receiver}, phone = #{phone}, province = #{province}, " +
            "city = #{city}, district = #{district}, detail = #{detail}, is_default = #{isDefault}, update_time = NOW() " +
            "WHERE id = #{id}")
    void update(Address address);

    //删除地址
    @Delete("DELETE FROM address WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    //将所有用户的is_default设为0(默认地址）
    @Update("UPDATE address SET is_default=0 where user_id=#{userId}")
    void resetDefault(@Param("userId") Long userId);

}

