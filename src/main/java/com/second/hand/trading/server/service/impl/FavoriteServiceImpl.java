package com.second.hand.trading.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.second.hand.trading.server.dao.FavoriteDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.model.FavoriteModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.vo.FavoriteVo;
import com.second.hand.trading.server.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteDao, FavoriteModel> implements FavoriteService {

    @Resource
    private FavoriteDao favoriteDao;

    @Resource
    private IdleItemDao idleItemDao;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 新增收藏
     * @param favoriteModel
     * @return
     */
    public boolean addFavorite(FavoriteModel favoriteModel){
        return favoriteDao.insert(favoriteModel)==1;
    }

    /**
     * 删除收藏
     * @param id
     * @return
     */
    public boolean deleteFavorite(Long id){
        return favoriteDao.deleteById(id)==1;
    }

    /**
     * 判断用户是否收藏某个闲置
     * user_id建索引
     * @param userId
     * @param idleId
     * @return
     */
    public Integer isFavorite(Long userId,Long idleId){
        return favoriteDao.checkFavorite(userId,idleId);
    }

    /**
     * 查询一个用户的所有收藏
     * 关联查询，没有用join，通过where in查询关联的闲置信息
     * @param userId
     * @return
     */
    public List<FavoriteModel> getAllFavorite(Long userId){
        List<FavoriteModel> list=favoriteDao.getMyFavorite(userId);
        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(FavoriteModel i:list){
                idleIdList.add(i.getIdleId());
            }
            List<IdleItemModel> idleItemModelList = idleItemDao.selectBatchIds(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:idleItemModelList){
                map.put(idle.getId(),idle);
            }
            for(FavoriteModel i:list){
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }
        return list;
    }

    @Override
    public boolean removefavoriteByIds(FavoriteVo favoriteVo) {

        List<String> favoriteIds = favoriteVo.getFavoriteIds();
        int i = favoriteDao.deletefavoriteIds(favoriteIds);
        return i==1;
    }

}