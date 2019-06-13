package com.mymmall.dao;

import com.mymmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 检测名字存在？
     * @param username
     * @return
     */
    int checkUserName(String username);

    /**
     * 展示登录者的信息
     * @param username
     * @param password
     * @return
     */

    User selectLogin(@Param(value = "username") String username,@Param(value = "password") String password);

    /**
     * 检测用户邮箱
     * @param email
     * @return
     */
    int checkEmail(String email);

    /**
     * 根据用户的名字 查找找回密码的问题
     */
    String selectQuestionByUserName(String username);

    /**
     * 检查问题的正确与否
     */
    int checkAnswer(@Param(value = "username")String username, @Param(value = "question")String question, @Param(value = "answer")String answer);

    /**
     * 修改密码通过名字
     */
     int restPassword(@Param(value = "username")String username,@Param(value = "passwordNew")String passwordNew);

    /**
     * 检测老密码的正确性
     */
    int checkPasswordOld(@Param(value = "passwordOld")String passwordOld, @Param(value = "userId")Integer userId);

    /**
     * 根据id 看email 是不是重复了或者被其他人用了 如果跟自己的重复没关系
     */
    int checkEmailByUserId(@Param(value = "email")String email, @Param(value = "userId")Integer userId);
}