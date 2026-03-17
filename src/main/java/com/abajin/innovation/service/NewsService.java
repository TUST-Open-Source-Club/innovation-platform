package com.abajin.innovation.service;

import com.abajin.innovation.entity.News;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.mapper.UserMapper;
import com.abajin.innovation.enums.NewsStatus;
import com.abajin.innovation.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 新闻管理服务类
 */
@Service
public class NewsService {
    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建新闻稿（草稿状态）
     */
    @Transactional
    public News createNews(News news, Long authorId) {
        User author = userMapper.selectById(authorId);
        if (author == null) {
            throw new RuntimeException("作者不存在");
        }

        news.setAuthorId(authorId);
        news.setAuthorName(author.getRealName());
        news.setStatus(NewsStatus.DRAFT.name());
        news.setApprovalStatus(ApprovalStatus.PENDING.name());
        news.setViewCount(0);
        news.setLikeCount(0);
        news.setIsTop(0);
        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());

        newsMapper.insert(news);
        return news;
    }

    /**
     * 提交新闻稿（待审核状态）
     */
    @Transactional
    public News submitNews(Long newsId, Long authorId) {
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在");
        }
        if (!news.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权提交此新闻");
        }
        if (!NewsStatus.DRAFT.name().equals(news.getStatus())) {
            throw new RuntimeException("只能提交草稿状态的新闻");
        }

        news.setStatus(NewsStatus.PENDING.name());
        news.setApprovalStatus(ApprovalStatus.PENDING.name());
        news.setUpdateTime(LocalDateTime.now());
        newsMapper.update(news);
        return news;
    }

    /**
     * 审核新闻（学校管理员）
     */
    @Transactional
    public News reviewNews(Long newsId, String approvalStatus, String reviewComment, Long reviewerId) {
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在");
        }
        if (!NewsStatus.PENDING.name().equals(news.getStatus())) {
            throw new RuntimeException("只能审核待审核状态的新闻");
        }

        // 验证审批状态
        ApprovalStatus status;
        try {
            status = ApprovalStatus.valueOf(approvalStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("审批状态无效");
        }

        if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
            throw new RuntimeException("审核操作只能设置为 APPROVED 或 REJECTED");
        }

        news.setApprovalStatus(approvalStatus);
        news.setReviewComment(reviewComment);
        news.setReviewerId(reviewerId);
        news.setReviewTime(LocalDateTime.now());

        if (ApprovalStatus.APPROVED.name().equals(approvalStatus)) {
            // 审核通过，发布新闻
            news.setStatus(NewsStatus.PUBLISHED.name());
            news.setPublishTime(LocalDateTime.now());
        } else {
            // 审核驳回
            news.setStatus(NewsStatus.REJECTED.name());
        }

        news.setUpdateTime(LocalDateTime.now());
        newsMapper.update(news);
        return news;
    }

    /**
     * 分页查询新闻
     */
    public List<News> getNews(int pageNum, int pageSize, String title, String status, 
                              String approvalStatus, Long categoryId, Long authorId) {
        int offset = (pageNum - 1) * pageSize;
        return newsMapper.selectPage(offset, pageSize, title, status, approvalStatus, categoryId, authorId);
    }

    /**
     * 统计新闻总数
     */
    public int countNews(String title, String status, String approvalStatus, 
                         Long categoryId, Long authorId) {
        return newsMapper.count(title, status, approvalStatus, categoryId, authorId);
    }

    /**
     * 查询新闻详情
     */
    public News getNewsById(Long id) {
        News news = newsMapper.selectById(id);
        if (news != null && NewsStatus.PUBLISHED.name().equals(news.getStatus())) {
            // 增加浏览次数
            newsMapper.incrementViewCount(id);
            // 重新查询以获取更新后的浏览次数
            news = newsMapper.selectById(id);
        }
        return news;
    }

    /**
     * 更新新闻（仅作者可更新草稿状态）
     */
    @Transactional
    public News updateNews(Long newsId, News news, Long authorId) {
        News existing = newsMapper.selectById(newsId);
        if (existing == null) {
            throw new RuntimeException("新闻不存在");
        }
        if (!existing.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权更新此新闻");
        }
        if (!NewsStatus.DRAFT.name().equals(existing.getStatus())) {
            throw new RuntimeException("只能更新草稿状态的新闻");
        }

        // 更新字段
        if (news.getTitle() != null) existing.setTitle(news.getTitle());
        if (news.getCategoryId() != null) existing.setCategoryId(news.getCategoryId());
        if (news.getCoverImage() != null) existing.setCoverImage(news.getCoverImage());
        if (news.getSummary() != null) existing.setSummary(news.getSummary());
        if (news.getContent() != null) existing.setContent(news.getContent());
        if (news.getSource() != null) existing.setSource(news.getSource());
        if (news.getAttachments() != null) existing.setAttachments(news.getAttachments());
        if (news.getRelatedActivityId() != null) existing.setRelatedActivityId(news.getRelatedActivityId());
        if (news.getIsTop() != null) existing.setIsTop(news.getIsTop());

        existing.setUpdateTime(LocalDateTime.now());
        newsMapper.update(existing);
        return existing;
    }

    /**
     * 删除新闻（仅作者可删除草稿状态）
     */
    @Transactional
    public void deleteNews(Long newsId, Long authorId) {
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在");
        }
        if (!news.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权删除此新闻");
        }
        if (!NewsStatus.DRAFT.name().equals(news.getStatus())) {
            throw new RuntimeException("只能删除草稿状态的新闻");
        }

        newsMapper.deleteById(newsId);
    }

    /**
     * 软删除新闻（管理员）
     */
    @Transactional
    public void softDeleteNews(Long newsId) {
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在");
        }
        newsMapper.softDeleteById(newsId);
    }

    /**
     * 批量软删除新闻（管理员）
     */
    @Transactional
    public int softDeleteNewsBatch(List<Long> newsIds) {
        if (newsIds == null || newsIds.isEmpty()) {
            throw new RuntimeException("请选择要删除的新闻");
        }
        return newsMapper.softDeleteBatch(newsIds);
    }
}
