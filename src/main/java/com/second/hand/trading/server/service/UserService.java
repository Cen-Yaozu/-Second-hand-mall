package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.vo.PageVo;
import com.second.hand.trading.server.vo.ResultVo;

public interface UserService {

    /**
     * 获取某个用户的公开信息
     * @param id
     * @return
     */
    UserModel getUser(Long id);

    /**
     * 登录接口
     * @param accountNumber
     * @param userPassword
     * @return
     */
    UserModel userLogin(String accountNumber, String userPassword);

    /**
     * 注册接口
     * @param userModel
     * @return
     */
    boolean userSignIn(UserModel userModel);

    /**
     * 更新用户信息
     * @param userModel
     * @return
     */
    boolean updateUserInfo(UserModel userModel);

    /**
     * 修改密码
     * @param newPassword
     * @param oldPassword
     * @param id
     * @return
     */
    boolean updatePassword(String newPassword, String oldPassword,Long id);

    PageVo<UserModel> getUserByStatus(int status, int page , int nums);


    /**
     * 通过用户名查询用户id
     *
     * */
    Long getUserId(String nickname);


    /*
    * 通过账号查询用户id
    *
    * */

    UserModel getUserByaccountNumber(String accountNumber);


    /**
     * 通过用户的账号查找用户信息
     *
     * @return*/

    PageVo<UserModel> getUserByNumber(String searchValue, int mode);

     /*
     * 登录操作
     *
     * */

    ResultVo<UserModel> login(String accountNumber, String userPassword);
}
