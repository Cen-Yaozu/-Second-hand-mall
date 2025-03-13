package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.AddressModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressDao extends BaseMapper<AddressModel> {
    // 自定义业务方法
    int deleteByPrimaryKeyAndUser(Long id, Long userId);

    List<AddressModel> getAddressByUser(Long userId);

    List<AddressModel> getDefaultAddress(Long userId);

    int updateByUserIdSelective(AddressModel record);
}