package com.second.hand.trading.server.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * sh_report - 举报信息表
 * @author claude
 */
@TableName("sh_report")
public class ReportModel implements Serializable {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 举报人ID
     */
    private Long reporterId;

    /**
     * 被举报用户ID
     */
    private Long reportedUserId;

    /**
     * 被举报物品ID，可选
     */
    private Long reportedItemId;

    /**
     * 举报类型：1-虚假信息, 2-违禁物品, 3-欺诈行为, 4-其他
     */
    private Integer reportType;

    /**
     * 举报理由
     */
    private String reportReason;

    /**
     * 证据URL，多个以逗号分隔
     */
    private String evidenceUrls;

    /**
     * 状态: 0-待处理, 1-已处理, 2-已驳回
     */
    private Integer status;

    /**
     * 处理管理员ID
     */
    private Long adminId;

    /**
     * 处理结果
     */
    private String handleResult;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public Long getReportedItemId() {
        return reportedItemId;
    }

    public void setReportedItemId(Long reportedItemId) {
        this.reportedItemId = reportedItemId;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }

    public String getEvidenceUrls() {
        return evidenceUrls;
    }

    public void setEvidenceUrls(String evidenceUrls) {
        this.evidenceUrls = evidenceUrls;
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

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
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
        return "ReportModel{" +
                "id=" + id +
                ", reporterId=" + reporterId +
                ", reportedUserId=" + reportedUserId +
                ", reportedItemId=" + reportedItemId +
                ", reportType=" + reportType +
                ", reportReason='" + reportReason + '\'' +
                ", evidenceUrls='" + evidenceUrls + '\'' +
                ", status=" + status +
                ", adminId=" + adminId +
                ", handleResult='" + handleResult + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 