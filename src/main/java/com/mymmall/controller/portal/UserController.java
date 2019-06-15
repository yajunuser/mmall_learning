package com.mymmall.controller.portal;

import com.mymmall.common.Const;
import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.User;
import com.mymmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * copy by yajun
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param name
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String name, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(name, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    /**
     * 登出功能
     * @param session
     * @return
     */
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册功能
     *
     * @param user 作为信息的承载
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 检验邮箱和用户名是否存在 恶意用户调用注册接口（应该是防止调用接口注册，让服务器瘫痪）
     * 实时的检测 用户名和邮箱地址
     *
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * @return 获得用户的登录信息
     * @param session
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 获得忘记密码找回的问题
     */
    @RequestMapping(value = "select_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> selectQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * @param username 回答完问题之后 会跳转到修改密码的接口 为了确认时这个用户一直在操作
     * @param question 所以要生成一个token 如果成功了就吧token值传给前端 为下一步的修改密码确认
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码中的重置密码
     */
    @RequestMapping(value = "forget_rest_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forgetRestPassword(String username, String passwordNew, String localToken) {
        return iUserService.forgetRestPassword(username, passwordNew, localToken);
    }

    /**
     * 登录状态下修改密码
     */
    @RequestMapping(value = "rest_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse restPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.restPassword(passwordOld, passwordNew, user);
    }

    /**
     * 登录状态时的修改信息
     * @param session 获取登录者的信息
     * @param user    作为更新信息的一个承载！从前端穿过来
     * @return 返回更新后的user信息 把密码制空
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateInformation(HttpSession session, User user) {
        //判断用户是否已经登录了
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //要修改的名字和id 是不能变的，而且修改的信息中不含有名字和id 那就从session里取
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse response = iUserService.updateInformation(user);
        //如果是对的就应该 更新我们的session
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }


    /**
     * 获得用户的信息，
     * @param session 读取session中是否有登录
     * @return 返回置空密码的用户对象
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录需要强制登陆status =10");
        }
        //直接返回这个方法的结果
        return iUserService.getInformation(user.getId());
    }
}
