package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户信息Mapper接口
 * 提供用户数据访问操作
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND status != 'graduated'")
    User findByUsername(@Param("username") String username);

    /**
     * 根据学号查询用户
     * 
     * @param studentId 学号
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE student_id = #{studentId} AND status != 'graduated'")
    User findByStudentId(@Param("studentId") String studentId);

    /**
     * 根据用户类型查询用户列表
     * 
     * @param userType 用户类型
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE user_type = #{userType} AND status = 'active' ORDER BY created_time DESC")
    List<User> findByUserType(@Param("userType") String userType);

    /**
     * 根据状态查询用户列表
     * 
     * @param status 用户状态
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE status = #{status} ORDER BY created_time DESC")
    List<User> findByStatus(@Param("status") String status);

    /**
     * 分页查询用户信息
     * 
     * @param page 分页参数
     * @param userType 用户类型（可选）
     * @param status 用户状态（可选）
     * @param keyword 关键词搜索（姓名、用户名、学号）
     * @return 分页用户列表
     */
    @Select("<script>" +
            "SELECT * FROM users WHERE 1=1 " +
            "<if test='userType != null and userType != \"\"'>" +
            "AND user_type = #{userType} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status} " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR student_id LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY created_time DESC" +
            "</script>")
    IPage<User> findUsersWithConditions(Page<User> page, 
                                       @Param("userType") String userType,
                                       @Param("status") String status, 
                                       @Param("keyword") String keyword);

    /**
     * 根据班级或部门查询用户
     * 
     * @param classDepartment 班级或部门
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE class_department = #{classDepartment} AND status = 'active' ORDER BY student_id")
    List<User> findByClassDepartment(@Param("classDepartment") String classDepartment);

    /**
     * 更新用户最后登录时间
     * 
     * @param userId 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 更新行数
     */
    @Update("UPDATE users SET updated_time = CURRENT_TIMESTAMP WHERE user_id = #{userId}")
    int updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 统计各用户类型的数量
     * 
     * @return 统计结果
     */
    @Select("SELECT user_type, COUNT(*) as count FROM users WHERE status = 'active' GROUP BY user_type")
    List<User> countByUserType();

    /**
     * 查询即将毕业的学生（可用于数据清理）
     * 
     * @return 即将毕业的学生列表
     */
    @Select("SELECT * FROM users WHERE user_type = 'student' AND status = 'active' " +
            "AND registration_date &lt;= DATE('now', '-4 years') ORDER BY registration_date")
    List<User> findGraduatingStudents();

    /**
     * 验证用户登录
     * 
     * @param username 用户名
     * @param passwordHash 密码哈希
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND password_hash = #{passwordHash} AND status = 'active'")
    User validateLogin(@Param("username") String username, @Param("passwordHash") String passwordHash);

    /**
     * 批量更新用户状态
     * 
     * @param userIds 用户ID列表
     * @param status 新状态
     * @return 更新行数
     */
    @Update("<script>" +
            "UPDATE users SET status = #{status}, updated_time = CURRENT_TIMESTAMP " +
            "WHERE user_id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("userIds") List<Long> userIds, @Param("status") String status);
} 