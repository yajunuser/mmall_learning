package com.mymmall.service.Impl;

import com.mymmall.common.Const;
import com.mymmall.common.ServerResponse;
import com.mymmall.common.TokenCache;
import com.mymmall.dao.UserMapper;
import com.mymmall.pojo.User;
import com.mymmall.service.IUserService;
import com.mymmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * copy by yajun
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param name
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String name, String password) {
        int count = userMapper.checkUserName(name);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //把密码转换成 Md5 后比较
        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(name, md5password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //把用户密码清除返回给前端
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    /**
     * 用户注册 需要验证邮箱，姓名 需要给用户设置权限 用常量表示 给用户密码MD5加密
     *
     * @param user
     * @return
     */
    @Override
    public ServerResponse register(User user) {
        //检验名字存在不
        ServerResponse<String> response = this.checkValid(user.getUsername(), Const.USSERNAME);
        if (!response.isSuccess()) {
            return response;
        }
        response = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!response.isSuccess()) {
            return response;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密，
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int count = userMapper.insert(user);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 检测用户的名字 和email是不是存在 时时检测
     *
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USSERNAME.equals(type)) {
                int count = userMapper.checkUserName(str);
                if (count != 0) {
                    return ServerResponse.createByErrorMessage("用户已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int count = userMapper.checkEmail(str);
                if (count != 0) {
                    return ServerResponse.createByErrorMessage("邮箱地址已注册过！");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("验证通过");
    }

    /**
     * 获得忘记密码的问题
     */
    @Override
    public ServerResponse selectQuestion(String username) {
        //用检测的方法 检测用户名是不是存在的 存在再开始下一步的操作
        ServerResponse<User> response = this.checkValid(username,Const.USSERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //得到结果如果问题不是空就返回给前端控制器
        String question = userMapper.selectQuestionByUserName(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        //否则问题就是空的：
        return ServerResponse.createByErrorMessage("找回密码的问题为空");
    }

    /**
     * 回答完问题之后 会跳转到修改密码的接口 为了确认时这个用户一直在操作
     * 所以要生成一个token 如果成功了就吧token值传给前端 为下一步的修改密码确认
     * 存token到本地缓存 并且设置有效时间。防止横向越权。
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int count = userMapper.checkAnswer(username, question, answer);
        if (count > 0) {
            String localToken = UUID.randomUUID().toString();
            //防止此token的key有危险 加一个username作为key
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,localToken);
            return ServerResponse.createBySuccess(localToken);
        }
        return ServerResponse.createByErrorMessage("问题答案不正确");
    }
    /**
     * 忘记密码中的重置密码，需要用户的名字 新密码 还有忘记密码的token，
     */
    @Override
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String localToken){
        //判断localtoken如果是空就 返回错误信息
        if(StringUtils.isBlank(localToken)){
            return ServerResponse.createByErrorMessage("token为空或以失效");
        }
        ServerResponse<String> response = this.checkValid(username,Const.USSERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token不存在或已失效，请从新验证问题");
        }
        if(StringUtils.equals(localToken,token)){
            //如果相等的话 就验证通过 修改密码
            String MD5PasswordNew = MD5Util.MD5EncodeUtf8(passwordNew);
            int count = userMapper.restPassword(username, MD5PasswordNew);
            if (count > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token值不正确，请从新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    /**
     * 登录状态下的修改密码
     */
    @Override
    public ServerResponse<String> restPassword(String passwordOld, String passwordNew, User user) {
        //检测老密码是不是正确 密码不是唯一值 为了防止查出很多来 要加入用户的id
        int resultCount = userMapper.checkPasswordOld(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码不正确");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("更改密码成功");
        }
        return ServerResponse.createByErrorMessage("更改密码失败");
    }

    /**
     * 登录状态时的修改信息
     */
    @Override
    public ServerResponse<User> updateInformation(User user) {
        //名字不能更改 所以名字不需要测试了
        //但是email 看会不会跟别人的重复，这事不行的，还需要用户的id 来被标记确认不是跟自己的之前的email重复
        int resultCoun = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCoun > 0) {
            return ServerResponse.createByErrorMessage("email已被使用，请更换新的Email地址");
        }
        //纠结这里直接把user 直接塞进去不行吗 为什么还要腾出来
//        User newUser = new User();
//        newUser.setAnswer(user.getAnswer());
//        newUser.setEmail(user.getEmail());
//        newUser.setPhone(user.getPhone());
//        newUser.setQuestion(user.getQuestion());
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新成功", user);
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    /**
     * 显示用户信息
     * 把用户密码弄空返回回去
     */
    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户信息不存在");
        }
        //把密码制空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
    /**
     * 做一个验证user 是不是管理员的方法
     */
    @Override
    public ServerResponse isAdmin(User user){
        if (Const.Role.ROLE_ADMIN == user.getRole()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}