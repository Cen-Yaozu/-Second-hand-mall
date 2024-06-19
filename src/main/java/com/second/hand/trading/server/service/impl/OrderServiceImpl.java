package com.second.hand.trading.server.service.impl;

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
public class OrderServiceImpl  extends ServiceImpl<OrderDao, OrderModel> implements OrderService {

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


    /**
     * 定义一个静态代码块，用于初始化锁映射表。
     * 锁映射表使用HashMap来存储，其中键为整数，值为ReentrantLock类型。
     * 这个映射表在类加载时被初始化，为0到99的每个整数分配一个公平的ReentrantLock。
     * 公平锁意味着获取锁的线程将按照它们到达的顺序被调度。
     */
    private static HashMap<Integer,ReentrantLock> lockMap=new HashMap<>();
    static {
        // 初始化lockMap，为每一个可能的线程分配一个公平的锁
//        ReentrantLock lock=new ReentrantLock(true);
        for(int i=0;i<100;i++){
            lockMap.put(i,new ReentrantLock(true));
        }
    }
    public boolean addOrder(OrderModel orderModel){
        IdleItemModel idleItemModel=idleItemDao.selectByPrimaryKey(orderModel.getIdleId());
        System.out.println(idleItemModel.getIdleStatus());
        if(idleItemModel.getIdleStatus()!=1){
            return false;
        }
        IdleItemModel idleItem=new IdleItemModel();
        idleItem.setId(orderModel.getIdleId());
        idleItem.setUserId(idleItemModel.getUserId());
        idleItem.setIdleStatus((byte)2);


        /*
         * 使用订单锁来保证订单操作的线程安全。
         *
         * @param idleItem 闲置物品信息
         * @param orderModel 订单模型
         * @return boolean 添加订单是否成功的标志
         */
        int key= (int) (orderModel.getIdleId()%100); // 根据订单ID计算锁的索引
        ReentrantLock lock=lockMap.get(key); // 从锁映射中获取对应的锁
        boolean flag;
        try {
            lock.lock(); // 获取锁
            flag=addOrderHelp(idleItem,orderModel); // 尝试添加订单
        }finally {
            lock.unlock(); // 释放锁
        }
        return flag; // 返回添加订单的结果

    }



    /**
     * 添加订单辅助方法
     * @param idleItem 闲置物品模型，包含闲置物品的相关信息
     * @param orderModel 订单模型，包含订单的相关信息
     * @return boolean 返回true表示订单添加成功，返回false表示订单添加失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addOrderHelp(IdleItemModel idleItem, OrderModel orderModel){
        // 根据闲置物品ID查询闲置物品信息
        IdleItemModel idleItemModel = idleItemDao.selectByPrimaryKey(orderModel.getIdleId());
        // 检查闲置物品状态是否为可用（1）
        if (idleItemModel.getIdleStatus() != 1) {
            return false;
        }
        // 更新闲置物品状态为已售出
        if (idleItemDao.updateByPrimaryKeySelective(idleItem) == 1) {
            // 插入新订单
            if (orderDao.insert(orderModel) == 1) {
                // 设置订单状态为待支付（4）
                orderModel.setOrderStatus((byte) 4);
                // 添加订单任务，如果订单在半小时内未支付则取消订单
                OrderTaskHandler.addOrder(new OrderTask(orderModel, 30 * 60));
                return true;
            } else {
                // 如果订单插入失败，抛出运行时异常
                new RuntimeException();
            }
        }
        return false;
    }


    /**
     * 获取订单信息，同时获取对应的闲置信息
     * @param id
     * @return
     */
    public OrderModel getOrder(Long id){
        OrderModel orderModel=orderDao.selectByPrimaryKey(id);
        orderModel.setIdleItem(idleItemDao.selectByPrimaryKey(orderModel.getIdleId()));
        return orderModel;
    }

    /**
     *   根据订单号，查询订单
     *
     *
     * @return*/
    @Override
    public PageVo<OrderModel> findOrderByNumber(String searchValue, int page, int nums) {
        List<OrderModel> list=orderDao.getOrderByNumber(searchValue,(page-1)*nums,nums);

        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(OrderModel i:list){
                idleIdList.add(i.getIdleId());
            }
            List<IdleItemModel> idleItemModelList=idleItemDao.findIdleByList(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:idleItemModelList){
                map.put(idle.getId(),idle);
            }
            for(OrderModel i:list){
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }

        return new PageVo<OrderModel>(list,1);
    }

    /**
     * 更新订单状态，无验证，后期修改为定制的更新sql
     * 后期改为在支付时下架闲置
     * @param orderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(OrderModel orderModel){
        //不可修改的信息
        orderModel.setOrderNumber(null);
        orderModel.setUserId(null);
        orderModel.setIdleId(null);
        orderModel.setCreateTime(null);
        if(orderModel.getOrderStatus()==4){
            //取消订单,需要优化，减少数据库查询次数
            OrderModel o=orderDao.selectByPrimaryKey(orderModel.getId());
            if(o.getOrderStatus()!=0){
                return false;
            }
            IdleItemModel idleItemModel=idleItemDao.selectByPrimaryKey(o.getIdleId());
            if(idleItemModel.getIdleStatus()==2){
                IdleItemModel idleItem=new IdleItemModel();
                idleItem.setId(o.getIdleId());
                idleItem.setUserId(idleItemModel.getUserId());
                idleItem.setIdleStatus((byte)1);
                if(orderDao.updateByPrimaryKeySelective(orderModel)==1){
                    if(idleItemDao.updateByPrimaryKeySelective(idleItem)==1){
                        return true;
                    }else {
                        new RuntimeException();
                    }
                }
                return false;
            }else{
                if(orderDao.updateByPrimaryKeySelective(orderModel)==1){
                    return true;
                }else {
                    new RuntimeException();
                }
            }
        }
        return orderDao.updateByPrimaryKeySelective(orderModel)==1;
    }
    /**
     * 获取我的所有订单
     * 同时查询出对应的闲置信息，
     * 未做分页
     * userId建索引
     * @param userId
     * @return
     */
    public List<OrderModel> getMyOrder(Long userId){
        List<OrderModel> list=orderDao.getMyOrder(userId);
        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(OrderModel i:list){
                idleIdList.add(i.getIdleId());
            }

            /*这里还有错误，购物车结算订单后，多个商品需要生成购物记录，需要将订单中idle_items读取出来，通过idle_items查询出商品列表显示出来订单，
            订单的信息中也需要修改，将多个商品信息保存*/

            List<IdleItemModel> idleItemModelList=idleItemDao.findIdleByList(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:idleItemModelList){
                map.put(idle.getId(),idle);
            }
            for(OrderModel i:list){
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }
        return list;
    }

    /**
     * 查询用户卖出的闲置
     * @param userId
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<OrderModel> getMySoldIdle(Long userId){
        List<IdleItemModel> list=idleItemDao.getAllIdleItem(userId);
        List<OrderModel> orderList=null;
        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(IdleItemModel i:list){
                idleIdList.add(i.getId());
            }
            orderList=orderDao.findOrderByIdleIdList(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:list){
                map.put(idle.getId(),idle);
            }
            for(OrderModel o:orderList){
                o.setIdleItem(map.get(o.getIdleId()));
            }
        }
        return orderList;
    }

    public PageVo<OrderModel> getAllOrder(int page, int nums){
        List<OrderModel> list=orderDao.getAllOrder((page-1)*nums,nums);
        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(OrderModel i:list){
                idleIdList.add(i.getIdleId());
            }
            List<IdleItemModel> idleItemModelList=idleItemDao.findIdleByList(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:idleItemModelList){
                map.put(idle.getId(),idle);
            }
            for(OrderModel i:list){
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }
        int count=orderDao.countAllOrder();
        return new PageVo<>(list,count);
    }

    @Override
    public FavoriteModel getShopCar(UserModel user, HttpServletRequest request) {
        return null;
    }

    public boolean deleteOrder(Long id){
        return orderDao.deleteByPrimaryKey(id)==1;
    }

    @Override
    @Transactional
    public OrderModel createOrder(OrderVo orderVo,String userid) {
        try{
            OrderModel model = new OrderModel();

            model.setOrderNumber(IdFactoryUtil.getOrderId());

            model.setCreateTime(new Date());
            model.setOrderStatus((byte) 0);
            model.setPaymentStatus((byte)0);
            model.setOrderPrice(orderVo.getTotal_price());
            model.setUserId(Long.valueOf(userid));

            /*使用fastjson将其序列化为string格式，存储到数据库中*/
            model.setOrderVoList(orderVo);
            String idleItemsJson = objectMapper.writeValueAsString(model.getOrderVoList());
            model.setIdle_items(idleItemsJson);

            int insert = orderDao.insert(model);

            if (insert>0){
                idleItemDao.updateStatusByIds(orderVo.getIdleItemIds());
                return model;
            }
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
