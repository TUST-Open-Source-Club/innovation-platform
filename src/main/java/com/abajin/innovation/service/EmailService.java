package com.abajin.innovation.service;

import com.abajin.innovation.common.Constants;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 邮件通知服务
 */
@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserMapper userMapper;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    /**
     * 发送纯文本邮件
     */
    @Async
    public void sendMail(String to, String subject, String content) {
        if (to == null || to.isBlank()) {
            log.warn("邮件收件人为空，跳过发送: subject={}", subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("邮件发送失败: to={}, subject={}, error={}", to, subject, e.getMessage());
        }
    }

    /**
     * 通知学院管理员有新的待审批项
     */
    @Async
    public void notifyCollegeAdmins(String itemType, String itemTitle) {
        List<User> admins = userMapper.selectByRole(Constants.ROLE_COLLEGE_ADMIN);
        if (admins == null || admins.isEmpty()) {
            log.warn("未找到学院管理员，无法发送通知");
            return;
        }
        String subject = "【创新平台】有新的" + itemType + "待审批";
        String content = "您好，\n\n有新的" + itemType + "《" + itemTitle + "》已提交，等待您审批。\n\n请登录创新平台查看详情。\n";
        for (User admin : admins) {
            if (admin.getEmail() != null && !admin.getEmail().isBlank()) {
                sendMail(admin.getEmail(), subject, content);
            }
        }
    }

    /**
     * 通知学校管理员有新的待审批项
     */
    @Async
    public void notifySchoolAdmins(String itemType, String itemTitle) {
        List<User> admins = userMapper.selectByRole(Constants.ROLE_SCHOOL_ADMIN);
        if (admins == null || admins.isEmpty()) {
            log.warn("未找到学校管理员，无法发送通知");
            return;
        }
        String subject = "【创新平台】有新的" + itemType + "待审批";
        String content = "您好，\n\n有新的" + itemType + "《" + itemTitle + "》已提交，等待您审批。\n\n请登录创新平台查看详情。\n";
        for (User admin : admins) {
            if (admin.getEmail() != null && !admin.getEmail().isBlank()) {
                sendMail(admin.getEmail(), subject, content);
            }
        }
    }

    /**
     * 通知申请人审批结果
     */
    @Async
    public void notifyApplicant(Long applicantId, String itemType, String itemTitle, boolean approved, String reviewComment) {
        User applicant = userMapper.selectById(applicantId);
        if (applicant == null || applicant.getEmail() == null || applicant.getEmail().isBlank()) {
            log.warn("申请人未设置邮箱，跳过通知: applicantId={}", applicantId);
            return;
        }
        String result = approved ? "已通过" : "未通过";
        String subject = "【创新平台】您的" + itemType + "审批结果：" + result;
        StringBuilder content = new StringBuilder();
        content.append("您好，\n\n您提交的").append(itemType).append("《").append(itemTitle).append("》");
        content.append("审批结果：").append(result).append("\n");
        if (reviewComment != null && !reviewComment.isBlank()) {
            content.append("审批意见：").append(reviewComment).append("\n");
        }
        content.append("\n请登录创新平台查看详情。\n");
        sendMail(applicant.getEmail(), subject, content.toString());
    }

    /**
     * 通知学院审批通过后转学校审批
     */
    @Async
    public void notifySchoolAdminsAfterCollegeApproval(String itemType, String itemTitle) {
        List<User> admins = userMapper.selectByRole(Constants.ROLE_SCHOOL_ADMIN);
        if (admins == null || admins.isEmpty()) {
            log.warn("未找到学校管理员，无法发送通知");
            return;
        }
        String subject = "【创新平台】学院已通过" + itemType + "，等待终审";
        String content = "您好，\n\n" + itemType + "《" + itemTitle + "》已通过学院审批，现等待学校管理员终审。\n\n请登录创新平台查看详情。\n";
        for (User admin : admins) {
            if (admin.getEmail() != null && !admin.getEmail().isBlank()) {
                sendMail(admin.getEmail(), subject, content);
            }
        }
    }
}
