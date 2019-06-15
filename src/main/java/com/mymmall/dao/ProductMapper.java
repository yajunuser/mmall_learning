package com.mymmall.dao;

import com.mymmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    /**
     * 写一个获取所有商品的sql
     * 分页的话 sql语句不需要添加页面信息参数
     */
    List<Product> selectList();

    /**
     * 商品搜索
     */
    List<Product> selectSearchProductBy(@Param(value = "productName") String productName, @Param(value = "productId")Integer productId);

    /**
     * 实现商品的模糊查询 在某个基础之上
     */
    List<Product> selectSearchProductByKeyWordAndList(@Param(value = "keyWord") String keyWord,@Param(value = "list")  List<Integer> list);
}