package com.second.hand.trading.server.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.second.hand.trading.server.model.UserModel;

import java.math.BigDecimal;

public class IdleItemVo {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 闲置物名称
     */
    private String idleName;

    /**
     * 详情
     */
    private String idleDetails;

    /**
     * 图集
     */
    private String pictureList;

    /**
     * 价格
     */
    private BigDecimal idlePrice;

    /**
     * 发货地区
     */
    private String idlePlace;

    /**
     * 状态（发布1、下架2、删除0）
     */
    private Byte idleStatus;

    /**
     * 用户主键id
     */
    private Long userId;

    @TableField(exist = false)
    private UserModel user;

    private static final long serialVersionUID = 1L;

    public IdleItemVo(Long id, String idleName, String idleDetails, String pictureList, BigDecimal idlePrice, String idlePlace, Byte idleStatus, Long userId, UserModel user) {
        this.id = id;
        this.idleName = idleName;
        this.idleDetails = idleDetails;
        this.pictureList = pictureList;
        this.idlePrice = idlePrice;
        this.idlePlace = idlePlace;
        this.idleStatus = idleStatus;
        this.userId = userId;
        this.user = user;
    }

    public IdleItemVo() {
    }

}
