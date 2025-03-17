package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.DonationRequestModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 爱心捐赠请求DAO
 */
@Mapper
public interface DonationRequestDao extends BaseMapper<DonationRequestModel> {
} 