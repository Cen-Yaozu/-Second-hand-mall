-- 为消息表添加未读状态字段
ALTER TABLE `sh_message` 
ADD COLUMN `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读' AFTER `to_message`,
ADD INDEX `idx_to_user_is_read` (`to_user`, `is_read`);

-- 更新所有现有消息为已读状态
UPDATE `sh_message` SET `is_read` = 1; 