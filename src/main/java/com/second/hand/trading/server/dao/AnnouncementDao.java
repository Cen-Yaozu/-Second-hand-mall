package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.AnnouncementModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告数据访问接口
 */
@Mapper
public interface AnnouncementDao {
    
    /**
     * 创建公告
     * @param announcement 公告信息
     * @return 影响行数
     */
    int insert(AnnouncementModel announcement);
    
    /**
     * 更新公告
     * @param announcement 公告信息
     * @return 影响行数
     */
    int update(AnnouncementModel announcement);
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告信息
     */
    AnnouncementModel selectById(Long id);
    
    /**
     * 根据ID删除公告
     * @param id 公告ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 获取公告列表
     * @param type 公告类型（可选）
     * @param status 公告状态（可选）
     * @param important 是否重要（可选）
     * @param start 起始索引
     * @param limit 记录数
     * @return 公告列表
     */
    List<AnnouncementModel> selectList(
            @Param("type") Integer type,
            @Param("status") Integer status,
            @Param("important") Integer important,
            @Param("start") Integer start,
            @Param("limit") Integer limit);
    
    /**
     * 获取公告总数
     * @param type 公告类型（可选）
     * @param status 公告状态（可选）
     * @param important 是否重要（可选）
     * @return 公告总数
     */
    int countList(
            @Param("type") Integer type,
            @Param("status") Integer status,
            @Param("important") Integer important);
    
    /**
     * 更新公告状态
     * @param id 公告ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
} 