package com.abajin.innovation.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 工单状态枚举
 * 数据库存储 code（整数 1-5），不依赖 ticket_status 表
 */
public enum TicketStatusEnum {
    SUBMITTED(1, "已提出"),
    ACCEPTED(2, "已受理"),
    PENDING_REPLY(3, "待回复"),
    COMPLETED(4, "已完结"),
    CLOSED(5, "已关闭");

    private final int code;
    private final String description;

    TicketStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static TicketStatusEnum fromCode(int code) {
        for (TicketStatusEnum s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown ticket status code: " + code);
    }
}
