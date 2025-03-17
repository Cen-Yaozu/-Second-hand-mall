package com.second.hand.trading.server.model.vo;

import com.second.hand.trading.server.model.DonationRequestModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import lombok.Data;

import java.util.Date;

/**
 * 捐赠请求视图对象
 */
@Data
public class DonationRequestVo {
    
    /**
     * 捐赠请求ID
     */
    private Long id;

    /**
     * 捐赠用户信息
     */
    private UserModel user;

    /**
     * 捐赠物品信息
     */
    private IdleItemModel item;

    /**
     * 捐赠理由
     */
    private String donationReason;

    /**
     * 捐赠类型: 0-公益捐赠, 1-环保回收
     */
    private Integer donationType;

    /**
     * 状态: 0-待审核, 1-已接受, 2-已拒绝, 3-已完成
     */
    private Integer status;

    /**
     * 管理员信息
     */
    private UserModel admin;

    /**
     * 管理员反馈
     */
    private String adminFeedback;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 格式化后的创建时间
     */
    private String createTimeStr;

    /**
     * 格式化后的更新时间
     */
    private String updateTimeStr;

    public DonationRequestVo() {
    }

    public DonationRequestVo(DonationRequestModel model) {
        this.id = model.getId();
        this.donationReason = model.getDonationReason();
        this.donationType = model.getDonationType();
        this.status = model.getStatus();
        this.adminFeedback = model.getAdminFeedback();
        this.createTime = model.getCreateTime();
        this.updateTime = model.getUpdateTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public IdleItemModel getItem() {
        return item;
    }

    public void setItem(IdleItemModel item) {
        this.item = item;
    }

    public String getDonationReason() {
        return donationReason;
    }

    public void setDonationReason(String donationReason) {
        this.donationReason = donationReason;
    }

    public Integer getDonationType() {
        return donationType;
    }

    public void setDonationType(Integer donationType) {
        this.donationType = donationType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UserModel getAdmin() {
        return admin;
    }

    public void setAdmin(UserModel admin) {
        this.admin = admin;
    }

    public String getAdminFeedback() {
        return adminFeedback;
    }

    public void setAdminFeedback(String adminFeedback) {
        this.adminFeedback = adminFeedback;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getUpdateTimeStr() {
        return updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }
} 