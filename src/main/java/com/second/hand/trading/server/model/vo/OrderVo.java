package com.second.hand.trading.server.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo {
//    private List<IdleItemModel> idleItemModels;
    private List<String> idleItemIds;
    private BigDecimal total_price;
}
