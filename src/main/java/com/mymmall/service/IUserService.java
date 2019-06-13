package com.mymmall.service;

import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.User;

/**
 * copy by yajun
 */
public interface IUserService {
    ServerResponse<User> login(String name, String password);

    ServerResponse register(User user);

    ServerResponse checkValid(String str, String type);

    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetRestPassword(String username, String passwordNew, String localToken);

    ServerResponse<String> restPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse isAdmin(User user);
}
