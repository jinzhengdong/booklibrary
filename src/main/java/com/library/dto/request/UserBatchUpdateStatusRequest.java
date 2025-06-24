package com.library.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 用户批量更新状态请求DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
public class UserBatchUpdateStatusRequest {

    /**
     * 用户ID列表
     */
    @NotEmpty(message = "用户ID列表不能为空")
    @Size(max = 100, message = "批量操作用户数量不能超过100个")
    private List<Long> userIds;

    /**
     * 新状态（active/frozen/graduated）
     */
    @NotBlank(message = "新状态不能为空")
    @Pattern(regexp = "^(active|frozen|graduated)$", message = "用户状态必须为 active、frozen 或 graduated")
    private String status;

    /**
     * 操作原因
     */
    private String reason;
} 