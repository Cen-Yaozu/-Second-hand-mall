package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.ReportModel;
import com.second.hand.trading.server.model.vo.ReportVo;

import java.util.List;

/**
 * 举报服务接口
 * @author claude
 */
public interface ReportService {
    
    /**
     * 创建举报
     * @param reportModel 举报信息
     * @return 创建结果
     */
    Boolean addReport(ReportModel reportModel);
    
    /**
     * 更新举报信息
     * @param reportModel 举报信息
     * @return 更新结果
     */
    Boolean updateReport(ReportModel reportModel);
    
    /**
     * 获取用户提交的所有举报
     * @param reporterId 举报用户ID
     * @return 举报列表
     */
    List<ReportVo> getReportsByReporterId(Long reporterId);
    
    /**
     * 获取针对某个用户的所有举报
     * @param reportedUserId 被举报用户ID
     * @return 举报列表
     */
    List<ReportVo> getReportsByReportedUserId(Long reportedUserId);
    
    /**
     * 获取针对某个商品的所有举报
     * @param reportedItemId 被举报商品ID
     * @return 举报列表
     */
    List<ReportVo> getReportsByReportedItemId(Long reportedItemId);
    
    /**
     * 根据ID获取举报详情
     * @param id 举报ID
     * @return 举报详情
     */
    ReportVo getReportById(Long id);
    
    /**
     * 获取所有举报列表（管理员使用）
     * @return 举报列表
     */
    List<ReportVo> getAllReports();
    
    /**
     * 根据状态获取举报列表
     * @param status 举报状态
     * @return 举报列表
     */
    List<ReportVo> getReportsByStatus(Integer status);
    
    /**
     * 管理员处理举报
     * @param id 举报ID
     * @param adminId 管理员ID
     * @param status 处理后的状态
     * @param handleResult 处理结果说明
     * @return 处理结果
     */
    Boolean handleReport(Long id, Long adminId, Integer status, String handleResult);
} 