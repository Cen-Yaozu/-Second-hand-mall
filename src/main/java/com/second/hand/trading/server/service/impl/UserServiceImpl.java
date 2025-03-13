package com.second.hand.trading.server.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.vo.PageVo;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;



@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    /**
     * 查询一个用户的公开信息
     * @param id
     * @return
     */
    public UserModel getUser(Long id){
        return userDao.selectById(id);
    }

    /**
     * 登录，安全问题未解决
     * @param accountNumber
     * @param userPassword
     * @return
     */
    public UserModel userLogin(String accountNumber, String userPassword){
        // 使用MyBatis-Plus的条件构造器
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_number", accountNumber)
                   .eq("user_password", userPassword);
        return userDao.selectOne(queryWrapper);
    }

    /**
     *注册
     * @param userModel
     * @return
     */
    public boolean userSignIn(UserModel userModel){
        // 设置默认注册时间
        if (userModel.getSignInTime() == null) {
            userModel.setSignInTime(new Timestamp(System.currentTimeMillis()));
        }
        // 对密码进行加密
        if (userModel.getUserPassword() != null) {
            if ("".equals(userModel.getUserPassword())) {
                // 可以选择抛出自定义异常或者给默认密码等其他处理方式
                throw new IllegalArgumentException("User password cannot be null");
            }else {
                // 对密码进行加盐方法的加密
                String salt = RandomUtil.randomString(6);
                SecureUtil.sha256(userModel.getUserPassword() + salt);
                userModel.setUserSalt(salt);
                userModel.setUserPassword(SecureUtil.sha256(userModel.getUserPassword() + salt));
            }
        } else {
            // 可以选择抛出自定义异常或者给默认密码等其他处理方式
            throw new IllegalArgumentException("User password cannot be null");
        }

        // 设置用户默认状态
        if (userModel.getUserStatus() == null) {
            Integer status = 0;
            userModel.setUserStatus(status.byteValue());
        }

        // 设置默认头像
        if (userModel.getAvatar() == null || "".equals(userModel.getAvatar())) {
            userModel.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        }

        return userDao.insert(userModel) == 1;
    }

    /**
     *修改用户公开信息，未验证用户身份
     * @param userModel
     * @return
     */
    public boolean updateUserInfo(UserModel userModel){
        return userDao.updateById(userModel) == 1;
    }

    /**
     * 修改密码
     * @param newPassword
     * @param oldPassword
     * @param id
     * @return
     */
    public boolean updatePassword(String newPassword, String oldPassword, Long id){
        // 使用MyBatis-Plus的条件构造器
        LambdaUpdateWrapper<UserModel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserModel::getId, id)
                    .eq(UserModel::getUserPassword, oldPassword)
                    .set(UserModel::getUserPassword, newPassword);
        
        return userDao.update(null, updateWrapper) == 1;
    }

    public PageVo<UserModel> getUserByStatus(int status, int page, int nums){
        // 使用MyBatis-Plus的分页查询
        IPage<UserModel> pageParam = new Page<>(page, nums);
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        
        if(status == 0){
            queryWrapper.isNull("user_status").or().eq("user_status", 0);
        } else {
            queryWrapper.eq("user_status", 1);
        }
        
        IPage<UserModel> result = userDao.selectPage(pageParam, queryWrapper);
        
        // 处理密码字段
        if(status == 0){
            result.getRecords().forEach(user -> {
                if (user.getUserSalt() != null && !user.getUserSalt().isEmpty()){
                    user.setUserPassword("密码已加密，可直接修改");
                }
            });
        }
        
        return new PageVo<>(result.getRecords(), (int)result.getTotal());
    }


    /**
     * 通过用户的姓名查询用户的id
     *
     * */
    @Override
    public Long getUserId(String nickname) {
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").eq("nickname", nickname);
        UserModel userModel = userDao.selectOne(queryWrapper);
        return userModel != null ? userModel.getId() : null;
    }

    @Override
    public UserModel getUserByaccountNumber(String accountNumber) {
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_number", accountNumber);
        return userDao.selectOne(queryWrapper);
    }


    /**
     * 通过用户账号查询用户信息
     *
     * @return*/
    @Override
    public PageVo<UserModel> getUserByNumber(String searchValue, int mode) {
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        
        if(mode == 0) { // 假设mode-1=0表示正常用户
            queryWrapper.like("account_number", searchValue)
                      .and(wrapper -> wrapper.isNull("user_status").or().eq("user_status", 0));
        } else { // 假设mode-1=1表示禁用用户
            queryWrapper.like("account_number", searchValue)
                      .eq("user_status", 1);
        }
        
        List<UserModel> list = userDao.selectList(queryWrapper);
        return new PageVo<>(list, list.size());
    }

    @Override
    public ResultVo login(String accountNumber, String userPassword) {

        // 用户名或者密码为空
        if(accountNumber.equals("") || userPassword.equals("")){
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }

        // 获取数据库中的用户密码信息
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_number", accountNumber);
        UserModel userModel = userDao.selectOne(queryWrapper);
        
        if (userModel == null){
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }

        String userPassword1 = userModel.getUserPassword();
        String userSalt = userModel.getUserSalt();

        // 对从前端获取的密码进行加密
        String s = SecureUtil.sha256(userPassword + userSalt);

        // 与数据库密码比较
        if (!userPassword1.equals(s)){
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }

        if(userModel.getUserStatus()!=null && userModel.getUserStatus().equals((byte) 1)){
            return ResultVo.fail(ErrorMsg.ACCOUNT_Ban);
        }
        return ResultVo.success(userModel);
    }
}
