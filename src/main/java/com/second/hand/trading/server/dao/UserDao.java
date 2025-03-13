package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<UserModel> {
    // 自定义业务方法
    UserModel userLogin(@Param("accountNumber") String accountNumber, @Param("userPassword") String userPassword);
    
    Long selectByUserName(String nickname);
    
    UserModel selectByAccountNumber(String accountNumber);
    
    List<UserModel> getUserList();
    
    List<UserModel> findUserByList(List<Long> idList);
    
    List<UserModel> getNormalUser(int begin, int nums);
    
    List<UserModel> getBanUser(int begin, int nums);
    
    // 通过账户查找用户
    List<UserModel> getUserByNumber(String searchValue, int mode);
    
    int countNormalUser();
    
    int countBanUser();
}
