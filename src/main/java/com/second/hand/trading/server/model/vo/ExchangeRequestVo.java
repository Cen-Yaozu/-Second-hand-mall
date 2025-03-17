package com.second.hand.trading.server.model.vo;

import com.second.hand.trading.server.model.ExchangeRequestModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import lombok.Data;

import java.util.Date;

/**
 * 闲置易物交换请求VO，包含请求相关的详细信息
 */
@Data
public class ExchangeRequestVo {
    
    /**
     * 交换请求ID
     */
    private Long id;

    /**
     * 请求用户ID
     */
    private Long requestUserId;
    
    /**
     * 请求用户信息
     */
    private UserModel requestUser;

    /**
     * 物品所有者ID
     */
    private Long itemOwnerId;
    
    /**
     * 物品所有者信息
     */
    private UserModel itemOwner;

    /**
     * 请求交换的物品ID
     */
    private Long requestItemId;
    
    /**
     * 请求交换的物品信息
     */
    private IdleItemModel requestItem;

    /**
     * 提供交换的物品ID
     */
    private Long offerItemId;
    
    /**
     * 提供交换的物品信息
     */
    private IdleItemModel offerItem;

    /**
     * 交换理由
     */
    private String exchangeReason;

    /**
     * 状态: 0-待审核, 1-已接受, 2-已拒绝, 3-已完成
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 将Model转换为VO对象
     * @param model 交换请求模型
     * @return 交换请求VO
     */
    public static ExchangeRequestVo fromModel(ExchangeRequestModel model) {
        ExchangeRequestVo vo = new ExchangeRequestVo();
        vo.setId(model.getId());
        vo.setRequestUserId(model.getRequestUserId());
        vo.setItemOwnerId(model.getItemOwnerId());
        vo.setRequestItemId(model.getRequestItemId());
        vo.setOfferItemId(model.getOfferItemId());
        vo.setExchangeReason(model.getExchangeReason());
        vo.setStatus(model.getStatus());
        vo.setCreateTime(model.getCreateTime());
        vo.setUpdateTime(model.getUpdateTime());
        
        // 设置状态描述
        switch (model.getStatus()) {
            case 0:
                vo.setStatusDesc("待审核");
                break;
            case 1:
                vo.setStatusDesc("已接受");
                break;
            case 2:
                vo.setStatusDesc("已拒绝");
                break;
            case 3:
                vo.setStatusDesc("已完成");
                break;
            default:
                vo.setStatusDesc("未知状态");
        }
        
        return vo;
    }
} 