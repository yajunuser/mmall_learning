package com.mymmall.controller.backend;

import com.mymmall.common.Const;
import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.User;
import com.mymmall.service.CategoryService;
import com.mymmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    /**
     * 管理员登陆接口
     *
     * @param username 账号
     * @param password 密码
     * @return 返回User信息 记得把对象存入session
     */
    @ResponseBody
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            //说明这里是管理员
            if (user.getRole().equals(Const.Role.ROLE_ADMIN)) {
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            }
            return ServerResponse.createByErrorMessage("您的权限不够");
        }
        return response;
    }
}
