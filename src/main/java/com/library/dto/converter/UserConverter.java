package com.library.dto.converter;

import com.library.dto.request.UserCreateRequest;
import com.library.dto.request.UserUpdateRequest;
import com.library.dto.response.UserInfoResponse;
import com.library.dto.response.UserLoginResponse;
import com.library.dto.response.UserSimpleResponse;
import com.library.entity.User;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户DTO转换工具类
 * 
 * @author Library Management System
 * @version 1.0.0
 */
public class UserConverter {

    /**
     * 将User实体转换为UserInfoResponse
     */
    public static UserInfoResponse toUserInfoResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserInfoResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .userTypeName(getUserTypeName(user.getUserType()))
                .studentId(user.getStudentId())
                .classDepartment(user.getClassDepartment())
                .phone(user.getPhone())
                .email(user.getEmail())
                .registrationDate(user.getRegistrationDate())
                .status(user.getStatus())
                .statusName(getUserStatusName(user.getStatus()))
                .maxBorrowBooks(user.getMaxBorrowBooks())
                .maxBorrowDays(user.getMaxBorrowDays())
                .createdTime(user.getCreatedTime())
                .updatedTime(user.getUpdatedTime())
                .build();
    }

    /**
     * 将User实体转换为UserSimpleResponse
     */
    public static UserSimpleResponse toUserSimpleResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserSimpleResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .userTypeName(getUserTypeName(user.getUserType()))
                .studentId(user.getStudentId())
                .classDepartment(user.getClassDepartment())
                .phone(user.getPhone())
                .email(user.getEmail())
                .registrationDate(user.getRegistrationDate())
                .status(user.getStatus())
                .statusName(getUserStatusName(user.getStatus()))
                .maxBorrowBooks(user.getMaxBorrowBooks())
                .build();
    }

    /**
     * 将User实体转换为UserLoginResponse
     */
    public static UserLoginResponse toUserLoginResponse(User user, String token) {
        if (user == null) {
            return null;
        }

        return UserLoginResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .studentId(user.getStudentId())
                .classDepartment(user.getClassDepartment())
                .phone(user.getPhone())
                .email(user.getEmail())
                .registrationDate(user.getRegistrationDate())
                .status(user.getStatus())
                .maxBorrowBooks(user.getMaxBorrowBooks())
                .maxBorrowDays(user.getMaxBorrowDays())
                .lastLoginTime(LocalDateTime.now())
                .token(token)
                .tokenExpireTime(LocalDateTime.now().plusHours(24)) // 24小时过期
                .build();
    }

    /**
     * 将UserCreateRequest转换为User实体
     */
    public static User toUser(UserCreateRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .username(request.getUsername())
                .passwordHash(DigestUtils.md5DigestAsHex(request.getPassword().getBytes()))
                .realName(request.getRealName())
                .userType(request.getUserType())
                .studentId(request.getStudentId())
                .classDepartment(request.getClassDepartment())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(User.UserStatus.ACTIVE)
                .maxBorrowBooks(request.getMaxBorrowBooks() != null ? request.getMaxBorrowBooks() : getDefaultMaxBorrowBooks(request.getUserType()))
                .maxBorrowDays(request.getMaxBorrowDays() != null ? request.getMaxBorrowDays() : getDefaultMaxBorrowDays(request.getUserType()))
                .build();
    }

    /**
     * 将UserUpdateRequest应用到User实体
     */
    public static void updateUser(User user, UserUpdateRequest request) {
        if (user == null || request == null) {
            return;
        }

        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getUserType() != null) {
            user.setUserType(request.getUserType());
        }
        if (request.getStudentId() != null) {
            user.setStudentId(request.getStudentId());
        }
        if (request.getClassDepartment() != null) {
            user.setClassDepartment(request.getClassDepartment());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getMaxBorrowBooks() != null) {
            user.setMaxBorrowBooks(request.getMaxBorrowBooks());
        }
        if (request.getMaxBorrowDays() != null) {
            user.setMaxBorrowDays(request.getMaxBorrowDays());
        }
    }

    /**
     * 批量转换User实体列表为UserSimpleResponse列表
     */
    public static List<UserSimpleResponse> toUserSimpleResponseList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(UserConverter::toUserSimpleResponse)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换User实体列表为UserInfoResponse列表
     */
    public static List<UserInfoResponse> toUserInfoResponseList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(UserConverter::toUserInfoResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户类型显示名称
     */
    private static String getUserTypeName(String userType) {
        if (userType == null) {
            return null;
        }
        switch (userType) {
            case User.UserType.STUDENT:
                return "学生";
            case User.UserType.TEACHER:
                return "教师";
            case User.UserType.ADMIN:
                return "管理员";
            default:
                return userType;
        }
    }

    /**
     * 获取用户状态显示名称
     */
    private static String getUserStatusName(String status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case User.UserStatus.ACTIVE:
                return "正常";
            case User.UserStatus.FROZEN:
                return "冻结";
            case User.UserStatus.GRADUATED:
                return "毕业";
            default:
                return status;
        }
    }

    /**
     * 根据用户类型获取默认最大借书数量
     */
    private static Integer getDefaultMaxBorrowBooks(String userType) {
        if (userType == null) {
            return 5;
        }
        switch (userType) {
            case User.UserType.STUDENT:
                return 5;
            case User.UserType.TEACHER:
                return 10;
            case User.UserType.ADMIN:
                return 30;
            default:
                return 5;
        }
    }

    /**
     * 根据用户类型获取默认最大借阅天数
     */
    private static Integer getDefaultMaxBorrowDays(String userType) {
        if (userType == null) {
            return 30;
        }
        switch (userType) {
            case User.UserType.STUDENT:
                return 30;
            case User.UserType.TEACHER:
                return 60;
            case User.UserType.ADMIN:
                return 90;
            default:
                return 30;
        }
    }
} 