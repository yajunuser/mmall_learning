package com.mymmall.service;

import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.Product;

public interface ProductService {
    ServerResponse saveAndUpdateProduct(Product product);

    ServerResponse updateStatus(Integer productId, Integer status);
}
