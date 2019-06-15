package com.mymmall.service;

import com.mymmall.common.ServerResponse;

public interface CartService {
    ServerResponse add(Integer userId, Integer productId, Integer count);

    ServerResponse update(Integer userId, Integer productId,Integer count);

    ServerResponse delete(Integer userId, String productIds);

    ServerResponse list(Integer userId);

    ServerResponse selectAllOrUnSelectAll(Integer userId,Integer checked);

    ServerResponse selectOneOrUnSelectOne(Integer userId, Integer productId, Integer checked);

    ServerResponse getCartProductCount(Integer userId);
}
