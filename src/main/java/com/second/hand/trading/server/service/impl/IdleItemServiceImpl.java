package com.second.hand.trading.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IdleItemServiceImpl  extends ServiceImpl<IdleItemDao, IdleItemModel> implements IdleItemService {

    @Resource
    private IdleItemDao idleItemDao;

    @Resource
    private UserDao userDao;

    /**
     * 发布闲置
     * @param idleItemModel
     * @return
     */
    public boolean addIdleItem(IdleItemModel idleItemModel) {
        return save(idleItemModel);
    }

    /**
     * 查询闲置信息，同时查出发布者的信息
     * @param id
     * @return
     */
    public IdleItemModel getIdleItem(Long id) {
        IdleItemModel idleItemModel = getById(id);
        if(idleItemModel != null){
            idleItemModel.setUser(userDao.selectById(idleItemModel.getUserId()));
        }
        return idleItemModel;
    }

    /**
     * 查询用户发布的所有闲置
     * user_id建索引
     * @param userId
     * @return
     */
    public List<IdleItemModel> getAllIdelItem(Long userId) {
        return idleItemDao.getAllIdleItem(userId);
    }

    /**
     * 搜索，分页
     * 同时查出闲置发布者的信息
     * @param findValue
     * @param page
     * @param nums
     * @return
     */
    public PageVo<IdleItemModel> findIdleItem(String findValue, int page, int nums) {
        List<IdleItemModel> list=idleItemDao.findIdleItem(findValue, (page - 1) * nums, nums);

        for (IdleItemModel i:list) {
            System.out.println(i.getIdleName() + ' ' + i.getIdleStatus() + '\n');
        }

        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }

        int count=idleItemDao.countIdleItem(findValue);
        return new PageVo<>(list,count);
    }



    /**
     * 根据标签查找闲置物品的分页信息。
     *
     * @param idleLabel 闲置物品的标签。
     * @param page 请求的页码。
     * @param nums 每页显示的数量。
     * @return 返回包含闲置物品列表和总数的分页信息对象。
     */
    public PageVo<IdleItemModel> findIdleItemByLable(int idleLabel, int page, int nums) {
        // 通过标签、页码和每页数量从数据库获取闲置物品列表
        List<IdleItemModel> list=idleItemDao.findIdleItemByLable(idleLabel, (page - 1) * nums, nums);
        if(list.size()>0){
            // 初始化用户ID列表，用于后续查询用户信息
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            // 根据用户ID列表查询用户信息
            List<UserModel> userList=userDao.findUserByList(idList);
            // 将用户信息映射到ID上，方便后续关联闲置物品和用户
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            // 关联每个闲置物品和它的用户信息
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }
        // 统计符合标签条件的闲置物品总数
        int count=idleItemDao.countIdleItemByLable(idleLabel);
        // 构造并返回分页信息对象
        return new PageVo<>(list,count);
    }


    /**
     * 更新闲置信息
     * @param idleItemModel
     * @return
     */
    public boolean updateIdleItem(IdleItemModel idleItemModel){
        return updateById(idleItemModel);
    }

    public PageVo<IdleItemModel> adminGetIdleList(int status, int page, int nums) {
        List<IdleItemModel> list=idleItemDao.getIdleItemByStatus(status, (page - 1) * nums, nums);
        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }
        int count=idleItemDao.countIdleItemByStatus(status);
        return new PageVo<>(list,count);
    }

    @Override
    public boolean isMyIdle(Long shUserId, Long idleId) {
        IdleItemModel idleItemModel = getById(idleId);
        return idleItemModel != null && idleItemModel.getUserId().equals(shUserId);
    }


    // 根据不同的状态查找闲置物品
    @Override
    public PageVo<IdleItemModel> findIdleItem1(String findValue, int status, int page, int nums) {

        List<IdleItemModel> list=idleItemDao.findIdleItem1(findValue, status, (page - 1) * nums, nums);

        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }
        int count=idleItemDao.countIdleItem(findValue);

//        System.out.println("------------------------------------下架的--------------------------");
//        for (IdleItemModel i:list) {
//
//            System.out.println(i.getIdleName());
//            System.out.println(i.getIdleStatus());
//            System.out.println(i.getIdlePlace());
//            System.out.println(i.getReleaseTime());
//            System.out.println(i.getPictureList());
//            System.out.println(i.getIdlePrice());
//            System.out.println(i.getIdleLabel());
//
//        }

        return new PageVo<>(list,count);

    }
}
