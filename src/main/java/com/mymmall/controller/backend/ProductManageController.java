package com.mymmall.controller.backend;

import com.mymmall.common.Const;
import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.Product;
import com.mymmall.pojo.User;
import com.mymmall.service.IUserService;
import com.mymmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 后台的商品管理模块
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ProductService productService;

    /**
     * 后台保存商品的接口
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //在这里需要强制登录下
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要强制登录");
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            //说明是管理员 可以做对应的操作
            return productService.saveAndUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("权限不够，需要管理员身份");
        }
    }

    /**
     * 修改商品的销售状态
     */
    @RequestMapping("update_status.do")
    @ResponseBody
    public ServerResponse updateStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //在这里需要强制登录下
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要强制登录");
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            //说明是管理员 可以做对应的操作
            return productService.updateStatus(productId,status);
        } else {
            return ServerResponse.createByErrorMessage("权限不够，需要管理员身份");
        }
    }
}
