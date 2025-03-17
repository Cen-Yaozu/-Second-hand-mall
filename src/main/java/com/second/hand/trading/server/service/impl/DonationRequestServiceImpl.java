package com.second.hand.trading.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.second.hand.trading.server.dao.DonationRequestDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.DonationRequestModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.model.vo.DonationRequestVo;
import com.second.hand.trading.server.service.DonationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 爱心捐赠请求Service实现
 */
@Service
public class DonationRequestServiceImpl implements DonationRequestService {

    @Autowired
    private DonationRequestDao donationRequestDao;
    
    @Autowired
    private IdleItemDao idleItemDao;
    
    @Autowired
    private UserDao userDao;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public boolean createDonationRequest(DonationRequestModel donationRequest) {
        // 设置初始状态为待审核
        donationRequest.setStatus(0);
        Date now = new Date();
        donationRequest.setCreateTime(now);
        donationRequest.setUpdateTime(now);
        
        // 验证捐赠物品是否存在
        IdleItemModel item = idleItemDao.selectById(donationRequest.getItemId());
        
        if (item == null) {
            return false;
        }
        
        // 验证物品是否属于当前用户
        if (!item.getUserId().equals(donationRequest.getUserId())) {
            return false;
        }
        
        // 验证物品状态是否为在售状态
        if (item.getIdleStatus() != 1) {
            return false;
        }
        
        // 创建捐赠请求
        int result = donationRequestDao.insert(donationRequest);
        
        // 如果创建成功，将物品状态设置为下架状态(2-已下架)
        if (result > 0) {
            item.setIdleStatus(Byte.valueOf("2")); // 设置为下架状态
            item.setReleaseTime(now); // 更新时间
            idleItemDao.updateById(item);
            return true;
        }
        
        return false;
    }

    @Override
    public List<DonationRequestVo> getMyDonationRequests(Long userId) {
        QueryWrapper<DonationRequestModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        List<DonationRequestModel> models = donationRequestDao.selectList(queryWrapper);
        return convertToVoList(models);
    }

    @Override
    @Transactional
    public boolean cancelDonationRequest(Long requestId, Long userId) {
        DonationRequestModel request = donationRequestDao.selectById(requestId);
        
        // 验证请求是否存在
        if (request == null) {
            return false;
        }
        
        // 验证请求是否属于当前用户
        if (!request.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有待审核状态的请求才能取消
        if (request.getStatus() != 0) {
            return false;
        }
        
        // 查询物品信息
        IdleItemModel item = idleItemDao.selectById(request.getItemId());
        if (item == null) {
            return false;
        }
        
        // 更新物品状态为在售状态(1-在售)
        item.setIdleStatus(Byte.valueOf("1"));
        item.setReleaseTime(new Date());
        idleItemDao.updateById(item);
        
        // 删除捐赠请求
        int result = donationRequestDao.deleteById(requestId);
        return result > 0;
    }

    @Override
    public DonationRequestVo getDonationRequestDetail(Long requestId) {
        DonationRequestModel model = donationRequestDao.selectById(requestId);
        if (model == null) {
            return null;
        }
        
        DonationRequestVo vo = new DonationRequestVo(model);
        
        // 获取用户信息
        UserModel user = userDao.selectById(model.getUserId());
        vo.setUser(user);
        
        // 获取物品信息
        IdleItemModel item = idleItemDao.selectById(model.getItemId());
        vo.setItem(item);
        
        // 获取管理员信息（如果有）
        if (model.getAdminId() != null) {
            UserModel admin = userDao.selectById(model.getAdminId());
            vo.setAdmin(admin);
        }
        
        // 格式化时间
        if (model.getCreateTime() != null) {
            vo.setCreateTimeStr(sdf.format(model.getCreateTime()));
        }
        if (model.getUpdateTime() != null) {
            vo.setUpdateTimeStr(sdf.format(model.getUpdateTime()));
        }
        
        return vo;
    }

    @Override
    public List<DonationRequestVo> getAllDonationRequests(Integer status) {
        QueryWrapper<DonationRequestModel> queryWrapper = new QueryWrapper<>();
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        List<DonationRequestModel> models = donationRequestDao.selectList(queryWrapper);
        return convertToVoList(models);
    }

    @Override
    @Transactional
    public boolean reviewDonationRequest(Long requestId, Integer status, Long adminId, String feedback) {
        DonationRequestModel request = donationRequestDao.selectById(requestId);
        
        // 验证请求是否存在
        if (request == null) {
            return false;
        }
        
        // 只有待审核状态的请求才能审核
        if (request.getStatus() != 0) {
            return false;
        }
        
        // 更新请求状态
        request.setStatus(status);
        request.setAdminId(adminId);
        request.setAdminFeedback(feedback);
        request.setUpdateTime(new Date());
        
        int result = donationRequestDao.updateById(request);
        
        // 如果是拒绝，需要将物品状态恢复为在售状态
        if (status == 2) {
            IdleItemModel item = idleItemDao.selectById(request.getItemId());
            if (item != null) {
                item.setIdleStatus(Byte.valueOf("1")); // 设置为在售状态
                item.setReleaseTime(new Date());
                idleItemDao.updateById(item);
            }
        }
        
        return result > 0;
    }

    @Override
    @Transactional
    public boolean completeDonation(Long requestId, Long adminId) {
        DonationRequestModel request = donationRequestDao.selectById(requestId);
        
        // 验证请求是否存在
        if (request == null) {
            return false;
        }
        
        // 只有已接受状态的请求才能完成
        if (request.getStatus() != 1) {
            return false;
        }
        
        // 更新请求状态为已完成
        request.setStatus(3);
        request.setUpdateTime(new Date());
        
        int result = donationRequestDao.updateById(request);
        return result > 0;
    }
    
    /**
     * 将模型列表转换为VO列表
     */
    private List<DonationRequestVo> convertToVoList(List<DonationRequestModel> models) {
        List<DonationRequestVo> voList = new ArrayList<>();
        
        for (DonationRequestModel model : models) {
            DonationRequestVo vo = new DonationRequestVo(model);
            
            // 获取用户信息
            UserModel user = userDao.selectById(model.getUserId());
            vo.setUser(user);
            
            // 获取物品信息
            IdleItemModel item = idleItemDao.selectById(model.getItemId());
            vo.setItem(item);
            
            // 获取管理员信息（如果有）
            if (model.getAdminId() != null) {
                UserModel admin = userDao.selectById(model.getAdminId());
                vo.setAdmin(admin);
            }
            
            // 格式化时间
            if (model.getCreateTime() != null) {
                vo.setCreateTimeStr(sdf.format(model.getCreateTime()));
            }
            if (model.getUpdateTime() != null) {
                vo.setUpdateTimeStr(sdf.format(model.getUpdateTime()));
            }
            
            voList.add(vo);
        }
        
        return voList;
    }
} 