package com.second.hand.trading.server.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 爱心捐赠请求模型
 */
@Data
@TableName("donation_request")
public class DonationRequestModel {
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 捐赠用户ID
     */
    private Long userId;

    /**
     * 捐赠物品ID
     */
    private Long itemId;

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
     * 处理管理员ID
     */
    private Long adminId;

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

    public DonationRequestModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
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

    @Override
    public String toString() {
        return "DonationRequestModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", donationReason='" + donationReason + '\'' +
                ", donationType=" + donationType +
                ", status=" + status +
                ", adminId=" + adminId +
                ", adminFeedback='" + adminFeedback + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 