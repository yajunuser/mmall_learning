package com.mymmall.controller.portal;

import com.mymmall.common.Const;
import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.User;
import com.mymmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class  CartController {
    @Autowired
    private CartService cartService;

    public ServerResponse list(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //提示需要登录
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户需要请强制登录");
        }
        //接着返回用户改用户的购物车里的东西
        return cartService.list(user.getId());
    }

    /**
     * 添加商品的接口
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Integer productId,Integer count) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户需要请强制登录");
        }
        //把这两个封装到对象上
       return  cartService.add(user.getId(), productId, count);
    }
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Integer productId,Integer count) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户需要请强制登录");
        }
        //把这两个封装到对象上
        return  cartService.update(user.getId(), productId, count);
    }
    //购物车删除产品操作
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户需要请强制登录");
        }
        //把这两个封装到对象上
        return cartService.delete(user.getId(),productIds);
    }

    /**
     * 全选 和全反选的操作接口
     */
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要请强制登录");
        }
        return cartService.selectAllOrUnSelectAll(user.getId(), Const.Cart.CHECKED);
    }
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要请强制登录");
        }
        return cartService.selectAllOrUnSelectAll(user.getId(), Const.Cart.UN_CHECKED);
    }
    /**
     * 单独选和单独不选
     */
    @RequestMapping("select_one.do")
    @ResponseBody
    public ServerResponse selectOne(HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要请强制登录");
        }
        return cartService.selectOneOrUnSelectOne(user.getId(), productId, Const.Cart.CHECKED);
    }
    @RequestMapping("un_select_one.do")
    @ResponseBody
    public ServerResponse unSelectOne(HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要请强制登录");
        }
        return cartService.selectOneOrUnSelectOne(user.getId(), productId, Const.Cart.UN_CHECKED);
    }
    //获取购物车中的商品数量
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse getCartProductCount(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }
        return cartService.getCartProductCount(user.getId());
    }
}
