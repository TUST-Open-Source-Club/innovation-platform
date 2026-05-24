--
-- 迁移脚本：添加项目成员招募功能
-- 适用场景：已有 innovation.sql 基础结构，需要升级到支持项目成员招募
-- 执行时间：2026-05-24
--

-- ============================================
-- 1. 扩展 project_application 表
-- ============================================

ALTER TABLE `project_application`
    ADD COLUMN `desired_position` VARCHAR(100) NULL COMMENT '申请职位' AFTER `application_type`,
    ADD COLUMN `applicant_college_id` BIGINT NULL COMMENT '申请人学院ID' AFTER `desired_position`,
    ADD COLUMN `applicant_college_name` VARCHAR(100) NULL COMMENT '申请人学院名称' AFTER `applicant_college_id`,
    ADD COLUMN `applicant_major` VARCHAR(100) NULL COMMENT '申请人专业' AFTER `applicant_college_name`,
    ADD COLUMN `resume_url` VARCHAR(500) NULL COMMENT '简历链接' AFTER `qualifications`;

CREATE INDEX `idx_project_application_type`
    ON `project_application` (`application_type`);

CREATE INDEX `idx_project_application_applicant_project`
    ON `project_application` (`project_id`, `applicant_id`);

-- ============================================
-- 2. 创建项目招募表
-- ============================================

CREATE TABLE IF NOT EXISTS `project_recruitment`
(
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `project_title` VARCHAR(200) NULL COMMENT '项目标题（冗余）',
    `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
    `publisher_name` VARCHAR(50) NULL COMMENT '发布人姓名',
    `position_name` VARCHAR(100) NOT NULL COMMENT '招募职位',
    `task_description` TEXT NOT NULL COMMENT '任务说明',
    `college_preference` VARCHAR(100) NULL COMMENT '学院偏好',
    `major_preference` VARCHAR(100) NULL COMMENT '专业偏好',
    `question_content` TEXT NULL COMMENT '需回答问题',
    `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN-招募中, CLOSED-已停止',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_recruitment_project`
        FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_project_recruitment_publisher`
        FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目招募表';

CREATE INDEX `idx_project_recruitment_project_id`
    ON `project_recruitment` (`project_id`);

CREATE INDEX `idx_project_recruitment_publisher_id`
    ON `project_recruitment` (`publisher_id`);

-- ============================================
-- 3. 创建项目招募申请表
-- ============================================

CREATE TABLE IF NOT EXISTS `project_recruitment_application`
(
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `recruitment_id` BIGINT NOT NULL COMMENT '招募ID',
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
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
    CONSTRAINT `fk_project_recruitment_application_recruitment`
        FOREIGN KEY (`recruitment_id`) REFERENCES `project_recruitment` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_project_recruitment_application_project`
        FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_project_recruitment_application_applicant`
        FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_project_recruitment_application_approver`
        FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目招募申请表';

CREATE INDEX `idx_project_recruitment_application_project_id`
    ON `project_recruitment_application` (`project_id`);

CREATE INDEX `idx_project_recruitment_application_recruitment_id`
    ON `project_recruitment_application` (`recruitment_id`);

CREATE INDEX `idx_project_recruitment_application_applicant_id`
    ON `project_recruitment_application` (`applicant_id`);

CREATE INDEX `idx_project_recruitment_application_status`
    ON `project_recruitment_application` (`approval_status`);
