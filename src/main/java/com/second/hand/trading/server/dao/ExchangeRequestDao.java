package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.ExchangeRequestModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 闲置易物交换请求DAO
 */
@Mapper
public interface ExchangeRequestDao extends BaseMapper<ExchangeRequestModel> {
} 