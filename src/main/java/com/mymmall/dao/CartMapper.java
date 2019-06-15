package com.mymmall.dao;

import com.mymmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    /**
     * 通过用户的id 和商品的id 查找记录
     */
    Cart selectByUserIdAndProductId(@Param(value = "userId") Integer userId, @Param(value = "productId")Integer productId);

    /**
     * 通过用户用户的id获得购物车信息集合
     */
    List<Cart> selectCartListByUserId(Integer userId);

    /**
     * 得到用户购物车是不全选状态的方法
     */
    int getCartAllStatusByUserId(Integer userId);

    /**
     * 删除购物车信息根据用户的id和商品的id
     */
    int deleteCartByUserIdAndProductIdList(@Param(value = "userId")Integer userId, @Param(value = "productList")List<String> productList);

    /**
     * 更改选择状态的
     */
    int checkedOrUnCheckedAllProduct(@Param(value = "userId")Integer userId, @Param(value = "checked")Integer checked);
    /**
     * 单个的修改选中状态
     */
    int checkedOrUnCheckedOneProduct(@Param(value = "userId")Integer userId,@Param(value = "productId")Integer productId ,@Param(value = "checked")Integer checked);

    int getCartProductCount(Integer userId);
}