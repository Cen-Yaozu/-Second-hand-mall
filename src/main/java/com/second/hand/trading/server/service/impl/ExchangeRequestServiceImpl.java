package com.second.hand.trading.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.second.hand.trading.server.dao.ExchangeRequestDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.ExchangeRequestModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.model.vo.ExchangeRequestVo;
import com.second.hand.trading.server.service.ExchangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 闲置易物交换请求Service实现
 */
@Service
public class ExchangeRequestServiceImpl implements ExchangeRequestService {

    @Autowired
    private ExchangeRequestDao exchangeRequestDao;
    
    @Autowired
    private IdleItemDao idleItemDao;
    
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public boolean createExchangeRequest(ExchangeRequestModel exchangeRequest) {
        // 设置初始状态为待审核
        exchangeRequest.setStatus(0);
        Date now = new Date();
        exchangeRequest.setCreateTime(now);
        exchangeRequest.setUpdateTime(now);
        
        // 验证请求交换的物品和提供交换的物品是否存在
        IdleItemModel requestItem = idleItemDao.selectById(exchangeRequest.getRequestItemId());
        IdleItemModel offerItem = idleItemDao.selectById(exchangeRequest.getOfferItemId());
        
        if (requestItem == null || offerItem == null) {
            return false;
        }
        
        // 验证请求交换的物品是否属于itemOwnerId，提供交换的物品是否属于requestUserId
        if (!requestItem.getUserId().equals(exchangeRequest.getItemOwnerId()) 
                || !offerItem.getUserId().equals(exchangeRequest.getRequestUserId())) {
            return false;
        }
        
        // 验证提供交换的物品是否已被锁定（在交易中）
        if (offerItem.getIdleStatus() != 0) {
            return false;
        }
        
        // 创建交换请求
        int result = exchangeRequestDao.insert(exchangeRequest);
        
        // 如果创建成功，将提供交换的物品状态设置为锁定(1-已锁定)
        if (result > 0) {
            offerItem.setIdleStatus(Byte.valueOf("1"));
            offerItem.setReleaseTime(now); // 使用releaseTime字段代替updateTime
            idleItemDao.updateById(offerItem);
            return true;
        }
        
        return false;
    }

    @Override
    public List<ExchangeRequestVo> getMyRequests(Long userId) {
        // 查询当前用户发起的交换请求
        QueryWrapper<ExchangeRequestModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("request_user_id", userId);
        queryWrapper.orderByDesc("create_time");
        
        List<ExchangeRequestModel> requests = exchangeRequestDao.selectList(queryWrapper);
        return convertToVoList(requests);
    }

    @Override
    public List<ExchangeRequestVo> getReceivedRequests(Long userId) {
        // 查询当前用户收到的交换请求
        QueryWrapper<ExchangeRequestModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_owner_id", userId);
        queryWrapper.orderByDesc("create_time");
        
        List<ExchangeRequestModel> requests = exchangeRequestDao.selectList(queryWrapper);
        return convertToVoList(requests);
    }

    @Override
    @Transactional
    public boolean acceptRequest(Long requestId, Long userId) {
        // 查询交换请求
        ExchangeRequestModel request = exchangeRequestDao.selectById(requestId);
        
        // 验证请求是否存在，状态是否为待审核，操作者是否为物品所有者
        if (request == null || request.getStatus() != 0 || !request.getItemOwnerId().equals(userId)) {
            return false;
        }
        
        // 更新请求状态为已接受
        request.setStatus(1);
        request.setUpdateTime(new Date());
        
        // 获取请求交换的物品，将其状态设置为锁定(1-已锁定)
        IdleItemModel requestItem = idleItemDao.selectById(request.getRequestItemId());
        if (requestItem == null) {
            return false;
        }
        
        requestItem.setIdleStatus(Byte.valueOf("1"));
        requestItem.setReleaseTime(new Date()); // 使用releaseTime字段代替updateTime
        
        // 更新数据库
        idleItemDao.updateById(requestItem);
        return exchangeRequestDao.updateById(request) > 0;
    }

    @Override
    @Transactional
    public boolean rejectRequest(Long requestId, Long userId) {
        // 查询交换请求
        ExchangeRequestModel request = exchangeRequestDao.selectById(requestId);
        
        // 验证请求是否存在，状态是否为待审核，操作者是否为物品所有者
        if (request == null || request.getStatus() != 0 || !request.getItemOwnerId().equals(userId)) {
            return false;
        }
        
        // 更新请求状态为已拒绝
        request.setStatus(2);
        request.setUpdateTime(new Date());
        
        // 获取提供交换的物品，将其状态恢复为可交易(0-在售)
        IdleItemModel offerItem = idleItemDao.selectById(request.getOfferItemId());
        if (offerItem == null) {
            return false;
        }
        
        offerItem.setIdleStatus(Byte.valueOf("0"));
        offerItem.setReleaseTime(new Date()); // 使用releaseTime字段代替updateTime
        
        // 更新数据库
        idleItemDao.updateById(offerItem);
        return exchangeRequestDao.updateById(request) > 0;
    }

    @Override
    @Transactional
    public boolean completeExchange(Long requestId, Long userId) {
        // 查询交换请求
        ExchangeRequestModel request = exchangeRequestDao.selectById(requestId);
        
        // 验证请求是否存在，状态是否为已接受，操作者是否为请求用户或物品所有者
        if (request == null || request.getStatus() != 1 || 
                (!request.getRequestUserId().equals(userId) && !request.getItemOwnerId().equals(userId))) {
            return false;
        }
        
        // 更新请求状态为已完成
        request.setStatus(3);
        request.setUpdateTime(new Date());
        
        // 获取请求交换的物品和提供交换的物品
        IdleItemModel requestItem = idleItemDao.selectById(request.getRequestItemId());
        IdleItemModel offerItem = idleItemDao.selectById(request.getOfferItemId());
        
        if (requestItem == null || offerItem == null) {
            return false;
        }
        
        Date now = new Date();
        
        // 更新物品状态为已售出(2-已售出)
        requestItem.setIdleStatus(Byte.valueOf("2"));
        requestItem.setReleaseTime(now); // 使用releaseTime字段代替updateTime
        offerItem.setIdleStatus(Byte.valueOf("2"));
        offerItem.setReleaseTime(now); // 使用releaseTime字段代替updateTime
        
        // 交换物品所有权
        Long tempUserId = requestItem.getUserId();
        requestItem.setUserId(offerItem.getUserId());
        offerItem.setUserId(tempUserId);
        
        // 更新数据库
        idleItemDao.updateById(requestItem);
        idleItemDao.updateById(offerItem);
        return exchangeRequestDao.updateById(request) > 0;
    }

    @Override
    public ExchangeRequestVo getExchangeRequestDetail(Long requestId) {
        // 查询交换请求
        ExchangeRequestModel request = exchangeRequestDao.selectById(requestId);
        if (request == null) {
            return null;
        }
        
        // 转换为VO
        ExchangeRequestVo vo = ExchangeRequestVo.fromModel(request);
        
        // 查询关联信息
        UserModel requestUser = userDao.selectById(request.getRequestUserId());
        UserModel itemOwner = userDao.selectById(request.getItemOwnerId());
        IdleItemModel requestItem = idleItemDao.selectById(request.getRequestItemId());
        IdleItemModel offerItem = idleItemDao.selectById(request.getOfferItemId());
        
        // 设置关联信息
        vo.setRequestUser(requestUser);
        vo.setItemOwner(itemOwner);
        vo.setRequestItem(requestItem);
        vo.setOfferItem(offerItem);
        
        return vo;
    }
    
    /**
     * 将Model列表转换为VO列表
     * @param models 模型列表
     * @return VO列表
     */
    private List<ExchangeRequestVo> convertToVoList(List<ExchangeRequestModel> models) {
        List<ExchangeRequestVo> vos = new ArrayList<>();
        
        for (ExchangeRequestModel model : models) {
            ExchangeRequestVo vo = ExchangeRequestVo.fromModel(model);
            
            // 查询关联的物品和用户信息
            IdleItemModel requestItem = idleItemDao.selectById(model.getRequestItemId());
            IdleItemModel offerItem = idleItemDao.selectById(model.getOfferItemId());
            UserModel requestUser = userDao.selectById(model.getRequestUserId());
            UserModel itemOwner = userDao.selectById(model.getItemOwnerId());
            
            // 设置关联信息
            vo.setRequestItem(requestItem);
            vo.setOfferItem(offerItem);
            vo.setRequestUser(requestUser);
            vo.setItemOwner(itemOwner);
            
            vos.add(vo);
        }
        
        return vos;
    }
} 