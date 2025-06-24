# 图书馆管理系统 - Mapper接口层

## 概述

本项目采用MyBatis Plus作为ORM框架，所有Mapper接口都继承自`BaseMapper<T>`，提供基础的CRUD操作。同时，每个Mapper接口都添加了针对业务需求的自定义查询方法，使用注解方式实现，无需XML配置文件。

## Mapper接口列表

### 1. UserMapper - 用户信息映射接口

**主要功能：**
- 用户基础信息查询
- 登录验证
- 用户状态管理
- 分页查询和条件搜索
- 用户活动统计

**核心方法：**
- `findByUsername()` - 根据用户名查询
- `findByStudentId()` - 根据学号查询
- `validateLogin()` - 验证用户登录
- `findUsersWithConditions()` - 条件分页查询
- `batchUpdateStatus()` - 批量更新用户状态

### 2. CategoryMapper - 图书分类映射接口

**主要功能：**
- 分层分类管理
- 分类树结构查询
- 分类统计和排序
- 分类路径查询

**核心方法：**
- `findCategoryTree()` - 查询完整分类树（递归）
- `findByParentCategoryId()` - 查询子分类
- `findCategoryPath()` - 获取分类完整路径
- `findCategoriesWithBooks()` - 查询有图书的分类
- `updateSortOrder()` - 更新分类排序

### 3. BookMapper - 图书信息映射接口

**主要功能：**
- 图书信息管理
- 库存管理
- 图书搜索和分页
- 热门图书统计
- 库存预警

**核心方法：**
- `findBooksWithConditions()` - 条件分页查询
- `updateBookStock()` - 更新图书库存
- `increaseAvailableCopies()` - 增加可借册数（归还）
- `decreaseAvailableCopies()` - 减少可借册数（借阅）
- `findPopularBooks()` - 查询热门图书
- `findLowStockBooks()` - 查询库存不足图书

### 4. BorrowRecordMapper - 借阅记录映射接口

**主要功能：**
- 借阅记录管理
- 逾期管理
- 借阅统计分析
- 续借处理
- 借阅历史查询

**核心方法：**
- `findCurrentBorrowsByUserId()` - 查询用户当前借阅
- `findOverdueRecords()` - 查询逾期记录
- `findDueSoonRecords()` - 查询即将到期记录
- `returnBook()` - 归还图书
- `renewBook()` - 续借图书
- `findBorrowStatistics()` - 借阅统计报表

### 5. ReservationMapper - 预约记录映射接口

**主要功能：**
- 图书预约管理
- 预约队列管理
- 预约通知处理
- 预约统计分析

**核心方法：**
- `findWaitingQueueByBookId()` - 查询等待队列
- `findFirstInQueue()` - 获取队列第一个预约
- `updateToNotified()` - 更新为已通知状态
- `getQueueLengthByBookId()` - 获取队列长度
- `checkUserBookReservation()` - 检查用户是否已预约

### 6. SystemConfigMapper - 系统配置映射接口

**主要功能：**
- 系统配置管理
- 配置值类型转换
- 配置分组管理
- 批量配置更新

**核心方法：**
- `findByConfigKey()` - 根据配置键查询
- `getStringValue()` - 获取字符串配置值
- `getIntegerValue()` - 获取整数配置值
- `getBooleanValue()` - 获取布尔配置值
- `findByConfigGroup()` - 根据分组查询配置
- `batchUpdateConfigValues()` - 批量更新配置

### 7. OperationLogMapper - 操作日志映射接口

**主要功能：**
- 操作日志记录
- 日志查询和分析
- 用户活动统计
- 安全审计
- 日志清理管理

**核心方法：**
- `findLogsWithConditions()` - 条件分页查询日志
- `findFailureLogs()` - 查询失败操作日志
- `findUserActivityStatistics()` - 用户活跃度统计
- `findDailyStatistics()` - 按天统计操作
- `cleanupOldLogs()` - 清理过期日志

## 技术特点

### 1. 注解驱动
- 使用`@Select`、`@Update`、`@Delete`等注解
- 支持动态SQL（`<script>`标签）
- 无需XML配置文件

### 2. 分页支持
- 集成MyBatis Plus分页插件
- 使用`IPage<T>`和`Page<T>`实现分页
- 支持复杂条件的分页查询

### 3. 条件查询
- 支持多条件动态组合查询
- 使用MyBatis动态SQL语法
- 关键词模糊搜索支持

### 4. 统计分析
- 丰富的统计查询方法
- 支持按时间、类型等维度统计
- 数据报表生成支持

### 5. 批量操作
- 批量更新和删除操作
- 提高数据处理效率
- 事务安全保障

## 使用示例

### 基础查询
```java
@Autowired
private UserMapper userMapper;

// 根据用户名查询
User user = userMapper.findByUsername("admin");

// 分页查询
Page<User> page = new Page<>(1, 10);
IPage<User> result = userMapper.findUsersWithConditions(page, "student", "active", "张");
```

### 复杂统计
```java
@Autowired
private BorrowRecordMapper borrowRecordMapper;

// 查询热门图书
List<BorrowRecord> popularBooks = borrowRecordMapper.findPopularBooksStatistics(10, 30);

// 借阅统计报表
List<BorrowRecord> statistics = borrowRecordMapper.findBorrowStatistics(startDate, endDate);
```

### 批量操作
```java
@Autowired
private BookMapper bookMapper;

// 批量更新图书状态
List<Long> bookIds = Arrays.asList(1L, 2L, 3L);
bookMapper.batchUpdateActiveStatus(bookIds, false);
```

## 注意事项

1. **事务管理**：涉及数据修改的操作需要在Service层添加`@Transactional`注解
2. **SQL优化**：复杂查询已添加必要的索引字段，注意查询性能
3. **数据安全**：敏感操作已添加必要的条件判断，防止误操作
4. **分页查询**：大数据量查询建议使用分页，避免内存溢出
5. **日志清理**：定期清理操作日志，避免数据库存储空间不足

## 扩展说明

如需添加新的查询方法：
1. 在对应的Mapper接口中添加方法声明
2. 使用合适的MyBatis注解（`@Select`、`@Update`等）
3. 编写SQL语句，注意SQLite语法兼容性
4. 添加必要的参数校验和异常处理

所有Mapper接口都遵循统一的编码规范和命名约定，便于维护和扩展。 