package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.FavoriteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteDao extends BaseMapper<FavoriteModel> {
    // 自定义业务方法
    List<FavoriteModel> getMyFavorite(Long userId);

    Integer checkFavorite(Long userId, Long idleId);

    int deletefavoriteIds(@Param("List") List<String> favoriteIds);
}