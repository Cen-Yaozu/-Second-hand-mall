package com.second.hand.trading.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.second.hand.trading.server.model.MessageModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageDao extends BaseMapper<MessageModel> {
    // 自定义业务方法
    List<MessageModel> getMyMessage(Long userId);

    List<MessageModel> getIdleMessage(Long idleId);
    
    /**
     * 获取用户未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadMessageCount(@Param("userId") Long userId);
    
    /**
     * 将用户的所有消息标记为已读
     * @param userId 用户ID
     * @return 更新的记录数
     */
    int markAllMessagesAsRead(@Param("userId") Long userId);
}