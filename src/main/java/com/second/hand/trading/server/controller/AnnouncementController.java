package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.AnnouncementModel;
import com.second.hand.trading.server.service.AnnouncementService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公告控制器 - 用户端接口
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 获取公告列表（用户端）
     * @param type 公告类型（可选）
     * @param important 是否重要（可选）
     * @param page 页码（可选，默认1）
     * @param limit 每页大小（可选，默认10）
     * @return 公告列表
     */
    @GetMapping("/list")
    public ResultVo getAnnouncementList(
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "important", required = false) Integer important,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        
        // 用户端只能看到已发布的公告
        Integer status = 1; // 1表示已发布状态
        Boolean isImportant = null;
        if (important != null) {
            isImportant = important == 1;
        }
        
        return ResultVo.success(announcementService.getAnnouncementList(type, status, isImportant, page, limit));
    }

    /**
     * 获取公告详情（用户端）
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/detail")
    public ResultVo getAnnouncementDetail(@RequestParam("id") Long id) {
        
        AnnouncementModel announcement = announcementService.getAnnouncementById(id);
        
        if (announcement == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 用户端只能看到已发布的公告
        if (announcement.getStatus() != 1) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        return ResultVo.success(announcement);
    }

    /**
     * 获取重要公告列表（用于首页展示）
     * @param limit 限制数量（可选，默认5）
     * @return 重要公告列表
     */
    @GetMapping("/important")
    public ResultVo getImportantAnnouncements(
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {
        
        // 获取已发布的重要公告
        Integer status = 1; // 1表示已发布状态
        Boolean isImportant = true;
        
        return ResultVo.success(announcementService.getAnnouncementList(null, status, isImportant, 1, limit));
    }
} 