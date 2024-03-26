package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.FavoriteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteDao extends BaseMapper<FavoriteModel> {
    int deleteByPrimaryKey(Long id);

    int insert(FavoriteModel record);

    int insertSelective(FavoriteModel record);

    FavoriteModel selectByPrimaryKey(Long id);

    List<FavoriteModel> getMyFavorite(Long userId);

    Integer checkFavorite(Long userId,Long idleId);

    int updateByPrimaryKeySelective(FavoriteModel record);

    int updateByPrimaryKey(FavoriteModel record);


   int deletefavoriteIds(@Param("List") List<String> favoriteIds);
}