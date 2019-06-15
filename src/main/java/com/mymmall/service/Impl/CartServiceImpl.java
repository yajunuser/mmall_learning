package com.mymmall.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mymmall.common.Const;
import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.dao.CartMapper;
import com.mymmall.dao.ProductMapper;
import com.mymmall.pojo.Cart;
import com.mymmall.pojo.Product;
import com.mymmall.pojo.User;
import com.mymmall.service.CartService;
import com.mymmall.util.BigDecimalUtil;
import com.mymmall.util.PropertiesUtil;
import com.mymmall.vo.CartProductVo;
import com.mymmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加对象
     */
    public ServerResponse add(Integer userId, Integer productId,Integer count) {
        if (userId == null || productId == null) {
            //参数错误
            return ServerResponse.createByCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart resultCount = cartMapper.selectByUserIdAndProductId(userId, productId);
        //如果结果是大于0的 说明有这个消失的记录只需要在count上加上添加的数量
        if (resultCount != null) {
            resultCount.setQuantity(count+resultCount.getQuantity());
            cartMapper.updateByPrimaryKey(resultCount);
        }else{
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setChecked(Const.Cart.CHECKED);
            cart.setQuantity(count);
            cartMapper.insert(cart);
        }
        CartVo cartVo = new CartVo();
        cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     *  删除产品的操做
     * @param userId
     * @param productIds
     * @return
     */
    public ServerResponse delete(Integer userId, String productIds) {
        if (userId == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //guava的一个方法 把字符串用逗号分割成字符串集合
        List<String> productIdList = Splitter.on(".").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)) {
            return ServerResponse.createByCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteCartByUserIdAndProductIdList(userId, productIdList);
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }
    /**
     * 更新库存
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse update(Integer userId, Integer productId,Integer count){
        if (userId == null || productId == null) {
            //参数错误
            return ServerResponse.createByCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //如果count 不为空
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if(cart != null){
            cart.setQuantity(count);

        }
        cartMapper.updateByPrimaryKey(cart);
        CartVo cartVo = new CartVo();
        cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }
    // 封装一个购物车的返回对象
    //先要知道，购物车上要显示什么东西
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        //获得登录者购物车里的消息
        List<Cart> cartList = cartMapper.selectCartListByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //这里要把每一条购物车信息转成vo
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                //购物车中的消息
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());
//                cartProductVo.setQuantity(cart.getQuantity());
                //这是设置商品的信息的
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cart.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //此商品的价格总和 这就需要用商品的个数乘单价了
                    BigDecimal totalPrice = BigDecimalUtil.mul(cart.getQuantity().doubleValue(), product.getPrice().doubleValue());
                    cartProductVo.setProductTotalPrice(totalPrice);
                    cartProductVo.setProductChecked(cart.getChecked());
                }
//                if(product == null){
//                    double price = 0;
//                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),price);
//                }
                if(cart.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    /**
     * 全选或者全反选
     */

    public ServerResponse selectAllOrUnSelectAll(Integer userId,Integer checked) {
        cartMapper.checkedOrUnCheckedAllProduct(userId, checked);
        return this.list(userId);
    }

    /**
     * 单选 和单独不选
     */
    public ServerResponse selectOneOrUnSelectOne(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUnCheckedOneProduct(userId, productId, checked);
        return this.list(userId);
    }

    public ServerResponse getCartProductCount(Integer userId) {
        if (userId == null) {
            return  ServerResponse.createBySuccess(0);
        }
         int count = cartMapper.getCartProductCount(userId);
        return ServerResponse.createBySuccess(count);
    }
    /**
     * 查询用户的购物车
     * @param userId
     * @return
     */
    public ServerResponse list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    //写一个判断一个用户在购物车的商品是不是全选状态
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.getCartAllStatusByUserId(userId)==0;
    }
}
