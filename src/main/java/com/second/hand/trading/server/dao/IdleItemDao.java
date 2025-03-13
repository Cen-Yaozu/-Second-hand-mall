package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.IdleItemModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IdleItemDao extends BaseMapper<IdleItemModel> {
    // 自定义业务方法
    List<IdleItemModel> getAllIdleItem(Long userId);

    int countIdleItem(String findValue);

    int countIdleItemByLable(int idleLabel);

    int countIdleItemByStatus(int status);

    List<IdleItemModel> findIdleItem(String findValue, int begin, int nums);

    List<IdleItemModel> findIdleItem1(String findValue, int status, int begin, int nums);

    List<IdleItemModel> findIdleItemByLable(int idleLabel, int begin, int nums);

    List<IdleItemModel> getIdleItemByStatus(int status, int begin, int nums);
}