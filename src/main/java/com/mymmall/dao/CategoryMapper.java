package com.mymmall.dao;

import com.mymmall.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    /**
     * 通过ParentId 查找符合条件的闪频信息
     */
    List<Category> selectParallelCategoryByParentId(Integer parentId);
}