package com.second.hand.trading.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.second.hand.trading.server.model.AdminModel;
import com.second.hand.trading.server.vo.PageVo;

public interface AdminService extends IService<AdminModel> {

    AdminModel login(String accountNumber, String adminPassword);

    PageVo<AdminModel> getAdminList(int page, int nums);

    boolean addAdmin(AdminModel adminModel);

    boolean updateUser(Long id,String nickname, String password);

}
