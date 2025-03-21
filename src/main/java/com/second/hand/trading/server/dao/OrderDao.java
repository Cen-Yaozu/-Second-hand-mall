package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.OrderModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
public interface OrderDao extends BaseMapper<OrderModel> {
    // 自定义业务方法
    List<OrderModel> getMyOrder(Long userId);

    List<OrderModel> getAllOrder(int begin, int nums);

    List<OrderModel> getOrderByNumber(String searchValue, int begin, int nums);

    int countAllOrder();

    List<OrderModel> findOrderByIdleIdList(List<Long> idleIdList);
}