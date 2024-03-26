package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.FavoriteModel;
import com.second.hand.trading.server.model.vo.FavoriteVo;
import com.second.hand.trading.server.service.FavoriteService;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add")
    public ResultVo addFavorite(@CookieValue("shUserId")
                                    @NotNull(message = "登录异常 请重新登录")
                                    @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                    @RequestBody FavoriteModel favoriteModel){
        favoriteModel.setUserId(Long.valueOf(shUserId));
        favoriteModel.setCreateTime(new Date());
        if(favoriteService.addFavorite(favoriteModel)){
            return ResultVo.success(favoriteModel.getId());
        }
        return ResultVo.fail(ErrorMsg.FAVORITE_EXIT);
    }

    @DeleteMapping("/delete")
    public ResultVo deleteFavorite(@CookieValue("shUserId")
                                       @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                       @RequestParam Long id){
        if(favoriteService.removeById(id)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/check")
    public ResultVo checkFavorite(@CookieValue("shUserId")
                                      @NotNull(message = "登录异常 请重新登录")
                                      @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                  @RequestParam Long idleId){
        return ResultVo.success(favoriteService.isFavorite(Long.valueOf(shUserId),idleId));
    }

    @GetMapping("/my")
    public ResultVo getMyFavorite(@CookieValue("shUserId")
                                    @NotNull(message = "登录异常 请重新登录")
                                    @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        return ResultVo.success(favoriteService.getAllFavorite(Long.valueOf(shUserId)));
    }

    @DeleteMapping("/removeByIds")
    public ResultVo removeFavoriteByIds(@CookieValue("shUserId")
                                            @NotNull(message = "登录异常 请重新登录")
                                            @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                        @RequestBody FavoriteVo favoriteVo){

        //前端传过来的id是当前用户中查询出来的购物车列表中购物车当行的id，所以直接用mybatis-plus中的removeids就可以了
        return ResultVo.success(favoriteService.removefavoriteByIds(favoriteVo));
    }

}
