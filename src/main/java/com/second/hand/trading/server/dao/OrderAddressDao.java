package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.OrderAddressModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderAddressDao extends BaseMapper<OrderAddressModel> {
    // 自定义业务方法
    OrderAddressModel selectByOrderId(Long orderId);
}