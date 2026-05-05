package com.abajin.innovation.enums;

/**
 * 审批状态枚举
 */
public enum ApprovalStatus {
    PENDING("待审批"),
    APPROVED("已通过"),
    REJECTED("已拒绝"),
    WITHDRAWN("已撤回");

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 获取枚举值的字符串名称（用于数据库存储）
     */
    public String getCode() {
        return this.name();
    }

    /**
     * 根据字符串代码获取枚举值
     */
    public static ApprovalStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        try {
            return ApprovalStatus.valueOf(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown approval status code: " + code);
        }
    }
}
