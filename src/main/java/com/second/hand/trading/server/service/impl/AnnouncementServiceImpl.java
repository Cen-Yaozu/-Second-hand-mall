package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.AnnouncementDao;
import com.second.hand.trading.server.model.AnnouncementModel;
import com.second.hand.trading.server.service.AnnouncementService;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告服务实现类
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    
    @Autowired
    private AnnouncementDao announcementDao;
    
    @Override
    public boolean createAnnouncement(AnnouncementModel announcement) {
        if (announcement.getType() == null) {
            announcement.setType(1); // 默认类型
        }
        if (announcement.getImportant() == null) {
            announcement.setImportant(0); // 默认不重要
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus(0); // 默认草稿状态
        }
        
        Date now = new Date();
        announcement.setCreateTime(now);
        announcement.setUpdateTime(now);
        
        // 如果状态为已发布，设置发布时间
        if (announcement.getStatus() == 1) {
            announcement.setPublishTime(now);
        }
        
        return announcementDao.insert(announcement) > 0;
    }
    
    @Override
    public boolean updateAnnouncement(AnnouncementModel announcement) {
        announcement.setUpdateTime(new Date());
        return announcementDao.update(announcement) > 0;
    }
    
    @Override
    public boolean publishAnnouncement(Long id) {
        return announcementDao.updateStatus(id, 1) > 0;
    }
    
    @Override
    public AnnouncementModel getAnnouncementById(Long id) {
        return announcementDao.selectById(id);
    }
    
    @Override
    public boolean deleteAnnouncement(Long id) {
        return announcementDao.deleteById(id) > 0;
    }
    
    @Override
    public PageVo getAnnouncementList(Integer type, Integer status, Boolean isImportant, Integer page, Integer size) {
        // 参数处理
        page = (page == null || page < 1) ? 1 : page;
        size = (size == null || size < 1) ? 10 : size;
        Integer important = isImportant == null ? null : (isImportant ? 1 : 0);
        
        // 计算起始位置
        int start = (page - 1) * size;
        
        // 查询数据
        List<AnnouncementModel> list = announcementDao.selectList(type, status, important, start, size);
        int total = announcementDao.countList(type, status, important);
        
        // 封装分页结果
        PageVo pageVo = new PageVo();
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        data.put("totalPage", (total + size - 1) / size);
        data.put("page", page);
        data.put("size", size);
        pageVo.setData(data);
        
        return pageVo;
    }
    
    @Override
    public List<AnnouncementModel> getPublishedAnnouncementList(Integer type, int page, int size) {
        // 计算起始位置
        int start = (page - 1) * size;
        
        // 只查询已发布的公告
        Integer status = 1; // 已发布状态
        
        // 查询数据
        return announcementDao.selectList(type, status, null, start, size);
    }
    
    @Override
    public List<AnnouncementModel> getImportantAnnouncements(int limit) {
        // 查询已发布且重要的公告
        Integer status = 1; // 已发布状态
        Integer important = 1; // 重要公告
        
        // 计算起始位置
        int start = 0;
        
        // 查询数据
        return announcementDao.selectList(null, status, important, start, limit);
    }
} 