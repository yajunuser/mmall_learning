package com.mymmall.service;

import com.mymmall.common.ServerResponse;

public interface CategoryService {

    ServerResponse addCategory(Integer parentId, String categoryName);

    ServerResponse updateCategory(Integer categoryId, String categoryName);

    ServerResponse getParallelCategory(Integer categoryId);

    ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
