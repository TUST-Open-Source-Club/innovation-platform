-- ============================================================
-- 创新创业平台数据库初始化脚本
-- 数据库: innovation_platform
-- 字符集: utf8mb4
-- 说明: 包含完整 DDL + 系统基础数据 + 默认管理员账号
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

-- 创建数据库（如不存在）
CREATE DATABASE IF NOT EXISTS `innovation_platform`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `innovation_platform`;

-- ============================================================
-- 1. 基础字典表（无依赖）
-- ============================================================

-- 学院表
DROP TABLE IF EXISTS `college`;
CREATE TABLE `college` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学院名称',
    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院代码',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '学院描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='学院表';

-- 活动类型表
DROP TABLE IF EXISTS `activity_type`;
CREATE TABLE `activity_type` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动类型名称',
    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '活动类型代码',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动类型描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='活动类型表';

-- 基金类型表
DROP TABLE IF EXISTS `fund_type`;
CREATE TABLE `fund_type` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '基金类型名称',
    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '基金类型代码',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '基金类型描述',
    `min_amount` decimal(15,2) DEFAULT NULL COMMENT '最小申请金额',
    `max_amount` decimal(15,2) DEFAULT NULL COMMENT '最大申请金额',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='基金类型表';

-- 新闻分类表
DROP TABLE IF EXISTS `news_category`;
CREATE TABLE `news_category` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类代码',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '分类描述',
    `sort_order` int DEFAULT '0' COMMENT '排序顺序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='新闻分类表';

-- 组织类型表
DROP TABLE IF EXISTS `organization_type`;
CREATE TABLE `organization_type` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织类型名称（如：社团、工作室、实验室等）',
    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织类型代码',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '组织类型描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='组织类型表';

-- 人员类型表
DROP TABLE IF EXISTS `person_type`;
CREATE TABLE `person_type` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '人员类型名称',
    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '人员类型代码',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '人员类型描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='人员类型表';

-- 空间类型表
DROP TABLE IF EXISTS `space_type`;
CREATE TABLE `space_type` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '空间类型名称（如：会议室、实验室、路演厅等）',
    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '空间类型代码',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '空间类型描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='空间类型表';

-- 工单状态表
DROP TABLE IF EXISTS `ticket_status`;
CREATE TABLE `ticket_status` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '状态ID',
    `name` varchar(50) NOT NULL COMMENT '状态名称',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序序号',
    `description` varchar(200) DEFAULT NULL COMMENT '状态描述',
    `is_system` tinyint NOT NULL DEFAULT 0 COMMENT '是否系统预置：0-否，1-是',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单状态表';

-- 工单类型表
DROP TABLE IF EXISTS `ticket_type`;
CREATE TABLE `ticket_type` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
    `name` varchar(50) NOT NULL COMMENT '类型名称',
    `description` varchar(200) DEFAULT NULL COMMENT '类型描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单类型表';

-- ============================================================
-- 2. 用户表（依赖 college）
-- ============================================================

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
    `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
    `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '真实姓名',
    `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
    `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色：STUDENT, TEACHER, COLLEGE_ADMIN, SCHOOL_ADMIN',
    `college_id` bigint DEFAULT NULL COMMENT '所属学院ID',
    `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院名称',
    `status` int DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `auth_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'LOCAL' COMMENT '认证方式：LOCAL-本地密码, CAS-CAS统一认证, BOTH-双认证',
    `cas_uid` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'CAS用户唯一标识（学号/工号）',
    `is_profile_complete` tinyint(1) DEFAULT '1' COMMENT '资料是否完善：0-未完善，1-已完善',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `username` (`username`) USING BTREE,
    UNIQUE KEY `idx_cas_uid` (`cas_uid`),
    KEY `college_id` (`college_id`) USING BTREE,
    KEY `idx_auth_type` (`auth_type`),
    KEY `idx_real_name` (`real_name`),
    KEY `idx_real_name_auth_type` (`real_name`,`auth_type`),
    CONSTRAINT `user_ibfk_1` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

-- ============================================================
-- 3. 业务主表
-- ============================================================

-- 活动申报表
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动标题',
    `activity_type_id` bigint DEFAULT NULL COMMENT '活动类型ID',
    `activity_series` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '活动系列：先锋双创榜样/双创技术讲坛/企业家大讲堂',
    `activity_type_other` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其他活动类型（当活动类型选其他时填写）',
    `organizer_id` bigint NOT NULL COMMENT '组织者ID（用户ID）',
    `organizer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织者姓名',
    `organizer_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织者类型：USER-个人, TEAM-团队, ORGANIZATION-组织',
    `organizer_entity_id` bigint DEFAULT NULL COMMENT '组织者实体ID（团队ID或组织ID）',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '活动地点',
    `space_id` bigint DEFAULT NULL COMMENT '关联预约空间ID',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动描述',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动内容',
    `registration_link` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报名链接',
    `qr_code_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报名二维码图片URL',
    `host_unit_id` bigint DEFAULT NULL COMMENT '主办单位ID（组织）',
    `co_organizer_ids` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '承办单位ID列表，逗号分隔',
    `other_units` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其他单位（文本）',
    `poster_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '海报图片URL（仅管理员可上传）',
    `max_participants` int DEFAULT NULL COMMENT '最大参与人数',
    `registration_deadline` datetime DEFAULT NULL COMMENT '报名截止时间',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝, PUBLISHED-已发布, ONGOING-进行中, COMPLETED-已完成, CANCELLED-已取消',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `activity_type_id` (`activity_type_id`) USING BTREE,
    KEY `organizer_id` (`organizer_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`activity_type_id`) REFERENCES `activity_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `activity_ibfk_2` FOREIGN KEY (`organizer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `activity_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='活动申报表';

-- 入驻申请表
DROP TABLE IF EXISTS `entry_application`;
CREATE TABLE `entry_application` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `applicant_id` bigint NOT NULL COMMENT '申请人ID（用户ID）',
    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
    `applicant_student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人学号',
    `applicant_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人联系方式',
    `team_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创新团队名称',
    `team_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队类型：INNOVATION-创新团队, STARTUP-创业团队, RESEARCH-科研团队',
    `team_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队简介',
    `innovation_direction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创新方向',
    `team_positioning` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '创新团队定位与建设思路（详细描述）',
    `team_size` int DEFAULT NULL COMMENT '团队规模（总人数）',
    `recruitment_requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '招募人员的要求（必填）',
    `instructor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导教师姓名',
    `instructor_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导教师联系方式',
    `campus_mentor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师姓名',
    `campus_mentor_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师联系方式',
    `enterprise_mentor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师姓名',
    `enterprise_mentor_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师联系方式',
    `partner_company` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '合作企业',
    `project_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目名称',
    `project_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目简介',
    `project_achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目成绩（AB类赛事参赛所获最高荣誉奖项等）',
    `expected_outcomes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '预期成果',
    `is_competition_registered` tinyint DEFAULT '0' COMMENT '是否已报名参加竞赛：0-否，1-是',
    `competition_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '竞赛名称（拟报或已报竞赛）',
    `team_members` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目组成员（JSON数组，包含学号、姓名、专业、主要工作）',
    `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
    `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系邮箱',
    `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '附件（JSON数组，存储文件路径）',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PENDING-待审核, APPROVED-已通过, REJECTED-已驳回, ENTERED-已入驻, EXITED-已退出',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `entry_time` datetime DEFAULT NULL COMMENT '入驻时间',
    `exit_time` datetime DEFAULT NULL COMMENT '退出时间',
    `exit_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '退出原因',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `applicant_id` (`applicant_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    CONSTRAINT `entry_application_ibfk_1` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `entry_application_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='入驻申请表';

-- 空间信息表
DROP TABLE IF EXISTS `space`;
CREATE TABLE `space` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '空间名称',
    `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '位置',
    `capacity` int DEFAULT NULL COMMENT '容量（人数）',
    `facilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '设施设备（JSON格式）',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '空间描述',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-可用, MAINTENANCE-维护中, DISABLED-已禁用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_space_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='空间信息表';

-- 团队表
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '团队名称',
    `team_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队类型',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队描述',
    `leader_id` bigint NOT NULL COMMENT '队长ID',
    `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '队长姓名',
    `leader_student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人学号',
    `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院名',
    `member_count` int DEFAULT '0' COMMENT '成员数量',
    `recruiting` tinyint(1) DEFAULT '1' COMMENT '是否招募成员：1-是，0-否',
    `is_public` tinyint(1) DEFAULT '1' COMMENT '团队是否公开：1-是，0-否',
    `recruitment_requirement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '招募人员的要求',
    `honors` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队历史荣誉',
    `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目标签，逗号分隔',
    `instructor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导老师',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `leader_id` (`leader_id`) USING BTREE,
    CONSTRAINT `team_ibfk_1` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='团队表';

-- 项目表
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目标题',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目描述',
    `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目类别',
    `leader_id` bigint DEFAULT NULL COMMENT '项目负责人ID，NULL表示虚位以待',
    `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人姓名',
    `previous_leader_id` bigint DEFAULT NULL COMMENT '上一任负责人ID',
    `previous_leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上一任负责人姓名',
    `previous_leader_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上一任负责人联系方式',
    `instructor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导老师姓名',
    `team_id` bigint DEFAULT NULL COMMENT '团队ID',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, IN_PROGRESS-进行中, COMPLETED-已完成',
    `approval_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审核意见',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
    `review_time` datetime DEFAULT NULL COMMENT '审核时间',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `leader_id` (`leader_id`) USING BTREE,
    KEY `team_id` (`team_id`) USING BTREE,
    CONSTRAINT `project_ibfk_1` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `project_ibfk_2` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目表';

-- 基金申请表
DROP TABLE IF EXISTS `fund_application`;
CREATE TABLE `fund_application` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请标题',
    `fund_type_id` bigint DEFAULT NULL COMMENT '基金类型ID',
    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
    `applicant_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人类型：USER-个人, TEAM-团队, PROJECT-项目',
    `applicant_entity_id` bigint DEFAULT NULL COMMENT '申请人实体ID',
    `project_id` bigint DEFAULT NULL COMMENT '关联项目ID',
    `team_id` bigint DEFAULT NULL COMMENT '关联团队ID',
    `application_amount` decimal(15,2) DEFAULT NULL COMMENT '申请金额',
    `application_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '申请理由',
    `application_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '申请内容',
    `expected_outcomes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '预期成果',
    `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '附件（JSON数组，存储文件路径）',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝, FUNDED-已资助',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `approved_amount` decimal(15,2) DEFAULT NULL COMMENT '批准金额',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `fund_type_id` (`fund_type_id`) USING BTREE,
    KEY `applicant_id` (`applicant_id`) USING BTREE,
    KEY `project_id` (`project_id`) USING BTREE,
    KEY `team_id` (`team_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    CONSTRAINT `fund_application_ibfk_1` FOREIGN KEY (`fund_type_id`) REFERENCES `fund_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fund_application_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fund_application_ibfk_3` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fund_application_ibfk_4` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fund_application_ibfk_5` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='基金申请表';

-- 信息对接记录表
DROP TABLE IF EXISTS `information_link`;
CREATE TABLE `information_link` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源类型：PROJECT-项目, TEAM-团队, FUND-基金申请',
    `source_id` bigint NOT NULL COMMENT '源ID',
    `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标类型：PROJECT-项目, TEAM-团队, FUND-基金申请',
    `target_id` bigint NOT NULL COMMENT '目标ID',
    `link_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联类型（如：合作、引用、关联等）',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '关联说明',
    `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `creator_id` (`creator_id`) USING BTREE,
    KEY `idx_source` (`source_type`,`source_id`) USING BTREE,
    KEY `idx_target` (`target_type`,`target_id`) USING BTREE,
    CONSTRAINT `information_link_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='信息对接记录表';

-- 校企联合创新团队申请表
DROP TABLE IF EXISTS `innovation_team_application`;
CREATE TABLE `innovation_team_application` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `team_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创新团队名称',
    `cooperative_enterprise` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '合作企业',
    `applicant_student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人学号',
    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发起人姓名',
    `applicant_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人联系方式',
    `on_campus_mentor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师姓名',
    `on_campus_mentor_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师联系方式',
    `enterprise_mentor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师姓名',
    `enterprise_mentor_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师联系方式',
    `innovation_direction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创新方向',
    `positioning_and_ideas` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '创新团队定位与建设思路',
    `applicant_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发起人签字',
    `applicant_sign_date` date DEFAULT NULL COMMENT '发起人签字日期',
    `on_campus_mentor_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '校内导师签字（学院盖章）',
    `on_campus_mentor_sign_date` date DEFAULT NULL COMMENT '校内导师签字日期',
    `enterprise_mentor_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业导师签字（企业盖章）',
    `enterprise_mentor_sign_date` date DEFAULT NULL COMMENT '企业导师签字日期',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝',
    `applicant_id` bigint DEFAULT NULL COMMENT '申请人ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `applicant_id` (`applicant_id`) USING BTREE,
    CONSTRAINT `innovation_team_application_ibfk_1` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='校企联合创新团队申请表';

-- 新闻表
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '新闻标题',
    `category_id` bigint DEFAULT NULL COMMENT '分类ID',
    `author_id` bigint DEFAULT NULL COMMENT '作者ID',
    `author_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '作者姓名',
    `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面图片URL',
    `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '摘要',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '新闻内容',
    `source` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源',
    `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '附件（JSON数组，存储文件路径）',
    `related_activity_id` bigint DEFAULT NULL COMMENT '关联活动ID（可选）',
    `view_count` int DEFAULT '0' COMMENT '浏览次数',
    `like_count` int DEFAULT '0' COMMENT '点赞数',
    `is_top` tinyint DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
    `is_published` tinyint DEFAULT '0' COMMENT '是否发布：0-否，1-是',
    `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PENDING-待审核, PUBLISHED-已发布, REJECTED-已驳回',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `category_id` (`category_id`) USING BTREE,
    KEY `author_id` (`author_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    KEY `related_activity_id` (`related_activity_id`) USING BTREE,
    CONSTRAINT `news_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `news_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `news_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `news_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `news_ibfk_4` FOREIGN KEY (`related_activity_id`) REFERENCES `activity` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='新闻表';

-- 组织表
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织名称',
    `organization_type_id` bigint DEFAULT NULL COMMENT '组织类型ID',
    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织代码',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '组织描述',
    `leader_id` bigint DEFAULT NULL COMMENT '负责人ID',
    `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人姓名',
    `member_count` int DEFAULT '0' COMMENT '成员数量',
    `college_id` bigint DEFAULT NULL COMMENT '所属学院ID',
    `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院名称',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, INACTIVE-非活跃, DISBANDED-已解散',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `code` (`code`) USING BTREE,
    KEY `organization_type_id` (`organization_type_id`) USING BTREE,
    KEY `leader_id` (`leader_id`) USING BTREE,
    KEY `college_id` (`college_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    CONSTRAINT `organization_ibfk_1` FOREIGN KEY (`organization_type_id`) REFERENCES `organization_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `organization_ibfk_2` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `organization_ibfk_3` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `organization_ibfk_4` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='组织表';

-- 人员库表
DROP TABLE IF EXISTS `person_library`;
CREATE TABLE `person_library` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `person_type_id` bigint NOT NULL COMMENT '人员类型ID',
    `user_id` bigint DEFAULT NULL COMMENT '关联用户ID（如果是校内人员）',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
    `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '性别：MALE-男, FEMALE-女',
    `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
    `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
    `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像URL',
    `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '职称/头衔',
    `organization` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属单位/企业',
    `position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '职位',
    `research_direction` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '研究方向/专业领域',
    `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '主要成就/荣誉',
    `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '个人简介',
    `expertise_areas` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '专业领域（JSON数组）',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, INACTIVE-非活跃',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'APPROVED' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `person_type_id` (`person_type_id`) USING BTREE,
    KEY `user_id` (`user_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    CONSTRAINT `person_library_ibfk_1` FOREIGN KEY (`person_type_id`) REFERENCES `person_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `person_library_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `person_library_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='人员库表';

-- 项目申请表单表
DROP TABLE IF EXISTS `project_application_form`;
CREATE TABLE `project_application_form` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `instructor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导教师姓名',
    `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '团队负责人姓名',
    `leader_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队负责人联系电话',
    `competition_registered` tinyint DEFAULT '0' COMMENT '是否已报名参加竞赛：0-否，1-是',
    `competition_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '竞赛名称（拟报或已报竞赛）',
    `project_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
    `project_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目简介',
    `project_achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '项目成绩（AB类赛事参赛所获最高荣誉奖项等）',
    `expected_outcomes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '预期成果',
    `leader_declaration` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队负责人声明内容',
    `leader_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队负责人签字',
    `leader_sign_date` date DEFAULT NULL COMMENT '团队负责人签字日期',
    `member_declaration` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '团队成员声明内容',
    `member_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队成员签字',
    `member_sign_date` date DEFAULT NULL COMMENT '团队成员签字日期',
    `instructor_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '指导教师意见',
    `instructor_signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导教师签字（学院盖章）',
    `instructor_sign_date` date DEFAULT NULL COMMENT '指导教师签字日期',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过, REJECTED-已拒绝',
    `applicant_id` bigint DEFAULT NULL COMMENT '申请人ID',
    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `applicant_id` (`applicant_id`) USING BTREE,
    CONSTRAINT `project_application_form_ibfk_1` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目申请表单表';

-- 空间预定表
DROP TABLE IF EXISTS `space_reservation`;
CREATE TABLE `space_reservation` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `space_id` bigint DEFAULT NULL COMMENT '空间ID，选其他时为NULL',
    `custom_space_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其他空间名称',
    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
    `reservation_date` date NOT NULL COMMENT '预定日期',
    `start_time` time NOT NULL COMMENT '开始时间',
    `end_time` time NOT NULL COMMENT '结束时间',
    `purpose` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '使用目的',
    `attendee_count` int DEFAULT NULL COMMENT '预计参与人数',
    `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消, COMPLETED-已完成',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `space_id` (`space_id`) USING BTREE,
    KEY `applicant_id` (`applicant_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    CONSTRAINT `space_reservation_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `space_reservation_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='空间预定表';

-- 工单主表
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    `title` varchar(200) NOT NULL COMMENT '工单标题',
    `content` text NOT NULL COMMENT '工单内容（支持Markdown+LaTeX富文本）',
    `type_id` bigint NOT NULL COMMENT '工单类型ID',
    `status_id` bigint NOT NULL COMMENT '工单状态ID',
    `urgency` varchar(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '紧急程度：LOW/MEDIUM/HIGH/URGENT',
    `images` text DEFAULT NULL COMMENT '图片URL列表（JSON数组格式）',
    `creator_id` bigint NOT NULL COMMENT '创建者ID',
    `college_id` bigint DEFAULT NULL COMMENT '所属学院ID',
    `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_type_id` (`type_id`),
    KEY `idx_status_id` (`status_id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_college_id` (`college_id`),
    KEY `idx_urgency` (`urgency`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单主表';

-- ============================================================
-- 4. 业务从表
-- ============================================================

-- 活动报名表
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `activity_id` bigint NOT NULL COMMENT '活动ID',
    `user_id` bigint NOT NULL COMMENT '报名用户ID',
    `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报名用户姓名',
    `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
    `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
    `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '备注',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_activity_user` (`activity_id`,`user_id`) USING BTREE,
    KEY `user_id` (`user_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    CONSTRAINT `activity_registration_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `activity_registration_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `activity_registration_ibfk_3` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='活动报名表';

-- 活动总结表
DROP TABLE IF EXISTS `activity_summary`;
CREATE TABLE `activity_summary` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `activity_id` bigint NOT NULL COMMENT '活动ID',
    `actual_participants` int DEFAULT NULL COMMENT '实际参与人数',
    `summary_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动总结内容',
    `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动成果',
    `photos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '活动照片（JSON数组，存储文件路径）',
    `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '附件（JSON数组，存储文件路径）',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, SUBMITTED-已提交, APPROVED-已通过',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `reviewer_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `review_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_activity_summary` (`activity_id`) USING BTREE,
    KEY `reviewer_id` (`reviewer_id`) USING BTREE,
    CONSTRAINT `activity_summary_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `activity_summary_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='活动总结表';

-- 入驻空间分配表
DROP TABLE IF EXISTS `entry_space_allocation`;
CREATE TABLE `entry_space_allocation` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `entry_application_id` bigint NOT NULL COMMENT '入驻申请ID',
    `space_id` bigint NOT NULL COMMENT '分配的空间ID',
    `start_date` date DEFAULT NULL COMMENT '开始日期',
    `end_date` date DEFAULT NULL COMMENT '结束日期',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-使用中, EXPIRED-已过期, RELEASED-已释放',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `entry_application_id` (`entry_application_id`) USING BTREE,
    KEY `space_id` (`space_id`) USING BTREE,
    CONSTRAINT `entry_space_allocation_ibfk_1` FOREIGN KEY (`entry_application_id`) REFERENCES `entry_application` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `entry_space_allocation_ibfk_2` FOREIGN KEY (`space_id`) REFERENCES `space` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='入驻空间分配表';

-- 组织成员表
DROP TABLE IF EXISTS `organization_member`;
CREATE TABLE `organization_member` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `organization_id` bigint NOT NULL COMMENT '组织ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户姓名',
    `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色：LEADER-负责人, MEMBER-成员, ADVISOR-顾问',
    `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, INACTIVE-非活跃',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_org_user` (`organization_id`,`user_id`) USING BTREE,
    KEY `user_id` (`user_id`) USING BTREE,
    CONSTRAINT `organization_member_ibfk_1` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `organization_member_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='组织成员表';

-- 项目申请表
DROP TABLE IF EXISTS `project_application`;
CREATE TABLE `project_application` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `application_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请编号',
    `project_id` bigint NOT NULL COMMENT '项目ID',
    `project_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目标题（冗余字段，便于查询）',
    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
    `applicant_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人角色：STUDENT-学生, TEACHER-教师',
    `application_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请类型：JOIN_TEAM-加入团队, TAKE_OVER-接管项目',
    `desired_position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请职位',
    `applicant_college_id` bigint DEFAULT NULL COMMENT '申请人学院ID',
    `applicant_college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人学院名称',
    `applicant_major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人专业',
    `application_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '申请内容',
    `qualifications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '资质说明',
    `resume_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简历链接',
    `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
    `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系邮箱',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `approver_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审批人角色：PROJECT_LEADER-项目负责人, COLLEGE_ADMIN-学院管理员',
    `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审批人姓名',
    `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
    `approval_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `status` int DEFAULT '1' COMMENT '状态：1-有效, 0-无效',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `application_no` (`application_no`) USING BTREE,
    KEY `approver_id` (`approver_id`) USING BTREE,
    KEY `idx_project_id` (`project_id`) USING BTREE,
    KEY `idx_applicant_id` (`applicant_id`) USING BTREE,
    KEY `idx_approval_status` (`approval_status`) USING BTREE,
    KEY `idx_approver_role` (`approver_role`) USING BTREE,
    KEY `idx_project_application_type` (`application_type`) USING BTREE,
    KEY `idx_project_application_applicant_project` (`project_id`, `applicant_id`) USING BTREE,
    CONSTRAINT `project_application_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `project_application_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `project_application_ibfk_3` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目申请表';

-- 项目申请表单成员表
DROP TABLE IF EXISTS `project_application_member`;
CREATE TABLE `project_application_member` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `application_form_id` bigint NOT NULL COMMENT '申请表单ID',
    `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学号',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '姓名',
    `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '专业',
    `main_work` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '主要工作',
    `signature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '签名',
    `sort_order` int DEFAULT '0' COMMENT '排序顺序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `application_form_id` (`application_form_id`) USING BTREE,
    CONSTRAINT `project_application_member_ibfk_1` FOREIGN KEY (`application_form_id`) REFERENCES `project_application_form` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目申请表单成员表';

-- 项目文件表
DROP TABLE IF EXISTS `project_file`;
CREATE TABLE `project_file` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `project_id` bigint NOT NULL COMMENT '项目ID',
    `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
    `original_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '原始文件名',
    `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件路径',
    `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件类型',
    `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
    `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
    `upload_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上传用户姓名',
    `upload_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `project_id` (`project_id`) USING BTREE,
    KEY `upload_user_id` (`upload_user_id`) USING BTREE,
    CONSTRAINT `project_file_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `project_file_ibfk_2` FOREIGN KEY (`upload_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目文件表';

-- 团队成员表
DROP TABLE IF EXISTS `team_member`;
CREATE TABLE `team_member` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `team_id` bigint NOT NULL COMMENT '团队ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户姓名',
    `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色：LEADER-队长, MEMBER-成员',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常, INACTIVE-已退出',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'APPROVED' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
    `join_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学号',
    `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '年级',
    `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '专业',
    `competition_experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '比赛经历',
    `awards` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '获奖情况',
    `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
    `resume_attachment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简历附件URL或路径',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_team_user` (`team_id`,`user_id`) USING BTREE,
    KEY `user_id` (`user_id`) USING BTREE,
    CONSTRAINT `team_member_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `team_member_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='团队成员表';

-- 工单回复表
DROP TABLE IF EXISTS `ticket_reply`;
CREATE TABLE `ticket_reply` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    `ticket_id` bigint NOT NULL COMMENT '工单ID',
    `parent_id` bigint DEFAULT NULL COMMENT '父回复ID（NULL表示顶层回复）',
    `content` text NOT NULL COMMENT '回复内容（支持Markdown+LaTeX富文本）',
    `creator_id` bigint NOT NULL COMMENT '回复者ID',
    `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单回复表';

-- 工单评价表
DROP TABLE IF EXISTS `ticket_evaluation`;
CREATE TABLE `ticket_evaluation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `ticket_id` bigint NOT NULL COMMENT '工单ID',
    `satisfaction` varchar(20) NOT NULL COMMENT '满意度：SATISFIED/DISSATISFIED',
    `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ticket_id` (`ticket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单评价表';

-- ============================================================
-- 5. 招募相关表（后续功能迭代新增）
-- ============================================================

-- 项目招募表
DROP TABLE IF EXISTS `project_recruitment`;
CREATE TABLE `project_recruitment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `project_id` bigint NOT NULL COMMENT '项目ID',
    `project_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目标题（冗余）',
    `publisher_id` bigint NOT NULL COMMENT '发布人ID',
    `publisher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发布人姓名',
    `position_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '招募职位',
    `task_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务说明',
    `college_preference` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院偏好',
    `major_preference` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '专业偏好',
    `question_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '需回答问题',
    `recruit_count` int NULL DEFAULT 1 COMMENT '招募人数',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN-招募中, CLOSED-已停止',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_project_recruitment_project_id` (`project_id`) USING BTREE,
    KEY `idx_project_recruitment_publisher_id` (`publisher_id`) USING BTREE,
    CONSTRAINT `project_recruitment_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `project_recruitment_ibfk_2` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目招募表';

-- 项目招募申请表
DROP TABLE IF EXISTS `project_recruitment_application`;
CREATE TABLE `project_recruitment_application` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `recruitment_id` bigint NOT NULL COMMENT '招募ID',
    `project_id` bigint NOT NULL COMMENT '项目ID',
    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
    `applicant_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人角色',
    `desired_position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请职位',
    `applicant_college_id` bigint DEFAULT NULL COMMENT '申请人学院ID',
    `applicant_college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人学院名称',
    `applicant_major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人专业',
    `qualifications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '特长',
    `answer_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '问题回答',
    `resume_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简历链接',
    `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '备注',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝, WITHDRAWN-已撤回',
    `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审批人姓名',
    `approval_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_pra_project_id` (`project_id`) USING BTREE,
    KEY `idx_pra_recruitment_id` (`recruitment_id`) USING BTREE,
    KEY `idx_pra_applicant_id` (`applicant_id`) USING BTREE,
    KEY `idx_pra_approval_status` (`approval_status`) USING BTREE,
    CONSTRAINT `project_recruitment_application_ibfk_1` FOREIGN KEY (`recruitment_id`) REFERENCES `project_recruitment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `project_recruitment_application_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `project_recruitment_application_ibfk_3` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `project_recruitment_application_ibfk_4` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目招募申请表';

-- 团队招募表
DROP TABLE IF EXISTS `team_recruitment`;
CREATE TABLE `team_recruitment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `team_id` bigint NOT NULL COMMENT '团队ID',
    `team_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '团队名称（冗余）',
    `publisher_id` bigint NOT NULL COMMENT '发布人ID',
    `publisher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发布人姓名',
    `position_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '招募职位',
    `task_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务说明',
    `college_preference` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学院偏好',
    `major_preference` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '专业偏好',
    `question_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '需回答问题',
    `recruit_count` int DEFAULT 1 COMMENT '招募人数',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN-招募中, CLOSED-已停止',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_team_recruitment_team_id` (`team_id`) USING BTREE,
    KEY `idx_team_recruitment_publisher_id` (`publisher_id`) USING BTREE,
    CONSTRAINT `fk_team_recruitment_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fk_team_recruitment_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='团队招募表';

-- 团队招募申请表
DROP TABLE IF EXISTS `team_recruitment_application`;
CREATE TABLE `team_recruitment_application` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `recruitment_id` bigint NOT NULL COMMENT '招募ID',
    `team_id` bigint NOT NULL COMMENT '团队ID',
    `applicant_id` bigint NOT NULL COMMENT '申请人ID',
    `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
    `applicant_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人角色',
    `desired_position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请职位',
    `applicant_college_id` bigint DEFAULT NULL COMMENT '申请人学院ID',
    `applicant_college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人学院名称',
    `applicant_major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人专业',
    `qualifications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '特长',
    `answer_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '问题回答',
    `resume_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简历链接',
    `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '备注',
    `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝, WITHDRAWN-已撤回',
    `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
    `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审批人姓名',
    `approval_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审批意见',
    `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_team_recruitment_application_team_id` (`team_id`) USING BTREE,
    KEY `idx_team_recruitment_application_recruitment_id` (`recruitment_id`) USING BTREE,
    KEY `idx_team_recruitment_application_applicant_id` (`applicant_id`) USING BTREE,
    KEY `idx_team_recruitment_application_status` (`approval_status`) USING BTREE,
    CONSTRAINT `fk_team_recruitment_application_recruitment` FOREIGN KEY (`recruitment_id`) REFERENCES `team_recruitment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fk_team_recruitment_application_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fk_team_recruitment_application_applicant` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_team_recruitment_application_approver` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='团队招募申请表';

-- ============================================================
-- 6. 初始数据
-- ============================================================

-- 6.1 默认管理员账号
-- 用户名: admin  密码: admin123  (BCrypt加密)
-- 角色: SCHOOL_ADMIN (校级管理员)
INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `email`, `phone`, `role`, `college_id`, `college_name`, `status`, `auth_type`, `cas_uid`, `is_profile_complete`, `create_time`, `update_time`)
VALUES (1, 'admin', '$2b$10$N6ZBMc0qOvlInnq97OACKebJRXTvjyH.oLDTVt68/OZ4vDLToOX.m', '系统管理员', 'admin@example.edu.cn', '13800000000', 'SCHOOL_ADMIN', NULL, NULL, 1, 'LOCAL', NULL, 1, NOW(), NOW());

-- 6.2 工单系统默认状态
INSERT INTO `ticket_status` (`name`, `sort_order`, `description`, `is_system`) VALUES
    ('已提出', 1, '工单已创建，等待受理', 1),
    ('已受理', 2, '工单已被管理员受理', 1),
    ('待回复', 3, '等待用户回复', 1),
    ('已完结', 4, '工单已处理完成', 1),
    ('已关闭', 5, '工单已关闭', 1);

-- 6.3 工单系统默认类型
INSERT INTO `ticket_type` (`name`, `description`) VALUES
    ('功能咨询', '系统功能使用咨询'),
    ('问题反馈', '系统问题或Bug反馈'),
    ('建议投诉', '功能建议或服务投诉'),
    ('其他', '其他类型工单');

-- 6.4 可选基础数据（可按需删除或修改）
-- 示例学院
INSERT INTO `college` (`name`, `code`, `description`) VALUES
    ('计算机科学与技术学院', 'CS', '计算机学院'),
    ('电子信息学院', 'EE', '电子信息学院'),
    ('机械工程学院', 'ME', '机械工程学院'),
    ('经济管理学院', 'EM', '经济管理学院'),
    ('创新创业学院', 'IE', '创新创业学院');

-- 示例活动类型
INSERT INTO `activity_type` (`name`, `code`, `description`) VALUES
    ('双创技术讲坛', 'TECH_TALK', '技术分享与讲座'),
    ('企业家大讲堂', 'ENTREPRENEUR_TALK', '企业家经验分享'),
    ('先锋双创榜样', 'ROLE_MODEL', '优秀榜样展示'),
    ('竞赛宣讲', 'COMPETITION', '各类竞赛宣讲活动'),
    ('其他', 'OTHER', '其他类型活动');

-- 示例基金类型
INSERT INTO `fund_type` (`name`, `code`, `description`, `min_amount`, `max_amount`) VALUES
    ('创业启动基金', 'STARTUP_FUND', '用于创业项目启动阶段的资金支持', 1000.00, 50000.00),
    ('创新实践基金', 'INNOVATION_FUND', '用于学生创新实践活动', 500.00, 20000.00),
    ('竞赛奖励基金', 'COMPETITION_FUND', '用于各类竞赛的奖励和补贴', 1000.00, 100000.00);

-- 示例新闻分类
INSERT INTO `news_category` (`name`, `code`, `description`, `sort_order`) VALUES
    ('通知公告', 'NOTICE', '学校及学院通知公告', 1),
    ('活动预告', 'EVENT', '各类活动预告信息', 2),
    ('成果展示', 'ACHIEVEMENT', '学生创新创业成果展示', 3),
    ('政策解读', 'POLICY', '各类政策解读', 4),
    ('媒体报道', 'MEDIA', '外部媒体报道', 5);

-- 示例组织类型
INSERT INTO `organization_type` (`name`, `code`, `description`) VALUES
    ('学生社团', 'CLUB', '各类学生社团组织'),
    ('创新工作室', 'STUDIO', '校内创新实践工作室'),
    ('科研实验室', 'LAB', '科研方向实验室'),
    ('创业团队', 'STARTUP_TEAM', '已注册或拟注册创业团队');

-- 示例人员类型
INSERT INTO `person_type` (`name`, `code`, `description`) VALUES
    ('校内导师', 'CAMPUS_MENTOR', '校内指导教师'),
    ('企业导师', 'ENTERPRISE_MENTOR', '企业合作导师'),
    ('投资人', 'INVESTOR', '潜在投资人'),
    ('优秀校友', 'ALUMNUS', '优秀毕业校友');

-- 示例空间类型
INSERT INTO `space_type` (`name`, `code`, `description`) VALUES
    ('会议室', 'MEETING_ROOM', '各类大小会议室'),
    ('实验室', 'LAB', '专业实验室'),
    ('路演厅', 'ROADSHOW_HALL', '项目路演展示厅'),
    ('工位', 'WORKSTATION', '开放办公工位'),
    ('其他', 'OTHER', '其他类型空间');

-- ============================================================
-- 7. 恢复约束检查
-- ============================================================
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
