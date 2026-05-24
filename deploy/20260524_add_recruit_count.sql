-- 迁移脚本：为 project_recruitment 表添加招募人数字段
-- 适用场景：已有 project_recruitment 表，需要支持设置招募人数
-- 执行时间：2026-05-24

ALTER TABLE `project_recruitment`
    ADD COLUMN `recruit_count` INT NULL DEFAULT 1 COMMENT '招募人数' AFTER `question_content`;
