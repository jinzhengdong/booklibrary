package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约记录实体类
 * 对应数据表：reservations
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("reservations")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预约ID（主键，自增）
     */
    @TableId(value = "reservation_id", type = IdType.AUTO)
    private Long reservationId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 图书ID
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 预约日期
     */
    @TableField("reservation_date")
    private LocalDate reservationDate;

    /**
     * 期望借阅日期
     */
    @TableField("expected_date")
    private LocalDate expectedDate;

    /**
     * 通知日期
     */
    @TableField("notification_date")
    private LocalDate notificationDate;

    /**
     * 过期日期
     */
    @TableField("expiry_date")
    private LocalDate expiryDate;

    /**
     * 队列位置
     */
    @TableField("queue_position")
    private Integer queuePosition;

    /**
     * 预约状态（waiting/notified/fulfilled/cancelled/expired）
     */
    @TableField("status")
    private String status;

    /**
     * 备注信息
     */
    @TableField("notes")
    private String notes;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    // 预约状态枚举
    public static class ReservationStatus {
        public static final String WAITING = "waiting";
        public static final String NOTIFIED = "notified";
        public static final String FULFILLED = "fulfilled";
        public static final String CANCELLED = "cancelled";
        public static final String EXPIRED = "expired";
    }
} 