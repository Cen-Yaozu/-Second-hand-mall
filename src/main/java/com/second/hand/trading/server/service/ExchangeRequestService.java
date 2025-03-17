package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.ExchangeRequestModel;
import com.second.hand.trading.server.model.vo.ExchangeRequestVo;

import java.util.List;

/**
 * 闲置易物交换请求Service
 */
public interface ExchangeRequestService {
    
    /**
     * 创建交换请求
     * @param exchangeRequest 交换请求信息
     * @return 创建结果
     */
    boolean createExchangeRequest(ExchangeRequestModel exchangeRequest);
    
    /**
     * 获取我发起的交换请求
     * @param userId 用户ID
     * @return 交换请求列表
     */
    List<ExchangeRequestVo> getMyRequests(Long userId);
    
    /**
     * 获取我收到的交换请求
     * @param userId 用户ID
     * @return 交换请求列表
     */
    List<ExchangeRequestVo> getReceivedRequests(Long userId);
    
    /**
     * 接受交换请求
     * @param requestId 请求ID
     * @param userId 当前操作的用户ID
     * @return 操作结果
     */
    boolean acceptRequest(Long requestId, Long userId);
    
    /**
     * 拒绝交换请求
     * @param requestId 请求ID
     * @param userId 当前操作的用户ID
     * @return 操作结果
     */
    boolean rejectRequest(Long requestId, Long userId);
    
    /**
     * 完成交换
     * @param requestId 请求ID
     * @param userId 当前操作的用户ID
     * @return 操作结果
     */
    boolean completeExchange(Long requestId, Long userId);
    
    /**
     * 获取交换请求详情
     * @param requestId 请求ID
     * @return 交换请求详情
     */
    ExchangeRequestVo getExchangeRequestDetail(Long requestId);
} 