package com.mymmall.controller.portal;

import com.mymmall.common.Const;
import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.Shipping;
import com.mymmall.pojo.User;
import com.mymmall.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private ShippingService shippingService;

    /**
     * 添加收获地址
     */
    @ResponseBody
    @RequestMapping("add.do")
    public ServerResponse add(HttpSession session, Shipping record) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
//        record.setUserId(user.getId());
       return shippingService.add(user.getId(), record);
    }
    @ResponseBody
    @RequestMapping("update.do")
    public ServerResponse update(HttpSession session,Shipping record) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return shippingService.update(user.getId(),record);
    }
    @ResponseBody
    @RequestMapping("delete.do")
    public ServerResponse delete(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return shippingService.delete(user.getId(),shippingId);
    }
    @ResponseBody
    @RequestMapping("select.do")
    public ServerResponse select(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return shippingService.select(user.getId(),shippingId);
    }
    @ResponseBody
    @RequestMapping("list.do")
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                    @RequestParam(value = "pageNum",defaultValue = "5")int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return shippingService.list(user.getId(),pageNum,pageSize);
    }
}
