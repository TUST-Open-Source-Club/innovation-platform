-- ============================================
-- 数据库迁移脚本：添加项目软删除功能
-- 日期：2025-03-17
-- 说明：给 project 表添加 is_deleted 字段实现软删除
-- ============================================

-- 1. 添加软删除标记列
ALTER TABLE project
ADD COLUMN is_deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除（软删除）' AFTER approval_status;

-- 2. 可选：添加索引优化查询性能
-- CREATE INDEX idx_project_is_deleted ON project(is_deleted);

-- ============================================
-- 验证脚本（执行后可检查）
-- ============================================
-- 检查列是否添加成功
-- DESC project;

-- 检查是否有软删除的数据（应该没有）
-- SELECT COUNT(*) as deleted_count FROM project WHERE is_deleted = 1;

-- 检查总数据量
-- SELECT COUNT(*) as total FROM project;
