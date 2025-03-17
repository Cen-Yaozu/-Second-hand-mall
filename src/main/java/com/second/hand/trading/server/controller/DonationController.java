package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.DonationRequestModel;
import com.second.hand.trading.server.model.vo.DonationRequestVo;
import com.second.hand.trading.server.service.DonationRequestService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 爱心捐赠请求控制器
 */
@RestController
@RequestMapping("/donation")
public class DonationController {

    @Autowired
    private DonationRequestService donationRequestService;

    /**
     * 创建捐赠请求
     * @param donationRequest 捐赠请求信息
     * @param session 会话
     * @return 结果
     */
    @PostMapping("/create")
    public ResultVo createDonationRequest(@RequestBody DonationRequestModel donationRequest, HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 设置用户ID
        donationRequest.setUserId(userId);
        
        // 创建捐赠请求
        boolean result = donationRequestService.createDonationRequest(donationRequest);
        
        if (result) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }
    
    /**
     * 获取我的捐赠请求
     * @param session 会话
     * @return 请求列表
     */
    @GetMapping("/my-requests")
    public ResultVo getMyDonationRequests(HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 获取请求列表
        List<DonationRequestVo> requests = donationRequestService.getMyDonationRequests(userId);
        
        return ResultVo.success(requests);
    }
    
    /**
     * 获取捐赠请求详情
     * @param requestId 请求ID
     * @param session 会话
     * @return 请求详情
     */
    @GetMapping("/detail/{requestId}")
    public ResultVo getDonationRequestDetail(@PathVariable Long requestId, HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 获取请求详情
        DonationRequestVo requestDetail = donationRequestService.getDonationRequestDetail(requestId);
        
        if (requestDetail == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 验证请求是否属于当前用户（非管理员只能查看自己的请求）
        Long requestUserId = requestDetail.getUser().getId();
        if (!userId.equals(requestUserId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        return ResultVo.success(requestDetail);
    }
    
    /**
     * 取消捐赠请求
     * @param requestId 请求ID
     * @param session 会话
     * @return 结果
     */
    @PutMapping("/cancel/{requestId}")
    public ResultVo cancelDonationRequest(@PathVariable Long requestId, HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 取消请求
        boolean result = donationRequestService.cancelDonationRequest(requestId, userId);
        
        if (result) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
    }
} 