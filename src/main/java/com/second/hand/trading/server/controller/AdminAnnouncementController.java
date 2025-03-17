package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.AnnouncementModel;
import com.second.hand.trading.server.model.AdminModel;
import com.second.hand.trading.server.service.AnnouncementService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 公告控制器 - 管理员端接口
 */
@RestController
@RequestMapping("/admin/announcement")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 创建公告
     * @param announcementModel 公告信息
     * @param session HttpSession
     * @return 创建结果
     */
    @PostMapping("/create")
    public ResultVo createAnnouncement(
            @RequestBody AnnouncementModel announcementModel,
            HttpSession session) {
        
        // 从session中获取管理员信息
        if(session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 设置管理员ID
        AdminModel adminModel = (AdminModel) session.getAttribute("admin");
        announcementModel.setAdminId(adminModel.getId());
        
        // 创建公告
        boolean success = announcementService.createAnnouncement(announcementModel);
        
        if (success) {
            return ResultVo.success(announcementModel);
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }

    /**
     * 更新公告
     * @param id 公告ID
     * @param announcementModel 公告信息
     * @param session HttpSession
     * @return 更新结果
     */
    @PutMapping("/update/{id}")
    public ResultVo updateAnnouncement(
            @PathVariable Long id,
            @RequestBody AnnouncementModel announcementModel,
            HttpSession session) {
        
        // 从session中获取管理员信息
        if(session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 检查公告是否存在
        AnnouncementModel existingAnnouncement = announcementService.getAnnouncementById(id);
        if (existingAnnouncement == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 设置ID
        announcementModel.setId(id);
        
        // 更新公告
        boolean success = announcementService.updateAnnouncement(announcementModel);
        
        if (success) {
            return ResultVo.success(announcementModel);
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }

    /**
     * 发布公告
     * @param id 公告ID
     * @param session HttpSession
     * @return 发布结果
     */
    @PutMapping("/publish/{id}")
    public ResultVo publishAnnouncement(
            @PathVariable Long id,
            HttpSession session) {
        
        // 从session中获取管理员信息
        if(session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 发布公告
        boolean success = announcementService.publishAnnouncement(id);
        
        if (success) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @param session HttpSession
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public ResultVo deleteAnnouncement(
            @PathVariable Long id,
            HttpSession session) {
        
        // 从session中获取管理员信息
        if(session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 删除公告
        boolean success = announcementService.deleteAnnouncement(id);
        
        if (success) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }

    /**
     * 获取公告列表（管理员端）
     * @param type 公告类型（可选）
     * @param status 公告状态（可选）
     * @param isImportant 是否重要（可选）
     * @param page 页码（可选，默认1）
     * @param size 每页大小（可选，默认10）
     * @param session HttpSession
     * @return 公告列表
     */
    @GetMapping("/list")
    public ResultVo getAnnouncementList(
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "isImportant", required = false) Boolean isImportant,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            HttpSession session) {
        
        // 从session中获取管理员信息
        if(session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        return ResultVo.success(announcementService.getAnnouncementList(type, status, isImportant, page, size));
    }

    /**
     * 获取公告详情（管理员端）
     * @param id 公告ID
     * @param session HttpSession
     * @return 公告详情
     */
    @GetMapping("/detail/{id}")
    public ResultVo getAnnouncementDetail(
            @PathVariable Long id,
            HttpSession session) {
        
        // 从session中获取管理员信息
        if(session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        AnnouncementModel announcement = announcementService.getAnnouncementById(id);
        
        if (announcement == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        return ResultVo.success(announcement);
    }
} 