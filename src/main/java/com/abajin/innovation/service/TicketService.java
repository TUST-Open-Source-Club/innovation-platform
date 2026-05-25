package com.abajin.innovation.service;

import com.abajin.innovation.common.PageResult;
import com.abajin.innovation.dto.CreateTicketDTO;
import com.abajin.innovation.dto.TicketQueryDTO;
import com.abajin.innovation.dto.UpdateTicketDTO;
import com.abajin.innovation.entity.Ticket;
import com.abajin.innovation.entity.TicketReply;
import com.abajin.innovation.entity.TicketType;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.TicketStatusEnum;
import com.abajin.innovation.mapper.TicketMapper;
import com.abajin.innovation.mapper.TicketReplyMapper;
import com.abajin.innovation.mapper.TicketTypeMapper;
import com.abajin.innovation.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 工单服务类
 */
@Slf4j
@Service
public class TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketReplyMapper ticketReplyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TicketTypeMapper ticketTypeMapper;

    @Autowired
    private EmailService emailService;

    /**
     * 创建工单
     */
    public Ticket createTicket(CreateTicketDTO dto, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setContent(dto.getContent());
        ticket.setTypeId(dto.getTypeId());
        ticket.setStatusId(TicketStatusEnum.SUBMITTED);
        ticket.setUrgency(dto.getUrgency());
        ticket.setImages(dto.getImages());
        ticket.setCreatorId(userId);
        ticket.setCollegeId(user.getCollegeId());
        ticket.setIsDeleted(0);
        ticket.setCreateTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());

        ticketMapper.insert(ticket);

        // 通知全体管理团队成员有新工单
        if (emailService != null) {
            TicketType ticketType = ticketTypeMapper.selectById(ticket.getTypeId());
            String typeName = ticketType != null ? ticketType.getName() : "未知类型";
            String urgencyText = switch (ticket.getUrgency()) {
                case "URGENT" -> "紧急";
                case "HIGH" -> "高";
                case "MEDIUM" -> "中";
                case "LOW" -> "低";
                default -> ticket.getUrgency();
            };
            String subject = "【创新平台】有新工单提交：" + ticket.getTitle();
            StringBuilder mailContent = new StringBuilder();
            mailContent.append("您好，\n\n有新工单已提交。\n\n");
            mailContent.append("标题：").append(ticket.getTitle()).append("\n");
            mailContent.append("类型：").append(typeName).append("\n");
            mailContent.append("紧急程度：").append(urgencyText).append("\n");
            mailContent.append("内容：\n").append(ticket.getContent()).append("\n\n");
            mailContent.append("请登录创新平台查看详情。\n");
            emailService.notifyAllAdmins(subject, mailContent.toString());
        }

        return ticketMapper.selectById(ticket.getId());
    }

    /**
     * 分页查询工单
     */
    public PageResult<Ticket> getTicketPage(TicketQueryDTO queryDTO) {
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10;
        int offset = (pageNum - 1) * pageSize;

        TicketStatusEnum statusEnum = null;
        if (queryDTO.getStatusId() != null) {
            statusEnum = TicketStatusEnum.fromCode(queryDTO.getStatusId());
        }

        List<Ticket> list = ticketMapper.selectPage(
                offset, pageSize,
                queryDTO.getTypeId(),
                statusEnum,
                queryDTO.getUrgency(),
                queryDTO.getKeyword(),
                queryDTO.getCreatorId(),
                queryDTO.getCollegeId()
        );

        int total = ticketMapper.count(
                queryDTO.getTypeId(),
                statusEnum,
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
                .filter(r -> Objects.equals(r.getParentId(), parentId))
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
        if (ticket.getStatusId() != TicketStatusEnum.SUBMITTED) {
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
    public Ticket updateTicketStatus(Long id, Integer statusCode) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        TicketStatusEnum newStatus = TicketStatusEnum.fromCode(statusCode);
        TicketStatusEnum oldStatus = ticket.getStatusId();
        ticketMapper.updateStatus(id, newStatus, LocalDateTime.now());

        // 通知工单创建人状态变更
        if (emailService != null && newStatus != oldStatus) {
            User creator = userMapper.selectById(ticket.getCreatorId());
            if (creator != null && creator.getEmail() != null && !creator.getEmail().isBlank()) {
                String subject = "【创新平台】您的工单状态已更新：" + ticket.getTitle();
                String content = "您好，\n\n您的工单《" + ticket.getTitle() + "》状态已由「" + oldStatus.getDescription() + "」更新为「" + newStatus.getDescription() + "」。\n\n请登录创新平台查看详情。\n";
                emailService.sendMail(creator.getEmail(), subject, content);
            }
        }

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
        List<Map<String, Object>> result = ticketMapper.countByStatus();
        for (Map<String, Object> item : result) {
            Integer code = (Integer) item.remove("status_code");
            if (code != null) {
                item.put("name", TicketStatusEnum.fromCode(code).getDescription());
            }
        }
        return result;
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
        Map<String, Object> result = ticketMapper.countSatisfaction();
        Map<String, Object> formatted = new HashMap<>();
        formatted.put("satisfied", ((Number) result.getOrDefault("satisfied_count", 0)).longValue());
        formatted.put("dissatisfied", ((Number) result.getOrDefault("dissatisfied_count", 0)).longValue());
        formatted.put("unrated", ((Number) result.getOrDefault("unevaluated_count", 0)).longValue());
        return formatted;
    }

    /**
     * 统计：完成率
     */
    public Map<String, Object> countCompletionRate() {
        Map<String, Object> result = ticketMapper.countCompletionRate();
        long completed = ((Number) result.getOrDefault("completed_count", 0)).longValue();
        long inProgress = ((Number) result.getOrDefault("incomplete_count", 0)).longValue();
        long closed = ((Number) result.getOrDefault("closed_count", 0)).longValue();
        long total = completed + inProgress + closed;

        Map<String, Object> formatted = new HashMap<>();
        formatted.put("completed", completed);
        formatted.put("inProgress", inProgress);
        formatted.put("closed", closed);
        formatted.put("rate", total > 0 ? Math.round((double) completed / total * 10000.0) / 100.0 : 0.0);
        return formatted;
    }

    /**
     * 统计：总数
     */
    public int countTotal() {
        return ticketMapper.countTotal();
    }
}
