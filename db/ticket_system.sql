-- 工单系统数据库初始化脚本

-- 1. 工单类型表
CREATE TABLE IF NOT EXISTS `ticket_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '类型ID',
    `name` VARCHAR(50) NOT NULL COMMENT '类型名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '类型描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单类型表';

-- 2. 工单状态表
CREATE TABLE IF NOT EXISTS `ticket_status` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '状态ID',
    `name` VARCHAR(50) NOT NULL COMMENT '状态名称',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '状态描述',
    `is_system` TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统预置：0-否，1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单状态表';

-- 3. 工单主表
CREATE TABLE IF NOT EXISTS `ticket` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    `title` VARCHAR(200) NOT NULL COMMENT '工单标题',
    `content` TEXT NOT NULL COMMENT '工单内容（支持Markdown+LaTeX富文本）',
    `type_id` BIGINT NOT NULL COMMENT '工单类型ID',
    `status_id` BIGINT NOT NULL COMMENT '工单状态ID',
    `urgency` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '紧急程度：LOW/MEDIUM/HIGH/URGENT',
    `images` TEXT DEFAULT NULL COMMENT '图片URL列表（JSON数组格式）',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `college_id` BIGINT DEFAULT NULL COMMENT '所属学院ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type_id` (`type_id`),
    KEY `idx_status_id` (`status_id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_college_id` (`college_id`),
    KEY `idx_urgency` (`urgency`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单主表';

-- 4. 工单回复表
CREATE TABLE IF NOT EXISTS `ticket_reply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    `ticket_id` BIGINT NOT NULL COMMENT '工单ID',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父回复ID（NULL表示顶层回复）',
    `content` TEXT NOT NULL COMMENT '回复内容（支持Markdown+LaTeX富文本）',
    `creator_id` BIGINT NOT NULL COMMENT '回复者ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单回复表';

-- 5. 工单评价表
CREATE TABLE IF NOT EXISTS `ticket_evaluation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `ticket_id` BIGINT NOT NULL COMMENT '工单ID',
    `satisfaction` VARCHAR(20) NOT NULL COMMENT '满意度：SATISFIED/DISSATISFIED',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ticket_id` (`ticket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单评价表';

-- 6. 插入默认工单状态数据
INSERT INTO `ticket_status` (`name`, `sort_order`, `description`, `is_system`) VALUES
    ('已提出', 1, '工单已创建，等待受理', 1),
    ('已受理', 2, '工单已被管理员受理', 1),
    ('待回复', 3, '等待用户回复', 1),
    ('已完结', 4, '工单已处理完成', 1),
    ('已关闭', 5, '工单已关闭', 1);

-- 插入默认工单类型数据
INSERT INTO `ticket_type` (`name`, `description`) VALUES
    ('功能咨询', '系统功能使用咨询'),
    ('问题反馈', '系统问题或Bug反馈'),
    ('建议投诉', '功能建议或服务投诉'),
    ('其他', '其他类型工单');
