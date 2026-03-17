package com.abajin.innovation.controller;

import com.abajin.innovation.annotation.RequiresRole;
import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.PageResult;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.News;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 新闻管理控制器
 */
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    /**
     * 创建新闻稿（草稿状态）
     * POST /api/news
     */
    @PostMapping
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<News> createNews(
            @Valid @RequestBody News news,
            @RequestAttribute("userId") Long userId) {
        try {
            News created = newsService.createNews(news, userId);
            return Result.success("新闻稿创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交新闻稿（待审核状态）
     * POST /api/news/{id}/submit
     */
    @PostMapping("/{id}/submit")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<News> submitNews(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            News news = newsService.submitNews(id, userId);
            return Result.success("新闻稿已提交", news);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核新闻（学校管理员）
     * POST /api/news/{id}/review
     */
    @PostMapping("/{id}/review")
    @RequiresRole(value = {Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = false)
    public Result<News> reviewNews(
            @PathVariable Long id,
            @RequestBody Map<String, String> reviewData,
            @RequestAttribute("userId") Long userId) {
        try {
            String approvalStatus = reviewData.get("approvalStatus");
            String reviewComment = reviewData.get("reviewComment");

            // 验证审批状态
            ApprovalStatus status;
            try {
                status = ApprovalStatus.valueOf(approvalStatus);
            } catch (IllegalArgumentException e) {
                return Result.error(400, "审批状态无效，必须为：APPROVED 或 REJECTED");
            }

            if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
                return Result.error(400, "审核操作只能设置为 APPROVED 或 REJECTED");
            }

            News reviewed = newsService.reviewNews(id, approvalStatus, reviewComment, userId);
            return Result.success("审核完成", reviewed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询新闻列表
     * GET /api/news?pageNum=1&pageSize=10&title=xxx&status=PUBLISHED
     */
    @GetMapping
    public Result<PageResult<News>> getNews(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestAttribute(value = "role", required = false) String role) {
        try {
            // 非学校/学院管理员：只能看到“已发布”的新闻
            if (!isSchoolOrCollegeAdmin(role)) {
                status = "PUBLISHED";
            }
            // 新闻审核仅学校管理员：非学校管理员查询待审核列表时直接返回空
            if ("PENDING".equals(approvalStatus) && !Constants.ROLE_SCHOOL_ADMIN.equals(role)) {
                PageResult<News> empty = PageResult.of(pageNum, pageSize, 0L, Collections.emptyList());
                return Result.success(empty);
            }
            List<News> newsList = newsService.getNews(pageNum, pageSize, title, status, 
                                                       approvalStatus, categoryId, authorId);
            Long total = (long) newsService.countNews(title, status, approvalStatus, categoryId, authorId);
            PageResult<News> pageResult = PageResult.of(pageNum, pageSize, total, newsList);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private boolean isSchoolOrCollegeAdmin(String role) {
        return Constants.ROLE_SCHOOL_ADMIN.equals(role) || Constants.ROLE_COLLEGE_ADMIN.equals(role);
    }

    /**
     * 查询新闻详情
     * GET /api/news/{id}
     */
    @GetMapping("/{id}")
    public Result<News> getNewsById(@PathVariable Long id) {
        try {
            News news = newsService.getNewsById(id);
            if (news == null) {
                return Result.error(404, "新闻不存在");
            }
            return Result.success(news);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新新闻（仅作者可更新草稿状态）
     * PUT /api/news/{id}
     */
    @PutMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<News> updateNews(
            @PathVariable Long id,
            @Valid @RequestBody News news,
            @RequestAttribute("userId") Long userId) {
        try {
            News updated = newsService.updateNews(id, news, userId);
            return Result.success("新闻更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除新闻（仅作者可删除草稿状态）
     * DELETE /api/news/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Void> deleteNews(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            newsService.deleteNews(id, userId);
            return Result.success("新闻删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 软删除新闻（管理员）
     * DELETE /api/news/{id}/soft
     */
    @DeleteMapping("/{id}/soft")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Void> softDeleteNews(@PathVariable Long id) {
        try {
            newsService.softDeleteNews(id);
            return Result.success("新闻删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除新闻（管理员）
     * DELETE /api/news/batch
     */
    @DeleteMapping("/batch")
    @RequiresRole(value = {Constants.ROLE_COLLEGE_ADMIN, Constants.ROLE_SCHOOL_ADMIN}, allowAdmin = true)
    public Result<Integer> batchDeleteNews(@RequestBody Map<String, List<Long>> data) {
        try {
            List<Long> ids = data.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的新闻");
            }
            int count = newsService.softDeleteNewsBatch(ids);
            return Result.success("成功删除 " + count + " 条新闻", count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
