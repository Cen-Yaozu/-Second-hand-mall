package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.AdminModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminDao extends BaseMapper<AdminModel> {
    // 自定义业务方法
    AdminModel login(@Param("accountNumber") String accountNumber, @Param("adminPassword") String adminPassword);

    List<AdminModel> getList(int begin, int nums);

    int getCount();

    boolean updateUser(Long id, String nickname, String password);
}
