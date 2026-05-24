package com.abajin.innovation.service;

import com.abajin.innovation.entity.TicketType;
import com.abajin.innovation.mapper.TicketTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单类型服务类
 */
@Slf4j
@Service
public class TicketTypeService {

    @Autowired
    private TicketTypeMapper ticketTypeMapper;

    public List<TicketType> getAllTypes() {
        return ticketTypeMapper.selectAll();
    }

    public TicketType getTypeById(Long id) {
        return ticketTypeMapper.selectById(id);
    }

    public TicketType createType(TicketType ticketType) {
        // 检查名称唯一性
        TicketType existing = ticketTypeMapper.selectByName(ticketType.getName());
        if (existing != null) {
            throw new RuntimeException("工单类型名称已存在");
        }
        ticketType.setCreateTime(LocalDateTime.now());
        ticketType.setUpdateTime(LocalDateTime.now());
        ticketTypeMapper.insert(ticketType);
        return ticketType;
    }

    public TicketType updateType(Long id, TicketType ticketType) {
        TicketType existing = ticketTypeMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("工单类型不存在");
        }
        // 检查名称唯一性（排除自身）
        if (ticketType.getName() != null) {
            TicketType duplicate = ticketTypeMapper.selectByName(ticketType.getName());
            if (duplicate != null && !duplicate.getId().equals(id)) {
                throw new RuntimeException("工单类型名称已存在");
            }
        }
        ticketType.setId(id);
        ticketType.setUpdateTime(LocalDateTime.now());
        ticketTypeMapper.update(ticketType);
        return ticketTypeMapper.selectById(id);
    }

    public void deleteType(Long id) {
        TicketType existing = ticketTypeMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("工单类型不存在");
        }
        // 检查是否有工单使用该类型
        int count = ticketTypeMapper.countTicketsByTypeId(id);
        if (count > 0) {
            throw new RuntimeException("该类型下有工单，不允许删除");
        }
        ticketTypeMapper.deleteById(id);
    }
}
