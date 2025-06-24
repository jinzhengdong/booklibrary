package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 * 对应数据表：borrow_records
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("borrow_records")
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID（主键，自增）
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

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
     * 借阅日期
     */
    @TableField("borrow_date")
    private LocalDate borrowDate;

    /**
     * 应还日期
     */
    @TableField("due_date")
    private LocalDate dueDate;

    /**
     * 预期归还日期
     */
    @TableField("return_date")
    private LocalDate returnDate;

    /**
     * 实际归还日期
     */
    @TableField("actual_return_date")
    private LocalDate actualReturnDate;

    /**
     * 逾期天数
     */
    @TableField("overdue_days")
    private Integer overdueDays;

    /**
     * 逾期费用
     */
    @TableField("overdue_fee")
    private BigDecimal overdueFee;

    /**
     * 续借次数
     */
    @TableField("renewal_times")
    private Integer renewalTimes;

    /**
     * 最大续借次数
     */
    @TableField("max_renewal_times")
    private Integer maxRenewalTimes;

    /**
     * 借阅状态（borrowed/returned/overdue/lost）
     */
    @TableField("status")
    private String status;

    /**
     * 操作员ID
     */
    @TableField("operator_id")
    private Long operatorId;

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

    // 借阅状态枚举
    public static class BorrowStatus {
        public static final String BORROWED = "borrowed";
        public static final String RETURNED = "returned";
        public static final String OVERDUE = "overdue";
        public static final String LOST = "lost";
    }
} 