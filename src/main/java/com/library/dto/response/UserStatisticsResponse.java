package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 用户统计响应DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 活跃用户数
     */
    private Long activeUsers;

    /**
     * 冻结用户数
     */
    private Long frozenUsers;

    /**
     * 毕业用户数
     */
    private Long graduatedUsers;

    /**
     * 学生数量
     */
    private Long studentCount;

    /**
     * 教师数量
     */
    private Long teacherCount;

    /**
     * 管理员数量
     */
    private Long adminCount;

    /**
     * 按用户类型统计
     * Key: 用户类型 (student/teacher/admin)
     * Value: 用户数量
     */
    private Map<String, Long> userTypeStatistics;

    /**
     * 按用户状态统计
     * Key: 用户状态 (active/frozen/graduated)
     * Value: 用户数量
     */
    private Map<String, Long> userStatusStatistics;

    /**
     * 按班级/部门统计
     * Key: 班级或部门名称
     * Value: 用户数量
     */
    private Map<String, Long> classDepartmentStatistics;

    /**
     * 本月新增用户数
     */
    private Long monthlyNewUsers;

    /**
     * 本年新增用户数
     */
    private Long yearlyNewUsers;

    /**
     * 当前借阅用户数
     */
    private Long currentBorrowingUsers;

    /**
     * 逾期用户数
     */
    private Long overdueUsers;
} 