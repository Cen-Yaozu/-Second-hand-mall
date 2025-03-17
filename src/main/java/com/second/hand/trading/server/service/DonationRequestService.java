package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.DonationRequestModel;
import com.second.hand.trading.server.model.vo.DonationRequestVo;

import java.util.List;

/**
 * 爱心捐赠请求Service
 */
public interface DonationRequestService {
    
    /**
     * 创建捐赠请求
     * @param donationRequest 捐赠请求信息
     * @return 创建结果
     */
    boolean createDonationRequest(DonationRequestModel donationRequest);
    
    /**
     * 获取我的捐赠请求
     * @param userId 用户ID
     * @return 捐赠请求列表
     */
    List<DonationRequestVo> getMyDonationRequests(Long userId);
    
    /**
     * 取消捐赠请求
     * @param requestId 请求ID
     * @param userId 当前操作的用户ID
     * @return 操作结果
     */
    boolean cancelDonationRequest(Long requestId, Long userId);
    
    /**
     * 获取捐赠请求详情
     * @param requestId 请求ID
     * @return 捐赠请求详情
     */
    DonationRequestVo getDonationRequestDetail(Long requestId);
    
    /**
     * 获取所有捐赠请求（管理员使用）
     * @param status 状态筛选，可为null
     * @return 捐赠请求列表
     */
    List<DonationRequestVo> getAllDonationRequests(Integer status);
    
    /**
     * 审核捐赠请求（管理员使用）
     * @param requestId 请求ID
     * @param status 审核状态：1-接受，2-拒绝
     * @param adminId 管理员ID
     * @param feedback 反馈信息
     * @return 操作结果
     */
    boolean reviewDonationRequest(Long requestId, Integer status, Long adminId, String feedback);
    
    /**
     * 完成捐赠（管理员使用）
     * @param requestId 请求ID
     * @param adminId 管理员ID
     * @return 操作结果
     */
    boolean completeDonation(Long requestId, Long adminId);
} 