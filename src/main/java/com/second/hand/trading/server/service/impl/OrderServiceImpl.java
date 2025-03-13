package com.second.hand.trading.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.model.FavoriteModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.OrderModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.model.vo.OrderVo;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.utils.IdFactoryUtil;
import com.second.hand.trading.server.utils.OrderTask;
import com.second.hand.trading.server.utils.OrderTaskHandler;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderModel> implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private IdleItemDao idleItemDao;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 新增订单，同时下架闲置
     * 用了事务串行化，后续要优化，修改更新的sql，增加更新条件，而不是在代码中判断条件
     * 业务逻辑可优化，改为支付时才下架。
     * 新功能待做，需要新增订单超时处理
     * （订单超时：
     * 1、重新上架闲置；2、修改订单状态；
     * 3、确保订单取消前不会影响用户的支付，支付前要判断订单状态并加读锁，取消订单时要判断订单状态为未支付才能取消；
     * 4、保证延期任务一定执行，即确保任务不会因为系统异常而消失）
     * @param orderModel
     * @return
     */
    private static HashMap<Integer,ReentrantLock> lockMap=new HashMap<>();
    
    static {
        for(int i=0;i<10;i++){
            lockMap.put(i,new ReentrantLock());
        }
    }

    @Override
    public boolean addOrder(OrderModel orderModel){
        // 查询闲置物品
        IdleItemModel idleItemModel = idleItemDao.selectById(orderModel.getIdleId());
        if(idleItemModel == null) {
            return false;
        }
        
        // 加锁处理
        int lockId = idleItemModel.getId().intValue() % 10;
        ReentrantLock lock = lockMap.get(lockId);
        lock.lock();
        
        try {
            return addOrderHelp(idleItemModel, orderModel);
        } finally {
            lock.unlock();
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean addOrderHelp(IdleItemModel idleItem, OrderModel orderModel){
        // 检查商品状态
        if(idleItem.getIdleStatus() != 0) {
            return false;
        }
        
        // 更新商品状态为已下架
        LambdaUpdateWrapper<IdleItemModel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(IdleItemModel::getId, idleItem.getId())
                    .eq(IdleItemModel::getIdleStatus, 0) // 确保状态为上架中
                    .set(IdleItemModel::getIdleStatus, 1); // 设置为已下架
        
        boolean updated = idleItemDao.update(null, updateWrapper) > 0;
        if(!updated) {
            return false;
        }
        
        // 设置订单的闲置物品信息
        orderModel.setOrderPrice(idleItem.getIdlePrice());
        
        // 创建30分钟后自动取消的任务
        OrderTask task = new OrderTask(orderModel, 30 * 60);
        OrderTaskHandler.addOrder(task);
        
        // 保存订单
        return save(orderModel);
    }

    @Override
    public OrderModel getOrder(Long id){
        OrderModel orderModel = getById(id);
        if (orderModel != null && orderModel.getIdleId() != null) {
            orderModel.setIdleItem(idleItemDao.selectById(orderModel.getIdleId()));
        }
        return orderModel;
    }

    @Override
    public PageVo<OrderModel> findOrderByNumber(String searchValue, int page, int nums) {
        // 构建分页对象
        IPage<OrderModel> pageParam = new Page<>(page, nums);
        
        // 构建查询条件
        LambdaQueryWrapper<OrderModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(OrderModel::getOrderNumber, searchValue)
                   .orderByDesc(OrderModel::getCreateTime);
        
        // 执行分页查询
        IPage<OrderModel> result = page(pageParam, queryWrapper);
        
        // 查询关联的闲置物品
        List<OrderModel> orders = result.getRecords();
        if (!orders.isEmpty()) {
            List<Long> idleIds = new ArrayList<>();
            for (OrderModel order : orders) {
                if (order.getIdleId() != null) {
                    idleIds.add(order.getIdleId());
                }
            }
            
            if (!idleIds.isEmpty()) {
                LambdaQueryWrapper<IdleItemModel> idleQueryWrapper = new LambdaQueryWrapper<>();
                idleQueryWrapper.in(IdleItemModel::getId, idleIds);
                List<IdleItemModel> idleItems = idleItemDao.selectList(idleQueryWrapper);
                
                Map<Long, IdleItemModel> idleMap = new HashMap<>();
                for (IdleItemModel idle : idleItems) {
                    idleMap.put(idle.getId(), idle);
                }
                
                for (OrderModel order : orders) {
                    if (order.getIdleId() != null) {
                        order.setIdleItem(idleMap.get(order.getIdleId()));
                    }
                }
            }
        }
        
        // 返回结果
        return new PageVo<>(orders, (int)result.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(OrderModel orderModel){
        // 获取原订单信息
        OrderModel oldOrder = getById(orderModel.getId());
        if(oldOrder == null) {
            return false;
        }
        
        // 检查订单状态变化
        if(orderModel.getOrderStatus() != null && 
           oldOrder.getOrderStatus() != orderModel.getOrderStatus()) {
            
            // 处理取消订单的情况
            if(orderModel.getOrderStatus() == 2) { // 假设2表示取消订单
                // 重新上架闲置物品
                if(oldOrder.getIdleId() != null) {
                    LambdaUpdateWrapper<IdleItemModel> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(IdleItemModel::getId, oldOrder.getIdleId())
                                .set(IdleItemModel::getIdleStatus, 0); // 重新上架
                    idleItemDao.update(null, updateWrapper);
                }
            }
        }
        
        // 不更新的字段置为null
        orderModel.setOrderNumber(null);
        orderModel.setUserId(null);
        orderModel.setIdleId(null);
        orderModel.setCreateTime(null);
        
        // 更新订单
        return updateById(orderModel);
    }

    @Override
    public List<OrderModel> getMyOrder(Long userId){
        // 构建查询条件
        LambdaQueryWrapper<OrderModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderModel::getUserId, userId)
                   .orderByDesc(OrderModel::getCreateTime);
        
        // 执行查询
        List<OrderModel> orders = list(queryWrapper);
        
        // 查询关联的闲置物品
        if (!orders.isEmpty()) {
            List<Long> idleIds = new ArrayList<>();
            for (OrderModel order : orders) {
                if (order.getIdleId() != null) {
                    idleIds.add(order.getIdleId());
                }
            }
            
            if (!idleIds.isEmpty()) {
                LambdaQueryWrapper<IdleItemModel> idleQueryWrapper = new LambdaQueryWrapper<>();
                idleQueryWrapper.in(IdleItemModel::getId, idleIds);
                List<IdleItemModel> idleItems = idleItemDao.selectList(idleQueryWrapper);
                
                Map<Long, IdleItemModel> idleMap = new HashMap<>();
                for (IdleItemModel idle : idleItems) {
                    idleMap.put(idle.getId(), idle);
                }
                
                for (OrderModel order : orders) {
                    if (order.getIdleId() != null) {
                        order.setIdleItem(idleMap.get(order.getIdleId()));
                    }
                }
            }
        }
        
        return orders;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<OrderModel> getMySoldIdle(Long userId){
        // 查询用户的闲置物品IDs
        LambdaQueryWrapper<IdleItemModel> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(IdleItemModel::getUserId, userId)
                       .select(IdleItemModel::getId);
        
        List<IdleItemModel> idleItems = idleItemDao.selectList(itemQueryWrapper);
        
        if(idleItems.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取闲置物品IDs
        List<Long> idleIds = new ArrayList<>();
        for(IdleItemModel item : idleItems) {
            idleIds.add(item.getId());
        }
        
        // 查询这些闲置物品对应的订单
        LambdaQueryWrapper<OrderModel> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.in(OrderModel::getIdleId, idleIds)
                        .orderByDesc(OrderModel::getCreateTime);
        
        List<OrderModel> orders = list(orderQueryWrapper);
        
        // 关联闲置物品信息
        Map<Long, IdleItemModel> idleMap = new HashMap<>();
        for (IdleItemModel idle : idleItems) {
            idleMap.put(idle.getId(), idle);
        }
        
        for (OrderModel order : orders) {
            if (order.getIdleId() != null) {
                order.setIdleItem(idleMap.get(order.getIdleId()));
            }
        }
        
        return orders;
    }

    @Override
    public PageVo<OrderModel> getAllOrder(int page, int nums){
        // 构建分页对象
        IPage<OrderModel> pageParam = new Page<>(page, nums);
        
        // 构建查询条件
        LambdaQueryWrapper<OrderModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(OrderModel::getCreateTime);
        
        // 执行分页查询
        IPage<OrderModel> result = page(pageParam, queryWrapper);
        
        // 查询关联的闲置物品
        List<OrderModel> orders = result.getRecords();
        if (!orders.isEmpty()) {
            List<Long> idleIds = new ArrayList<>();
            for (OrderModel order : orders) {
                if (order.getIdleId() != null) {
                    idleIds.add(order.getIdleId());
                }
            }
            
            if (!idleIds.isEmpty()) {
                LambdaQueryWrapper<IdleItemModel> idleQueryWrapper = new LambdaQueryWrapper<>();
                idleQueryWrapper.in(IdleItemModel::getId, idleIds);
                List<IdleItemModel> idleItems = idleItemDao.selectList(idleQueryWrapper);
                
                Map<Long, IdleItemModel> idleMap = new HashMap<>();
                for (IdleItemModel idle : idleItems) {
                    idleMap.put(idle.getId(), idle);
                }
                
                for (OrderModel order : orders) {
                    if (order.getIdleId() != null) {
                        order.setIdleItem(idleMap.get(order.getIdleId()));
                    }
                }
            }
        }
        
        // 返回结果
        return new PageVo<>(orders, (int)result.getTotal());
    }

    @Override
    public FavoriteModel getShopCar(UserModel user, HttpServletRequest request) {
        // 这个方法涉及会话处理，需要根据具体实现调整
        return (FavoriteModel) request.getSession().getAttribute("shoppingCar_" + user.getId());
    }

    @Override
    public boolean deleteOrder(Long id){
        return removeById(id);
    }

    @Override
    @Transactional
    public OrderModel createOrder(OrderVo orderVo, String userid) {
        try {
            // 创建订单
            OrderModel orderModel = new OrderModel();
            orderModel.setOrderNumber(IdFactoryUtil.getOrderId());
            orderModel.setCreateTime(new Date());
            orderModel.setUserId(Long.valueOf(userid));
            orderModel.setOrderStatus((byte) 0);
            orderModel.setPaymentStatus((byte) 0);
            orderModel.setOrderPrice(orderVo.getTotal_price());
            
            // 序列化订单项
            orderModel.setOrderVoList(orderVo);
            String idleItemsJson = objectMapper.writeValueAsString(orderModel.getOrderVoList());
            orderModel.setIdle_items(idleItemsJson);
            
            // 保存订单
            if(save(orderModel)) {
                // 批量更新闲置物品状态
                List<String> idleItemIds = orderVo.getIdleItemIds();
                if (idleItemIds != null && !idleItemIds.isEmpty()) {
                    for (String idStr : idleItemIds) {
                        Long idleId = Long.valueOf(idStr);
                        LambdaUpdateWrapper<IdleItemModel> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(IdleItemModel::getId, idleId)
                                    .set(IdleItemModel::getIdleStatus, 1); // 设置为已下架
                        idleItemDao.update(null, updateWrapper);
                    }
                }
                
                // 创建自动取消任务
                OrderTask task = new OrderTask(orderModel, 30 * 60);
                OrderTaskHandler.addOrder(task);
                
                return orderModel;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
