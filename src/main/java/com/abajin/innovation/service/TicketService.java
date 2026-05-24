package com.abajin.innovation.service;

import com.abajin.innovation.common.PageResult;
import com.abajin.innovation.dto.CreateTicketDTO;
import com.abajin.innovation.dto.TicketQueryDTO;
import com.abajin.innovation.dto.UpdateTicketDTO;
import com.abajin.innovation.entity.Ticket;
import com.abajin.innovation.entity.TicketReply;
import com.abajin.innovation.entity.TicketStatus;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.TicketMapper;
import com.abajin.innovation.mapper.TicketReplyMapper;
import com.abajin.innovation.mapper.TicketStatusMapper;
import com.abajin.innovation.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 工单服务类
 */
@Slf4j
@Service
public class TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketStatusMapper ticketStatusMapper;

    @Autowired
    private TicketReplyMapper ticketReplyMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建工单
     */
    public Ticket createTicket(CreateTicketDTO dto, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 获取默认状态（已提出）
        TicketStatus defaultStatus = ticketStatusMapper.selectByName("已提出");
        if (defaultStatus == null) {
            throw new RuntimeException("系统错误：默认状态不存在");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setContent(dto.getContent());
        ticket.setTypeId(dto.getTypeId());
        ticket.setStatusId(defaultStatus.getId());
        ticket.setUrgency(dto.getUrgency());
        ticket.setImages(dto.getImages());
        ticket.setCreatorId(userId);
        ticket.setCollegeId(user.getCollegeId());
        ticket.setIsDeleted(0);
        ticket.setCreateTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());

        ticketMapper.insert(ticket);
        return ticketMapper.selectById(ticket.getId());
    }

    /**
     * 分页查询工单
     */
    public PageResult<Ticket> getTicketPage(TicketQueryDTO queryDTO) {
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10;
        int offset = (pageNum - 1) * pageSize;

        List<Ticket> list = ticketMapper.selectPage(
                offset, pageSize,
                queryDTO.getTypeId(),
                queryDTO.getStatusId(),
                queryDTO.getUrgency(),
                queryDTO.getKeyword(),
                queryDTO.getCreatorId(),
                queryDTO.getCollegeId()
        );

        int total = ticketMapper.count(
                queryDTO.getTypeId(),
                queryDTO.getStatusId(),
                queryDTO.getUrgency(),
                queryDTO.getKeyword(),
                queryDTO.getCreatorId(),
                queryDTO.getCollegeId()
        );

        return PageResult.of(pageNum, pageSize, (long) total, list);
    }

    /**
     * 获取工单详情（包含回复树）
     */
    public Ticket getTicketDetail(Long id) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        return ticket;
    }

    /**
     * 获取工单回复树
     */
    public List<TicketReply> getTicketReplies(Long ticketId) {
        List<TicketReply> allReplies = ticketReplyMapper.selectByTicketId(ticketId);
        return buildReplyTree(allReplies, null);
    }

    /**
     * 构建回复树结构
     */
    private List<TicketReply> buildReplyTree(List<TicketReply> allReplies, Long parentId) {
        List<TicketReply> children = allReplies.stream()
                .filter(r -> parentId == null ? r.getParentId() == null : r.getParentId().equals(parentId))
                .toList();
        for (TicketReply child : children) {
            child.setChildren(buildReplyTree(allReplies, child.getId()));
        }
        return children;
    }

    /**
     * 更新工单（仅已提出状态可编辑）
     */
    public Ticket updateTicket(Long id, UpdateTicketDTO dto, Long userId) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        if (!ticket.getCreatorId().equals(userId)) {
            throw new RuntimeException("只能编辑自己创建的工单");
        }
        // 检查状态：只有"已提出"状态允许编辑
        TicketStatus status = ticketStatusMapper.selectById(ticket.getStatusId());
        if (status == null || !"已提出".equals(status.getName())) {
            throw new RuntimeException("当前状态不允许编辑");
        }

        Ticket update = new Ticket();
        update.setId(id);
        if (dto.getTitle() != null) update.setTitle(dto.getTitle());
        if (dto.getContent() != null) update.setContent(dto.getContent());
        if (dto.getTypeId() != null) update.setTypeId(dto.getTypeId());
        if (dto.getUrgency() != null) update.setUrgency(dto.getUrgency());
        if (dto.getImages() != null) update.setImages(dto.getImages());
        update.setUpdateTime(LocalDateTime.now());

        ticketMapper.update(update);
        return ticketMapper.selectById(id);
    }

    /**
     * 删除工单（软删除）
     */
    @Transactional
    public void deleteTicket(Long id, Long userId, String userRole) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        // 只有创建者或学校管理员可以删除
        if (!ticket.getCreatorId().equals(userId) && !"SCHOOL_ADMIN".equals(userRole)) {
            throw new RuntimeException("无权删除此工单");
        }
        ticketMapper.softDeleteById(id);
        // 同时软删除所有回复
        ticketReplyMapper.softDeleteByTicketId(id);
    }

    /**
     * 更新工单状态
     */
    public Ticket updateTicketStatus(Long id, Long statusId) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        TicketStatus newStatus = ticketStatusMapper.selectById(statusId);
        if (newStatus == null) {
            throw new RuntimeException("目标状态不存在");
        }
        ticketMapper.updateStatus(id, statusId, LocalDateTime.now());
        return ticketMapper.selectById(id);
    }

    /**
     * 获取当前用户工单
     */
    public PageResult<Ticket> getMyTickets(Long userId, Integer pageNum, Integer pageSize) {
        TicketQueryDTO queryDTO = new TicketQueryDTO();
        queryDTO.setCreatorId(userId);
        queryDTO.setPageNum(pageNum != null ? pageNum : 1);
        queryDTO.setPageSize(pageSize != null ? pageSize : 10);
        return getTicketPage(queryDTO);
    }

    /**
     * 统计：按类型
     */
    public List<Map<String, Object>> countByType() {
        return ticketMapper.countByType();
    }

    /**
     * 统计：按状态
     */
    public List<Map<String, Object>> countByStatus() {
        return ticketMapper.countByStatus();
    }

    /**
     * 统计：按紧急程度
     */
    public List<Map<String, Object>> countByUrgency() {
        return ticketMapper.countByUrgency();
    }

    /**
     * 统计：满意度
     */
    public Map<String, Object> countSatisfaction() {
        return ticketMapper.countSatisfaction();
    }

    /**
     * 统计：完成率
     */
    public Map<String, Object> countCompletionRate() {
        return ticketMapper.countCompletionRate();
    }

    /**
     * 统计：总数
     */
    public int countTotal() {
        return ticketMapper.countTotal();
    }
}
