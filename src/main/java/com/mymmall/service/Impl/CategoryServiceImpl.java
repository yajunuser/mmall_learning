package com.mymmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mymmall.common.ServerResponse;
import com.mymmall.dao.CategoryMapper;
import com.mymmall.pojo.Category;
import com.mymmall.service.CategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;


@Service
public class CategoryServiceImpl implements CategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加商品 需要商品的节点id 和 商品的名字
     */
    @Override
    public ServerResponse addCategory(Integer parentId, String categoryName) {
        if(parentId==null || StringUtils.isEmpty(categoryName)){
            return ServerResponse.createByErrorMessage("商品参数错误");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int resultCount = categoryMapper.insert(category);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("商品添加成功");
        }
        return ServerResponse.createByErrorMessage("商品提交失败");
    }

    /**
     *修改商品的信息通过id
     */
    @Override
    public ServerResponse updateCategory(Integer categoryId, String categoryName) {
        //先判空 看id是否存在 名字是不是空的
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category == null){
            return ServerResponse.createByErrorMessage("商品的id不存在");
        }
        if (StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("商品的名字不能是空的");
        }
        //然后准备update
        Category newCategory = new Category();
        newCategory.setId(categoryId);
        newCategory.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(newCategory);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("修改商品成功");
        }
        return ServerResponse.createByErrorMessage("修改商品失败");
    }

    /**
     * 获得商品的子节点所有信息，
     * 需要的参数，一个categoryId
     */
    public ServerResponse getParallelCategory(Integer categoryId) {
        //判断id是否存在
//        if (categoryId == 0) {
//            return ServerResponse.createBySuccess(categoryMapper.selectParallelCategoryByParentId(categoryId));
//        }
//        Category category = categoryMapper.selectByPrimaryKey(categoryId);
//        if(category == null){
//            return ServerResponse.createByErrorMessage("商品的id节点不存在或不正确");
//        }
        //用这个id 当作节点id 取找
        List<Category> categoryList = categoryMapper.selectParallelCategoryByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id 和其孩子节点的id
     */
    @Override
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
        if (categoryId == null) {
            return ServerResponse.createByErrorMessage("节点的id不能为空");
        }
        Set<Category> categorySet = Sets.newHashSet();
        findchildCategory(categorySet, categoryId);

        List<Integer> categoryItem = Lists.newArrayList();
        for (Category category : categorySet) {
            categoryItem.add(category.getId());
        }
        return ServerResponse.createBySuccess(categoryItem);
    }

    /**
     * 写一个递归的方法 获取我们的节点和根节点下的所有分类
     */
    private Set<Category> findchildCategory(Set<Category> categorySet,Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            //因为要返回所有的类 所有如果此id对应的商品有就加入
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectParallelCategoryByParentId(categoryId);
        for (Category category1 : categoryList) {
            findchildCategory(categorySet, category1.getId());
        }
        return categorySet;
    }
}
