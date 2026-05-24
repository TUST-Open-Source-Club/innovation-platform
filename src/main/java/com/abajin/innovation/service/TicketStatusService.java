package com.abajin.innovation.service;

import com.abajin.innovation.entity.TicketStatus;
import com.abajin.innovation.mapper.TicketStatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单状态服务类
 */
@Slf4j
@Service
public class TicketStatusService {

    @Autowired
    private TicketStatusMapper ticketStatusMapper;

    public List<TicketStatus> getAllStatuses() {
        return ticketStatusMapper.selectAll();
    }

    public TicketStatus getStatusById(Long id) {
        return ticketStatusMapper.selectById(id);
    }

    public TicketStatus createStatus(TicketStatus ticketStatus) {
        // 检查名称唯一性
        TicketStatus existing = ticketStatusMapper.selectByName(ticketStatus.getName());
        if (existing != null) {
            throw new RuntimeException("工单状态名称已存在");
        }
        ticketStatus.setIsSystem(0); // 自定义状态非系统预置
        ticketStatus.setCreateTime(LocalDateTime.now());
        ticketStatus.setUpdateTime(LocalDateTime.now());
        ticketStatusMapper.insert(ticketStatus);
        return ticketStatus;
    }

    public TicketStatus updateStatus(Long id, TicketStatus ticketStatus) {
        TicketStatus existing = ticketStatusMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("工单状态不存在");
        }
        // 检查名称唯一性（排除自身）
        if (ticketStatus.getName() != null) {
            TicketStatus duplicate = ticketStatusMapper.selectByName(ticketStatus.getName());
            if (duplicate != null && !duplicate.getId().equals(id)) {
                throw new RuntimeException("工单状态名称已存在");
            }
        }
        ticketStatus.setId(id);
        ticketStatus.setUpdateTime(LocalDateTime.now());
        ticketStatusMapper.update(ticketStatus);
        return ticketStatusMapper.selectById(id);
    }

    public void deleteStatus(Long id) {
        TicketStatus existing = ticketStatusMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("工单状态不存在");
        }
        // 系统预置状态不允许删除
        if (existing.getIsSystem() != null && existing.getIsSystem() == 1) {
            throw new RuntimeException("系统预置状态不允许删除");
        }
        // 检查是否有工单处于该状态
        int count = ticketStatusMapper.countTicketsByStatusId(id);
        if (count > 0) {
            throw new RuntimeException("该状态下有工单，不允许删除");
        }
        ticketStatusMapper.deleteById(id);
    }
}
