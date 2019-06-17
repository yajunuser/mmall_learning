package com.mymmall.service.Impl;

import com.google.common.collect.Lists;
import com.mymmall.common.Const;
import com.mymmall.common.ServerResponse;
import com.mymmall.dao.OrderMapper;
import com.mymmall.dao.ProductMapper;
import com.mymmall.pojo.Cart;
import com.mymmall.pojo.OrderItem;
import com.mymmall.pojo.Product;
import com.mymmall.service.OrderService;
import com.mymmall.util.BigDecimalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrederServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 创建订单
     */
    public ServerResponse create(Integer userId, Integer shippingId) {
        //需要两个参数，其他的参数从购物车中获的被勾选的数据
        List<Cart> cartList = orderMapper.selectCheckedByUserId(userId);
        //订单的总价  先知道买了都少个产品 每样买了几个 产品的价格 总的产品价格
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        //todo 未完待续，还没有写完想全程看看
        return  ServerResponse.createByError();

    }

    //获得产品
    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItems = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
            //获得商品的详情
            for (Cart cartitem : cartList) {
                OrderItem orderItem = new OrderItem();
                Product product = productMapper.selectByPrimaryKey(cartitem.getProductId());
                //产看商品的销售状态
                if (Const.productStatus.ON_LINE.getCode() != product.getStatus()) {
                    return ServerResponse.createByErrorMessage("产品" + product.getName() + "已下架");
                }
                if (cartitem.getQuantity() > product.getStock()) {
                    return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
                }
                orderItem.setUserId(userId);
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setCurrentUnitPrice(product.getPrice());
                orderItem.setQuantity(cartitem.getQuantity());
                orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartitem.getQuantity()));
                orderItems.add(orderItem);
            }
            return ServerResponse.createBySuccess(orderItems);
        }

}
