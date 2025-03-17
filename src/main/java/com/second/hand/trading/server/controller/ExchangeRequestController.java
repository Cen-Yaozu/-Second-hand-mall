package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.ExchangeRequestModel;
import com.second.hand.trading.server.model.vo.ExchangeRequestVo;
import com.second.hand.trading.server.service.ExchangeRequestService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 闲置易物交换请求控制器
 */
@RestController
@RequestMapping("/exchange")
public class ExchangeRequestController {

    @Autowired
    private ExchangeRequestService exchangeRequestService;

    /**
     * 创建交换请求
     * @param exchangeRequest 交换请求信息
     * @param session 会话
     * @return 结果
     */
    @PostMapping("/create")
    public ResultVo createExchangeRequest(@RequestBody ExchangeRequestModel exchangeRequest, HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 设置请求用户ID
        exchangeRequest.setRequestUserId(userId);
        
        // 创建交换请求
        boolean result = exchangeRequestService.createExchangeRequest(exchangeRequest);
        
        if (result) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }
    
    /**
     * 获取我发起的交换请求
     * @param session 会话
     * @return 请求列表
     */
    @GetMapping("/my-requests")
    public ResultVo getMyRequests(HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 获取请求列表
        List<ExchangeRequestVo> requests = exchangeRequestService.getMyRequests(userId);
        
        return ResultVo.success(requests);
    }
    
    /**
     * 获取我收到的交换请求
     * @param session 会话
     * @return 请求列表
     */
    @GetMapping("/received-requests")
    public ResultVo getReceivedRequests(HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 获取请求列表
        List<ExchangeRequestVo> requests = exchangeRequestService.getReceivedRequests(userId);
        
        return ResultVo.success(requests);
    }
    
    /**
     * 获取交换请求详情
     * @param requestId 请求ID
     * @param session 会话
     * @return 请求详情
     */
    @GetMapping("/detail/{requestId}")
    public ResultVo getExchangeRequestDetail(@PathVariable Long requestId, HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 获取请求详情
        ExchangeRequestVo request = exchangeRequestService.getExchangeRequestDetail(requestId);
        
        if (request == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 验证当前用户是否为请求用户或物品所有者
        if (!request.getRequestUserId().equals(userId) && !request.getItemOwnerId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        return ResultVo.success(request);
    }
    
    /**
     * 接受交换请求
     * @param requestId 请求ID
     * @param session 会话
     * @return 结果
     */
    @PutMapping("/accept/{requestId}")
    public ResultVo acceptRequest(@PathVariable Long requestId, HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 接受请求
        boolean result = exchangeRequestService.acceptRequest(requestId, userId);
        
        if (result) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }
    
    /**
     * 拒绝交换请求
     * @param requestId 请求ID
     * @param session 会话
     * @return 结果
     */
    @PutMapping("/reject/{requestId}")
    public ResultVo rejectRequest(@PathVariable Long requestId, HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 拒绝请求
        boolean result = exchangeRequestService.rejectRequest(requestId, userId);
        
        if (result) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }
    
    /**
     * 完成交换
     * @param requestId 请求ID
     * @param session 会话
     * @return 结果
     */
    @PutMapping("/complete/{requestId}")
    public ResultVo completeExchange(@PathVariable Long requestId, HttpSession session) {
        // 从会话中获取用户ID
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 完成交换
        boolean result = exchangeRequestService.completeExchange(requestId, userId);
        
        if (result) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }
} 