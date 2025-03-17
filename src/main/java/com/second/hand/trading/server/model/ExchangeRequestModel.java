package com.second.hand.trading.server.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 闲置易物交换请求模型
 */
@Data
@TableName("exchange_request")
public class ExchangeRequestModel {
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求用户ID
     */
    private Long requestUserId;

    /**
     * 物品所有者ID
     */
    private Long itemOwnerId;

    /**
     * 请求交换的物品ID
     */
    private Long requestItemId;

    /**
     * 提供交换的物品ID
     */
    private Long offerItemId;

    /**
     * 交换理由
     */
    private String exchangeReason;

    /**
     * 状态: 0-待审核, 1-已接受, 2-已拒绝, 3-已完成
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public ExchangeRequestModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(Long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public Long getItemOwnerId() {
        return itemOwnerId;
    }

    public void setItemOwnerId(Long itemOwnerId) {
        this.itemOwnerId = itemOwnerId;
    }

    public Long getRequestItemId() {
        return requestItemId;
    }

    public void setRequestItemId(Long requestItemId) {
        this.requestItemId = requestItemId;
    }

    public Long getOfferItemId() {
        return offerItemId;
    }

    public void setOfferItemId(Long offerItemId) {
        this.offerItemId = offerItemId;
    }

    public String getExchangeReason() {
        return exchangeReason;
    }

    public void setExchangeReason(String exchangeReason) {
        this.exchangeReason = exchangeReason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        return "ExchangeRequestModel{" +
                "id=" + id +
                ", requestUserId=" + requestUserId +
                ", itemOwnerId=" + itemOwnerId +
                ", requestItemId=" + requestItemId +
                ", offerItemId=" + offerItemId +
                ", exchangeReason='" + exchangeReason + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 