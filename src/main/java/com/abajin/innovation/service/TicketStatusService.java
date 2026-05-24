package com.abajin.innovation.service;

import com.abajin.innovation.enums.TicketStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工单状态服务类
 * 状态已改为枚举，不再依赖数据库表
 */
@Slf4j
@Service
public class TicketStatusService {

    /**
     * 获取所有工单状态（从枚举）
     */
    public List<Map<String, Object>> getAllStatuses() {
        return Arrays.stream(TicketStatusEnum.values())
                .map(s -> Map.<String, Object>of(
                        "id", s.getCode(),
                        "name", s.getDescription()
                ))
                .collect(Collectors.toList());
    }
}
