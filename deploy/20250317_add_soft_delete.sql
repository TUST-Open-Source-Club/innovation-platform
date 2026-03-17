-- ============================================
-- 数据库迁移脚本：添加活动软删除功能
-- 日期：2025-03-17
-- 说明：将逻辑删除从 CANCELLED 状态改为 is_deleted 字段
-- ============================================

-- 1. 添加软删除标记列
ALTER TABLE activity
ADD COLUMN is_deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除（软删除）' AFTER status;


-- 4. 添加索引优化查询性能（可选）
-- CREATE INDEX idx_is_deleted ON activity(is_deleted);

-- ============================================
-- 验证脚本（执行后可检查）
-- ============================================
-- 检查列是否添加成功
-- DESC activity;

-- 检查是否有软删除的数据
-- SELECT id, title, status, is_deleted FROM activity WHERE is_deleted = 1;

-- 检查总数据量
-- SELECT COUNT(*) as total, SUM(is_deleted) as deleted_count FROM activity;
