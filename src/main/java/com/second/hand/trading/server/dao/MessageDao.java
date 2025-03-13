package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.MessageModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageDao extends BaseMapper<MessageModel> {
    // 自定义业务方法
    List<MessageModel> getMyMessage(Long userId);

    List<MessageModel> getIdleMessage(Long idleId);
}