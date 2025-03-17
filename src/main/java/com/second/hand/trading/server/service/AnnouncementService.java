package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.AnnouncementModel;
import com.second.hand.trading.server.vo.PageVo;
import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService {
    
    /**
     * 创建公告
     * @param announcement 公告信息
     * @return 是否成功
     */
    boolean createAnnouncement(AnnouncementModel announcement);
    
    /**
     * 更新公告
     * @param announcement 公告信息
     * @return 是否成功
     */
    boolean updateAnnouncement(AnnouncementModel announcement);
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告信息
     */
    AnnouncementModel getAnnouncementById(Long id);
    
    /**
     * 删除公告
     * @param id 公告ID
     * @return 是否成功
     */
    boolean deleteAnnouncement(Long id);
    
    /**
     * 发布公告
     * @param id 公告ID
     * @return 是否成功
     */
    boolean publishAnnouncement(Long id);
    
    /**
     * 获取公告列表
     * @param type 公告类型（可选）
     * @param status 公告状态（可选）
     * @param isImportant 是否重要（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 公告列表分页数据
     */
    PageVo getAnnouncementList(Integer type, Integer status, Boolean isImportant, Integer page, Integer size);
    
    /**
     * 获取已发布的公告列表（前台使用）
     * @param type 公告类型（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 公告列表
     */
    List<AnnouncementModel> getPublishedAnnouncementList(Integer type, int page, int size);
    
    /**
     * 获取重要公告列表（前台首页使用）
     * @param limit 限制数量
     * @return 重要公告列表
     */
    List<AnnouncementModel> getImportantAnnouncements(int limit);
}
