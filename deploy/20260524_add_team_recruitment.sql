--
-- 迁移脚本：添加团队招募功能
-- 适用场景：已有 innovation.sql 基础结构，需要升级到支持团队招募
-- 执行时间：2026-05-24
--

-- ============================================
-- 创建团队招募表
-- ============================================

CREATE TABLE IF NOT EXISTS `team_recruitment`
(
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `team_id` BIGINT NOT NULL COMMENT '团队ID',
    `team_name` VARCHAR(200) NULL COMMENT '团队名称（冗余）',
    `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
    `publisher_name` VARCHAR(50) NULL COMMENT '发布人姓名',
    `position_name` VARCHAR(100) NOT NULL COMMENT '招募职位',
    `task_description` TEXT NOT NULL COMMENT '任务说明',
    `college_preference` VARCHAR(100) NULL COMMENT '学院偏好',
    `major_preference` VARCHAR(100) NULL COMMENT '专业偏好',
    `question_content` TEXT NULL COMMENT '需回答问题',
    `recruit_count` INT NULL DEFAULT 1 COMMENT '招募人数',
    `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN-招募中, CLOSED-已停止',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_team_recruitment_team`
        FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_team_recruitment_publisher`
        FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='团队招募表';

CREATE INDEX `idx_team_recruitment_team_id`
    ON `team_recruitment` (`team_id`);

CREATE INDEX `idx_team_recruitment_publisher_id`
    ON `team_recruitment` (`publisher_id`);

-- ============================================
-- 创建团队招募申请表
-- ============================================

CREATE TABLE IF NOT EXISTS `team_recruitment_application`
(
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `recruitment_id` BIGINT NOT NULL COMMENT '招募ID',
    `team_id` BIGINT NOT NULL COMMENT '团队ID',
    `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
    `applicant_name` VARCHAR(50) NULL COMMENT '申请人姓名',
    `applicant_role` VARCHAR(20) NULL COMMENT '申请人角色',
    `desired_position` VARCHAR(100) NOT NULL COMMENT '申请职位',
    `applicant_college_id` BIGINT NULL COMMENT '申请人学院ID',
    `applicant_college_name` VARCHAR(100) NULL COMMENT '申请人学院名称',
    `applicant_major` VARCHAR(100) NOT NULL COMMENT '申请人专业',
    `qualifications` TEXT NULL COMMENT '特长',
    `answer_content` TEXT NULL COMMENT '问题回答',
    `resume_url` VARCHAR(500) NULL COMMENT '简历链接',
    `remark` TEXT NULL COMMENT '备注',
    `approval_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝, WITHDRAWN-已撤回',
    `approver_id` BIGINT NULL COMMENT '审批人ID',
    `approver_name` VARCHAR(50) NULL COMMENT '审批人姓名',
    `approval_comment` TEXT NULL COMMENT '审批意见',
    `approval_time` DATETIME NULL COMMENT '审批时间',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_team_recruitment_application_recruitment`
        FOREIGN KEY (`recruitment_id`) REFERENCES `team_recruitment` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_team_recruitment_application_team`
        FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_team_recruitment_application_applicant`
        FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_team_recruitment_application_approver`
        FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='团队招募申请表';

CREATE INDEX `idx_team_recruitment_application_team_id`
    ON `team_recruitment_application` (`team_id`);

CREATE INDEX `idx_team_recruitment_application_recruitment_id`
    ON `team_recruitment_application` (`recruitment_id`);

CREATE INDEX `idx_team_recruitment_application_applicant_id`
    ON `team_recruitment_application` (`applicant_id`);

CREATE INDEX `idx_team_recruitment_application_status`
    ON `team_recruitment_application` (`approval_status`);
