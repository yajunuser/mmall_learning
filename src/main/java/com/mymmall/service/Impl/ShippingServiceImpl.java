package com.mymmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.dao.ShippingMapper;
import com.mymmall.pojo.Shipping;
import com.mymmall.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 添加地址
     */
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int resultCount = shippingMapper.insertSelective(shipping);
        if (resultCount > 0) {
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    /**
     * 修改地址
     */
    public ServerResponse update(Integer userId,Shipping record) {
        //从新设置下userId 防止横向越权。。。
        record.setUserId(userId);
        int resultCount = shippingMapper.updateByShipping(record);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("修改地址成功");
        }
        return ServerResponse.createByErrorMessage("修改地址失败");
    }

    /**
     * 删除地址
     */
    public ServerResponse delete(Integer userId, Integer shippingId) {
        int resultCount = shippingMapper.deleteShippingByUserIdAndShippingId(userId, shippingId);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createBySuccess("删除地址失败");
    }

    /**
     * 查询地址详情
     */
    public ServerResponse select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(userId, shippingId);
        if(shipping == null){
            return ServerResponse.createByErrorMessage("无法查询该地址，可能已被删除");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    /**
     * 返回地址列表
     */
    public ServerResponse list(Integer userId,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.getShippingList(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
