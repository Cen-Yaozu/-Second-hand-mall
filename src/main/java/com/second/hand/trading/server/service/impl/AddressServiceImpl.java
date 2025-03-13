package com.second.hand.trading.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.second.hand.trading.server.dao.AddressDao;
import com.second.hand.trading.server.model.AddressModel;
import com.second.hand.trading.server.service.AddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressDao, AddressModel> implements AddressService{

    @Resource
    private AddressDao addressDao;

    /**
     * 查询一个用户的所有地址信息
     * 数据库对user_id建索引，加速查询
     * @param userId
     * @return
     */
    public List<AddressModel> getAddressByUser(Long userId){
        LambdaQueryWrapper<AddressModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressModel::getUserId, userId);
        return list(queryWrapper);
    }

    /**
     * 通过地址id查询地址的信息
     * 同时验证用户身份
     * @param id
     * @param userId
     * @return
     */
    public AddressModel getAddressById(Long id, Long userId){
        LambdaQueryWrapper<AddressModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressModel::getId, id)
                   .eq(AddressModel::getUserId, userId);
        return getOne(queryWrapper);
    }

    /**
     * 新增地址，默认地址的处理逻辑待优化
     * @param addressModel
     * @return
     */
    public boolean addAddress(AddressModel addressModel){
        if(addressModel.getDefaultFlag()){
            // 使用MyBatis-Plus的条件构造器更新用户的所有地址为非默认地址
            LambdaUpdateWrapper<AddressModel> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AddressModel::getUserId, addressModel.getUserId())
                        .set(AddressModel::getDefaultFlag, false);
            update(updateWrapper);
        } else {
            // 判断是否有默认地址，若无，则将当前地址设为默认地址
            LambdaQueryWrapper<AddressModel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AddressModel::getUserId, addressModel.getUserId())
                       .eq(AddressModel::getDefaultFlag, true);
            // 使用count代替list查询，减少IO负担
            long count = count(queryWrapper);
            if(count == 0){
                addressModel.setDefaultFlag(true);
            }
        }
        return save(addressModel);
    }

    /**
     * 更新地址信息，同时要验证用户身份
     * @param addressModel
     * @return
     */
    public boolean updateAddress(AddressModel addressModel){
        if(addressModel.getDefaultFlag()){
            // 先将该用户的所有地址设为非默认
            LambdaUpdateWrapper<AddressModel> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AddressModel::getUserId, addressModel.getUserId())
                        .set(AddressModel::getDefaultFlag, false);
            update(updateWrapper);
        } else {
            // 若取消默认地址，则检查是否为原默认地址，如果是则将第一个地址设置为默认地址
            LambdaQueryWrapper<AddressModel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AddressModel::getUserId, addressModel.getUserId())
                       .eq(AddressModel::getDefaultFlag, true);
            AddressModel defaultAddress = getOne(queryWrapper);
            
            if(defaultAddress != null && defaultAddress.getId().equals(addressModel.getId())){
                // 查找除了当前地址外的第一个地址
                LambdaQueryWrapper<AddressModel> otherQuery = new LambdaQueryWrapper<>();
                otherQuery.eq(AddressModel::getUserId, addressModel.getUserId())
                         .ne(AddressModel::getId, addressModel.getId())
                         .orderByAsc(AddressModel::getId)
                         .last("LIMIT 1");
                AddressModel firstAddress = getOne(otherQuery);
                
                if(firstAddress != null){
                    firstAddress.setDefaultFlag(true);
                    updateById(firstAddress);
                }
            }
        }
        return updateById(addressModel);
    }

    /**
     * 删除地址，同时要验证用户身份
     * @param addressModel
     * @return
     */
    public boolean deleteAddress(AddressModel addressModel){
        LambdaQueryWrapper<AddressModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressModel::getId, addressModel.getId())
                   .eq(AddressModel::getUserId, addressModel.getUserId());
        return remove(queryWrapper);
    }
}
