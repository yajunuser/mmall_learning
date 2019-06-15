package com.mymmall.dao;

import com.mymmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    /**
     * 通过用户id 和地址的id修改 地址
     */
    int updateByShipping(Shipping record);

    /**
     * 删除通过用户id 和地址id
     */
    int deleteShippingByUserIdAndShippingId(@Param(value = "userId") Integer userId, @Param(value = "shippingId") Integer shippingId);

    /**
     * c查找对像
     */
    Shipping selectByShippingIdAndUserId(@Param(value = "userId")Integer userId, @Param(value = "shippingId")Integer shippingId);

    /**
     * 获得某用户的地址列表 带分页的
     */
    List<Shipping> getShippingList(Integer userId);
}