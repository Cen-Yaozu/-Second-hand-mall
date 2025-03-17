package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.ReportModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 举报信息DAO接口
 * @author claude
 */
@Mapper
@Repository
public interface ReportDao extends BaseMapper<ReportModel> {
    /**
     * 查询用户提交的所有举报
     * @param reporterId 举报人ID
     * @return 举报列表
     */
    List<ReportModel> findByReporterId(@Param("reporterId") Long reporterId);
    
    /**
     * 查询针对某个用户的所有举报
     * @param reportedUserId 被举报用户ID
     * @return 举报列表
     */
    List<ReportModel> findByReportedUserId(@Param("reportedUserId") Long reportedUserId);
    
    /**
     * 查询针对某个商品的所有举报
     * @param reportedItemId 被举报商品ID
     * @return 举报列表
     */
    List<ReportModel> findByReportedItemId(@Param("reportedItemId") Long reportedItemId);
    
    /**
     * 按状态查询举报
     * @param status 举报状态
     * @return 举报列表
     */
    List<ReportModel> findByStatus(@Param("status") Integer status);
} 