package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.AddressModel;
import com.second.hand.trading.server.service.AddressService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController // 相当于控制层类上加 @Controller + 方法上加@ResponseBody
// 这就意味着当前控制层类中所有方法返回的都是json对象
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /*
    * 获取用户的地址信息
    * */
    @GetMapping("/info")
    public ResultVo  getAddress(@CookieValue("shUserId")
                                    @NotNull(message = "登录异常 请重新登录")
                                    @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                @RequestParam(value = "id",required = false) Long id){

        // 通过id获取用户所拥有的地址信息
		if(null==id){
            return ResultVo.success(addressService.getAddressByUser(Long.valueOf(shUserId)));
        }else {
            return ResultVo.success(addressService.getAddressById(id,Long.valueOf(shUserId)));
        }

    }

    /*
    * 新增地址
    * */

    @PostMapping("/add")
    public ResultVo addAddress(@CookieValue("shUserId")
                                   @NotNull(message = "登录异常 请重新登录")
                                   @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                               @RequestBody AddressModel addressModel){
        // 设置用户id
        addressModel.setUserId(Long.valueOf(shUserId));
        // 新增地址
        if(addressService.addAddress(addressModel)){
            return ResultVo.success(addressModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /*
    * 更新地址
    * */
    @PostMapping("/update")
    public ResultVo updateAddress(@CookieValue("shUserId")
                               @NotNull(message = "登录异常 请重新登录")
                               @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                               @RequestBody AddressModel addressModel){
        addressModel.setUserId(Long.valueOf(shUserId));
        if(addressService.updateAddress(addressModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /*
    * 删除地址
    * */
    @DeleteMapping("/delete")
    public ResultVo deleteAddress(@CookieValue("shUserId")
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                  @RequestBody AddressModel addressModel){
        addressModel.setUserId(Long.valueOf(shUserId));
        if(addressService.deleteAddress(addressModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}
