-- 创建捐赠请求表
CREATE TABLE `donation_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '捐赠请求ID',
  `user_id` bigint(20) NOT NULL COMMENT '捐赠用户ID',
  `item_id` bigint(20) NOT NULL COMMENT '捐赠物品ID',
  `donation_reason` varchar(500) DEFAULT NULL COMMENT '捐赠理由',
  `donation_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '捐赠类型：0-公益捐赠，1-环保回收',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已接受，2-已拒绝，3-已完成',
  `admin_id` bigint(20) DEFAULT NULL COMMENT '处理管理员ID',
  `admin_feedback` varchar(500) DEFAULT NULL COMMENT '管理员反馈',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='捐赠请求表'; 