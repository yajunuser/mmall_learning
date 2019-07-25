package com.mymmall.service.Impl;

import com.google.common.collect.Lists;
import com.mymmall.common.Const;
import com.mymmall.common.ServerResponse;
import com.mymmall.dao.CartMapper;
import com.mymmall.dao.OrderMapper;
import com.mymmall.dao.ProductMapper;
import com.mymmall.pojo.Cart;
import com.mymmall.pojo.Order;
import com.mymmall.pojo.OrderItem;
import com.mymmall.pojo.Product;
import com.mymmall.service.OrderService;
import com.mymmall.util.BigDecimalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrederServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;


    /**
     * 创建订单
     */
    public ServerResponse create(Integer userId, Integer shippingId) {
        //需要两个参数，其他的参数从购物车中获的被勾选的数据
        List<Cart> cartList = cartMapper.selectChechedCartByUserId(userId);
        //订单的总价  先知道买了都少个产品 每样买了几个 产品的价格 总的产品价格
        ServerResponse<List<OrderItem>> serverResponse = this.getCartOrderItem(userId, cartList);
        //如果返回的是失败的则 把返回的信息提供给前端
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItems = serverResponse.getData();
        //获的订单的总价
        BigDecimal payment = this.getPayment(orderItems);
        //生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);

        return ServerResponse.createByError();

    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        //先生成一个订单号
        Order order = new Order();
        long orderNo = this.generrateOrederNO();
        order.setOrderNo(orderNo);
        order.setShippingId(shippingId);
        order.setUserId(userId);
        order.setPayment(payment);
        order.setPostage(0);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPaymentType(Const.PayPlatformEnum.ALIPAY.getCode());
        int count = orderMapper.insert(order);
        if (count>0) {
            return order;
        }
        return null;
    }

    private long generrateOrederNO() {
        long currenttimr = System.currentTimeMillis();
        return currenttimr+ currenttimr%9;
    }

    private BigDecimal getPayment(List<OrderItem> orderItems) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItems) {
            BigDecimalUtil.add(payment.doubleValue(), orderItem.getQuantity().doubleValue());
        }
        return payment;
    }

    //获得产品
    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList) {
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
