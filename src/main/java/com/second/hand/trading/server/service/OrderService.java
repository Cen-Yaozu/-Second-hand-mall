package com.second.hand.trading.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.second.hand.trading.server.model.FavoriteModel;
import com.second.hand.trading.server.model.OrderModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.model.vo.OrderVo;
import com.second.hand.trading.server.vo.PageVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface OrderService extends IService<OrderModel> {

    /**
     * 新增订单
     * @param orderModel
     * @return
     */
    boolean addOrder(OrderModel orderModel);

    /**
     * 获取订单信息
     * @param id
     * @return
     */
    OrderModel getOrder(Long id);

    // 通过订单编号获取订单

    PageVo<OrderModel> findOrderByNumber(String searchValue, int page, int nums);

    /**
     *  获取订单信息
     * @param name
     * */

    /**
     * 更新订单信息
     * @param orderModel
     * @return
     */
    boolean updateOrder(OrderModel orderModel);

    /**
     * 获取某个用户买到的闲置的订单列表
     * @param userId
     * @return
     */
    List<OrderModel> getMyOrder(Long userId);

    /**
     * 获取某个用户卖出的闲置的订单信息
     * @param userId
     * @return
     */
    List<OrderModel> getMySoldIdle(Long userId);

    PageVo<OrderModel> getAllOrder(int page, int nums);

    //    从session中获取购物车对象
    FavoriteModel getShopCar(UserModel user, HttpServletRequest request);

    boolean deleteOrder(Long id);

    OrderModel createOrder(OrderVo orderVo,String userid);


}
