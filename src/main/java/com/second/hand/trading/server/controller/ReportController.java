package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.ReportModel;
import com.second.hand.trading.server.model.vo.ReportVo;
import com.second.hand.trading.server.vo.ResultVo;
import com.second.hand.trading.server.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 举报控制器
 * @author claude
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 创建举报
     * @param report 举报信息
     * @param session 会话信息
     * @return 处理结果
     */
    @PostMapping("/create")
    public ResultVo createReport(@RequestBody ReportModel report, HttpSession session) {
        Long userId = (Long) session.getAttribute("shUserId");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.NOSIGNIN);
        }
        
        report.setReporterId(userId);
        Boolean result = reportService.addReport(report);
        
        if (result) {
            return ResultVo.success("举报提交成功");
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }
    
    /**
     * 获取我的举报记录
     * @param session 会话信息
     * @return 举报列表
     */
    @GetMapping("/my-reports")
    public ResultVo getMyReports(HttpSession session) {
        Long userId = (Long) session.getAttribute("shUserId");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.NOSIGNIN);
        }
        
        List<ReportVo> reports = reportService.getReportsByReporterId(userId);
        return ResultVo.success(reports);
    }
    
    /**
     * 获取举报详情
     * @param id 举报ID
     * @param session 会话信息
     * @return 举报详情
     */
    @GetMapping("/detail/{id}")
    public ResultVo getReportDetail(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("shUserId");
        if (userId == null) {
            return ResultVo.fail(ErrorMsg.NOSIGNIN);
        }
        
        ReportVo report = reportService.getReportById(id);
        
        if (report == null) {
            return ResultVo.fail(ErrorMsg.REPORT_NOT_EXIST);
        }
        
        // 只有举报人和管理员可以查看详情
        Long adminId = (Long) session.getAttribute("shAdminId");
        if (!report.getReporterId().equals(userId) && adminId == null) {
            return ResultVo.fail(ErrorMsg.REPORT_NO_PERMISSION);
        }
        
        return ResultVo.success(report);
    }
    
    /**
     * 管理员获取举报列表
     * @param params 查询参数（可包含status参数）
     * @param session 会话信息
     * @return 举报列表
     */
    @GetMapping("/admin/list")
    public ResultVo getReportList(@RequestParam Map<String, Object> params, HttpSession session) {
        Long adminId = (Long) session.getAttribute("shAdminId");
        if (adminId == null) {
            return ResultVo.fail(ErrorMsg.NOPERMISSION);
        }
        
        List<ReportVo> reports;
        if (params.containsKey("status")) {
            Integer status = Integer.parseInt(params.get("status").toString());
            reports = reportService.getReportsByStatus(status);
        } else {
            reports = reportService.getAllReports();
        }
        
        return ResultVo.success(reports);
    }
    
    /**
     * 管理员处理举报
     * @param id 举报ID
     * @param params 处理参数
     * @param session 会话信息
     * @return 处理结果
     */
    @PutMapping("/admin/handle/{id}")
    public ResultVo handleReport(@PathVariable Long id, @RequestBody Map<String, Object> params, HttpSession session) {
        Long adminId = (Long) session.getAttribute("shAdminId");
        if (adminId == null) {
            return ResultVo.fail(ErrorMsg.NOPERMISSION);
        }
        
        if (!params.containsKey("status") || !params.containsKey("handleResult")) {
            return ResultVo.fail(ErrorMsg.REPORT_PARAM_ERROR);
        }
        
        Integer status = Integer.parseInt(params.get("status").toString());
        String handleResult = params.get("handleResult").toString();
        
        Boolean result = reportService.handleReport(id, adminId, status, handleResult);
        
        if (result) {
            return ResultVo.success("处理成功");
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }
    
    /**
     * 管理员驳回举报
     * @param id 举报ID
     * @param params 处理参数
     * @param session 会话信息
     * @return 处理结果
     */
    @PutMapping("/admin/reject/{id}")
    public ResultVo rejectReport(@PathVariable Long id, @RequestBody Map<String, Object> params, HttpSession session) {
        Long adminId = (Long) session.getAttribute("shAdminId");
        if (adminId == null) {
            return ResultVo.fail(ErrorMsg.NOPERMISSION);
        }
        
        if (!params.containsKey("handleResult")) {
            return ResultVo.fail(ErrorMsg.REPORT_REASON_EMPTY);
        }
        
        String handleResult = params.get("handleResult").toString();
        
        // 状态2表示已驳回
        Boolean result = reportService.handleReport(id, adminId, 2, handleResult);
        
        if (result) {
            return ResultVo.success("驳回成功");
        } else {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }
} 