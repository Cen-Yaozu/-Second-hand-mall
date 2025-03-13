package com.second.hand.trading.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.second.hand.trading.server.dao.OrderAddressDao;
import com.second.hand.trading.server.model.OrderAddressModel;
import com.second.hand.trading.server.service.OrderAddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderAddressServiceImpl extends ServiceImpl<OrderAddressDao, OrderAddressModel> implements OrderAddressService {

    @Resource
    private OrderAddressDao orderAddressDao;

    /**
     * 为订单新增地址信息
     * @param orderAddressModel
     * @return
     */
    public boolean addOrderAddress(OrderAddressModel orderAddressModel){
        return save(orderAddressModel);
    }

    /**
     * 更新订单的地址信息，未验证用户身份
     * @param orderAddressModel
     * @return
     */
    public boolean updateOrderAddress(OrderAddressModel orderAddressModel){
        orderAddressModel.setOrderId(null);
        return updateById(orderAddressModel);
    }

    /**
     * 获取订单的地址信息
     * orderId建索引
     * @param orderId
     * @return
     */
    public OrderAddressModel getOrderAddress(Long orderId){
        return orderAddressDao.selectByOrderId(orderId);
    }
}
