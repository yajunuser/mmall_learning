package com.mymmall.service;

import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.Shipping;

public interface ShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse update(Integer userId,Shipping record);

    ServerResponse delete(Integer userId, Integer shippingId);

    ServerResponse select(Integer userId, Integer shippingId);

    ServerResponse list(Integer userId,int pageNum,int pageSize);
}
