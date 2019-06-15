package com.mymmall.service;

import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.Product;

public interface ProductService {
    ServerResponse saveAndUpdateProduct(Product product);

    ServerResponse updateStatus(Integer productId, Integer status);

    ServerResponse getProductDetail(Integer productId);

    ServerResponse getProductDetailList(int pageNum, int pageSize);

    ServerResponse searchProductList(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse detail(Integer productId);

    ServerResponse list(String keyWord, Integer categoryId, int pageNum, int pageSize,String orderBy);
}
