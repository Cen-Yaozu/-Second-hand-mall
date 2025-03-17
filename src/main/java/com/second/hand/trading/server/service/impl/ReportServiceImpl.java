package com.second.hand.trading.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.second.hand.trading.server.dao.ReportDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.ReportModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.model.vo.ReportVo;
import com.second.hand.trading.server.service.ReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 举报服务实现类
 * @author claude
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private ReportDao reportDao;
    
    @Resource
    private UserDao userDao;
    
    @Resource
    private IdleItemDao idleItemDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addReport(ReportModel reportModel) {
        // 设置初始值
        reportModel.setStatus(0); // 默认状态为待处理
        reportModel.setCreateTime(new Date());
        reportModel.setUpdateTime(new Date());
        
        return reportDao.insert(reportModel) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateReport(ReportModel reportModel) {
        reportModel.setUpdateTime(new Date());
        return reportDao.updateById(reportModel) > 0;
    }

    @Override
    public List<ReportVo> getReportsByReporterId(Long reporterId) {
        List<ReportModel> reports = reportDao.findByReporterId(reporterId);
        return convertToVoList(reports);
    }

    @Override
    public List<ReportVo> getReportsByReportedUserId(Long reportedUserId) {
        List<ReportModel> reports = reportDao.findByReportedUserId(reportedUserId);
        return convertToVoList(reports);
    }

    @Override
    public List<ReportVo> getReportsByReportedItemId(Long reportedItemId) {
        List<ReportModel> reports = reportDao.findByReportedItemId(reportedItemId);
        return convertToVoList(reports);
    }

    @Override
    public ReportVo getReportById(Long id) {
        ReportModel report = reportDao.selectById(id);
        if (report == null) {
            return null;
        }
        return convertToVo(report);
    }

    @Override
    public List<ReportVo> getAllReports() {
        QueryWrapper<ReportModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        List<ReportModel> reports = reportDao.selectList(queryWrapper);
        return convertToVoList(reports);
    }

    @Override
    public List<ReportVo> getReportsByStatus(Integer status) {
        List<ReportModel> reports = reportDao.findByStatus(status);
        return convertToVoList(reports);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handleReport(Long id, Long adminId, Integer status, String handleResult) {
        ReportModel report = reportDao.selectById(id);
        if (report == null) {
            return false;
        }
        
        report.setAdminId(adminId);
        report.setStatus(status);
        report.setHandleResult(handleResult);
        report.setUpdateTime(new Date());
        
        return reportDao.updateById(report) > 0;
    }
    
    /**
     * 将举报模型转换为VO对象
     * @param report 举报模型
     * @return 举报VO
     */
    private ReportVo convertToVo(ReportModel report) {
        ReportVo reportVo = new ReportVo();
        BeanUtils.copyProperties(report, reportVo);
        
        // 添加举报人信息
        UserModel reporter = userDao.selectById(report.getReporterId());
        if (reporter != null) {
            reportVo.setReporter(reporter);
        }
        
        // 添加被举报用户信息
        UserModel reportedUser = userDao.selectById(report.getReportedUserId());
        if (reportedUser != null) {
            reportVo.setReportedUser(reportedUser);
        }
        
        // 添加被举报物品信息（如果有）
        if (report.getReportedItemId() != null) {
            IdleItemModel reportedItem = idleItemDao.selectById(report.getReportedItemId());
            if (reportedItem != null) {
                reportVo.setReportedItem(reportedItem);
            }
        }
        
        return reportVo;
    }
    
    /**
     * 将举报模型列表转换为VO对象列表
     * @param reports 举报模型列表
     * @return 举报VO列表
     */
    private List<ReportVo> convertToVoList(List<ReportModel> reports) {
        List<ReportVo> reportVos = new ArrayList<>();
        for (ReportModel report : reports) {
            reportVos.add(convertToVo(report));
        }
        return reportVos;
    }
} 