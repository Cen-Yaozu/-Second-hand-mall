-- ----------------------------
-- 举报信息表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sh_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `reporter_id` bigint(20) NOT NULL COMMENT '举报人ID',
  `reported_user_id` bigint(20) NOT NULL COMMENT '被举报用户ID',
  `reported_item_id` bigint(20) DEFAULT NULL COMMENT '被举报物品ID',
  `report_type` int(11) NOT NULL COMMENT '举报类型：1-虚假信息, 2-违禁物品, 3-欺诈行为, 4-其他',
  `report_reason` varchar(500) NOT NULL COMMENT '举报理由',
  `evidence_urls` varchar(1000) DEFAULT NULL COMMENT '证据URL，多个以逗号分隔',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态: 0-待处理, 1-已处理, 2-已驳回',
  `admin_id` bigint(20) DEFAULT NULL COMMENT '处理管理员ID',
  `handle_result` varchar(500) DEFAULT NULL COMMENT '处理结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_reporter_id` (`reporter_id`),
  KEY `idx_reported_user_id` (`reported_user_id`),
  KEY `idx_reported_item_id` (`reported_item_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报信息表'; 