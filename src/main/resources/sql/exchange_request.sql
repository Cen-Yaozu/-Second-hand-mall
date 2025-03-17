-- 创建闲置易物交换请求表
CREATE TABLE `exchange_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `request_user_id` bigint(20) NOT NULL COMMENT '请求用户ID',
  `item_owner_id` bigint(20) NOT NULL COMMENT '物品所有者ID',
  `request_item_id` bigint(20) NOT NULL COMMENT '请求交换的物品ID',
  `offer_item_id` bigint(20) NOT NULL COMMENT '提供交换的物品ID',
  `exchange_reason` varchar(500) DEFAULT NULL COMMENT '交换理由',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态: 0-待审核, 1-已接受, 2-已拒绝, 3-已完成',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_request_user` (`request_user_id`),
  KEY `idx_item_owner` (`item_owner_id`),
  KEY `idx_request_item` (`request_item_id`),
  KEY `idx_offer_item` (`offer_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闲置易物交换请求表'; 