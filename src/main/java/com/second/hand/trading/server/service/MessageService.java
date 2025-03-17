package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.MessageModel;

import java.util.List;

public interface MessageService {

    /**
     * 发送留言
     * @param messageModel
     * @return
     */
    boolean addMessage(MessageModel messageModel);

    /**
     * 删除留言
     * @param id
     * @return
     */
    boolean deleteMessage(Long id);

    /**
     * 获取某个留言
     * @param id
     * @return
     */
    MessageModel getMessage(Long id);

    /**
     * 获取某个用户收到的所有留言
     * @param userId
     * @return
     */
    List<MessageModel> getAllMyMessage(Long userId);

    /**
     * 获取某个闲置的所有留言
     * @param idleId
     * @return
     */
    List<MessageModel> getAllIdleMessage(Long idleId);

    /**
     * 获取用户未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadMessageCount(Long userId);

    /**
     * 将用户的所有消息标记为已读
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllMessagesAsRead(Long userId);

}
