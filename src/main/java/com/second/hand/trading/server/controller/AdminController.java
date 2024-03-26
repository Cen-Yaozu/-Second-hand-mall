package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.AdminModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.model.vo.UserVo;
import com.second.hand.trading.server.service.AdminService;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("admin")
public class AdminController{

    @Autowired
    private AdminService adminService;

    @Autowired
    private IdleItemService idleItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public ResultVo login(@RequestParam("accountNumber") @NotNull @NotEmpty String accountNumber,
                          @RequestParam("adminPassword") @NotNull @NotEmpty String adminPassword,
                          HttpSession session){
        // 调用service进行验证
        AdminModel adminModel=adminService.login(accountNumber,adminPassword);
        // 如果返回的实体为空，则返回错误
        if (null == adminModel) {
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }
        // 如果正确，则将用户信息存入session中
        session.setAttribute("admin",adminModel);
        // 返回成功，并将用户信息返回
        return ResultVo.success(adminModel);
    }
    /*
    * 管理员账号登录登出功能
    * */
    @GetMapping("loginOut")
    public ResultVo loginOut( HttpSession session){
        // 在session中移除用户信息
        session.removeAttribute("admin");
        return ResultVo.success();
    }

    @GetMapping("list")
    public ResultVo getAdminList(HttpSession session,
                                 @RequestParam(value = "page",required = false) Integer page,
                                 @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(adminService.getAdminList(p,n));
    }

    @PostMapping("add")
    public ResultVo addAdmin(HttpSession session,
                             @RequestBody AdminModel adminModel){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        if(adminService.addAdmin(adminModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.PARAM_ERROR);
    }

    @GetMapping("idleList")
    public ResultVo idleList(HttpSession session,
                             @RequestParam("status") @NotNull @NotEmpty Integer status,
                             @RequestParam(value = "page",required = false) Integer page,
                             @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(idleItemService.adminGetIdleList(status,p,n));
    }

    /*
    * 更改闲置商品状态
    * */
    @GetMapping("updateIdleStatus")
    public ResultVo updateIdleStatus(HttpSession session,
                                     @RequestParam("id") @NotNull @NotEmpty Long id,
                                     @RequestParam("status") @NotNull @NotEmpty Integer status
                                     ){
        // 判断是否管理员登录
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        // 创建实体封装传来的数据
        IdleItemModel idleItemModel=new IdleItemModel();
        idleItemModel.setId(id);
        idleItemModel.setIdleStatus(status.byteValue());
        // 调用service层方法进行更新商品状态操作
        if(idleItemService.updateIdleItem(idleItemModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /*
    * 获取订单列表
    * */
    @GetMapping("orderList")
    public ResultVo orderList(HttpSession session,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(orderService.getAllOrder(p,n));
    }

    /*
    * 删除订单
    * */
    @DeleteMapping("deleteOrder")
    public ResultVo deleteOrder(HttpSession session,
                              @RequestParam("id") @NotNull @NotEmpty Long id){
        // 验证管理员是否登录
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        // 调用service层方法进行删除订单操作（修改订单状态）
        if(orderService.deleteOrder(id)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /*
    * 查询用户账号信息
    * */
    @GetMapping("userList")
    public ResultVo userList(HttpSession session,
                             @RequestParam(value = "page",required = false) Integer page,
                             @RequestParam(value = "nums",required = false) Integer nums,
                             @RequestParam("status") @NotNull @NotEmpty Integer status){
        // 判断session中的管理员信息是否为空
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        // 分页设置默认值
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        // 查询用户账号信息
        return ResultVo.success(userService.getUserByStatus(status,p,n));
    }

    /*
    * 改变用户状态，实现封禁、解封效果
    * */
    @GetMapping("updateUserStatus")
    public ResultVo updateUserStatus(HttpSession session,
                                     @RequestParam("id") @NotNull @NotEmpty Long id,
                                     @RequestParam("status") @NotNull @NotEmpty Integer status){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        // 封装前端传来的用户信息
        UserModel userModel=new UserModel();
        userModel.setId(id);
        userModel.setUserStatus(status.byteValue());
        // 改变用户状态
        if(userService.updateUserInfo(userModel))
            return ResultVo.success();
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }


    // 按订单闲置物品名称查询
    @GetMapping("queryIdle")
    public ResultVo queryIdle(@RequestParam(value = "findValue",required = false) String findValue,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums,
                              @RequestParam("status") @NotNull @NotEmpty Integer status){

        // 封装前端传来的闲置物品信息
        if(null==findValue){
            findValue="";
        }
        // 封装前端传来的分页信息
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        // 查询商品信息
        if(status == 1)
            return ResultVo.success(idleItemService.findIdleItem(findValue,p,n));
        return ResultVo.success(idleItemService.findIdleItem1(findValue,status,p,n));
    }

    // 按订单号查询订单
    @GetMapping("queryOrder")
    public ResultVo queryOrder(HttpSession session,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums,
                              @RequestParam(value = "searchValue",required = false) String searchValue){

        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }

//        System.out.println("---------------------" + searchValue + "--------------");
        if(null == searchValue || "".equals(searchValue))
            return ResultVo.success(orderService.getAllOrder(p,n));
        return ResultVo.success(orderService.findOrderByNumber(searchValue, p, n));
    }




    // 根据用户账号来查找信息
    @GetMapping("queryUser")
    public ResultVo queryUser(HttpSession session,
                              @RequestParam(value = "searchValue",required = false) String searchValue,
                              @RequestParam(value = "mode",required = false) Integer mode,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
//        return ResultVo.success(userService.getUserByStatus(0,p,n));

        if(mode == 1){
            if(null == searchValue || "".equals(searchValue)){
                return ResultVo.success(userService.getUserByStatus(0,p,n));
            }else{
                return ResultVo.success(userService.getUserByNumber(searchValue,mode));
            }
        }else if(mode == 2){
            if(null == searchValue || "".equals(searchValue)){
                return ResultVo.success(userService.getUserByStatus(1,p,n));
            }else{
                return ResultVo.success(userService.getUserByNumber(searchValue,mode));
            }
        }else
            return ResultVo.success(adminService.getAdminList(p,n));



    }
    /*
     * 后台管理员更新用户信息
     * */
    @PostMapping("/updateUser")
    public ResultVo updateUser(HttpSession session,
                               @RequestBody @NotNull @NotEmpty UserVo userVo){
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        return ResultVo.success(adminService.updateUser(userVo.getId(),userVo.getNickname(),userVo.getPassword()));
    }
}
