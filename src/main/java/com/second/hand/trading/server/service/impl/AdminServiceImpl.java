package com.second.hand.trading.server.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.second.hand.trading.server.dao.AdminDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.AdminModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.AdminService;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminDao,AdminModel> implements AdminService {

    @Resource
    private AdminDao adminDao;

    @Resource
    private UserDao userDao;

    public AdminModel login(String accountNumber, String adminPassword){
        return adminDao.login(accountNumber,adminPassword);
    }

    public PageVo<AdminModel> getAdminList(int page, int nums){
        List<AdminModel> list=adminDao.getList((page-1)*nums,nums);
        int count=adminDao.getCount();
        return new PageVo<>(list,count);
    }

    public boolean addAdmin(AdminModel adminModel){
        return adminDao.insert(adminModel)==1;
    }

    /*
    * 管理员修改用户信息
    * */
    @Override
    public boolean updateUser(Long id,String nickname , String password) {

        UserModel userModel = userDao.selectByPrimaryKey(id);
        String s = SecureUtil.sha256(password + userModel.getUserSalt());
        return adminDao.updateUser(id,nickname,s);
    }

}
