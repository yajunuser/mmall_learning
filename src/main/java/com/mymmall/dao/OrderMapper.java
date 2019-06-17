package com.mymmall.dao;

import com.mymmall.pojo.Cart;
import com.mymmall.pojo.Order;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    /**
     * 查找特定用户购物车中被勾选的购物车信息
     */
    List<Cart> selectCheckedByUserId(Integer userId);
}