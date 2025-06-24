# 图书馆管理系统实体类说明

## 概述

本文档描述了图书馆管理系统中所有实体类的结构和用途。所有实体类都位于 `com.library.entity` 包中，使用了 Lombok 注解简化代码，并配置了 MyBatis Plus 注解进行ORM映射。

## 实体类列表

### 1. User（用户实体类）
- **对应表**: `users`
- **主键**: `user_id`（自增）
- **用途**: 存储系统用户信息，包括学生、教师和管理员
- **主要字段**:
  - `username`: 用户名（唯一）
  - `passwordHash`: 密码哈希值
  - `realName`: 真实姓名
  - `userType`: 用户类型（student/teacher/admin）
  - `studentId`: 学号或工号
  - `classDepartment`: 班级或部门
  - `maxBorrowBooks`: 最大借书数量
  - `maxBorrowDays`: 最大借阅天数
  - `status`: 用户状态（active/frozen/graduated）

### 2. Category（图书分类实体类）
- **对应表**: `categories`
- **主键**: `category_id`（自增）
- **用途**: 管理图书分类体系，支持层级结构
- **主要字段**:
  - `categoryCode`: 分类代码（唯一）
  - `categoryName`: 分类名称
  - `parentCategoryId`: 父分类ID（支持多级分类）
  - `categoryLevel`: 分类层级
  - `sortOrder`: 排序顺序
  - `isActive`: 是否启用

### 3. Book（图书实体类）
- **对应表**: `books`
- **主键**: `book_id`（自增）
- **用途**: 存储图书详细信息和库存管理
- **主要字段**:
  - `isbn`: ISBN编号（唯一）
  - `title`: 图书标题
  - `author`: 作者
  - `publisher`: 出版社
  - `categoryId`: 分类ID
  - `totalCopies`: 总册数
  - `availableCopies`: 可借册数
  - `borrowedCopies`: 已借册数
  - `location`: 存放位置
  - `price`: 价格

### 4. BorrowRecord（借阅记录实体类）
- **对应表**: `borrow_records`
- **主键**: `record_id`（自增）
- **用途**: 记录图书借阅和归还的完整历史
- **主要字段**:
  - `userId`: 借阅用户ID
  - `bookId`: 借阅图书ID
  - `borrowDate`: 借阅日期
  - `dueDate`: 应还日期
  - `actualReturnDate`: 实际归还日期
  - `status`: 借阅状态（borrowed/returned/overdue/lost）
  - `renewalTimes`: 续借次数
  - `overdueDays`: 逾期天数
  - `overdueFee`: 逾期费用

### 5. Reservation（预约记录实体类）
- **对应表**: `reservations`
- **主键**: `reservation_id`（自增）
- **用途**: 管理图书预约申请和排队系统
- **主要字段**:
  - `userId`: 预约用户ID
  - `bookId`: 预约图书ID
  - `reservationDate`: 预约日期
  - `queuePosition`: 队列位置
  - `status`: 预约状态（waiting/notified/fulfilled/cancelled/expired）
  - `notificationDate`: 通知日期
  - `expiryDate`: 过期日期

### 6. SystemConfig（系统配置实体类）
- **对应表**: `system_config`
- **主键**: `config_id`（自增）
- **用途**: 存储系统运行参数和业务规则配置
- **主要字段**:
  - `configKey`: 配置键（唯一）
  - `configValue`: 配置值
  - `configType`: 配置类型（string/integer/decimal/boolean）
  - `configGroup`: 配置分组
  - `description`: 配置描述
  - `isActive`: 是否启用

### 7. OperationLog（操作日志实体类）
- **对应表**: `operation_logs`
- **主键**: `log_id`（自增）
- **用途**: 记录系统操作日志，支持审计和追踪
- **主要字段**:
  - `userId`: 操作用户ID
  - `operationType`: 操作类型（登录/新增/修改/删除等）
  - `operationModule`: 操作模块
  - `operationDescription`: 操作描述
  - `operationResult`: 操作结果（success/failure）
  - `ipAddress`: IP地址
  - `userAgent`: 用户代理

## 技术特性

### Lombok 注解使用
所有实体类都使用了以下 Lombok 注解：
- `@Data`: 自动生成 getter、setter、toString、equals 和 hashCode 方法
- `@Builder`: 提供建造者模式支持
- `@NoArgsConstructor`: 生成无参构造函数
- `@AllArgsConstructor`: 生成全参构造函数

### MyBatis Plus 注解配置
- `@TableName`: 指定对应的数据库表名
- `@TableId`: 标识主键字段及其生成策略
- `@TableField`: 配置字段映射和自动填充策略
- `@FieldFill`: 配置字段的自动填充时机

### 自动填充功能
通过 `MetaObjectHandlerConfig` 配置类实现：
- `createdTime`: 插入时自动填充
- `updatedTime`: 插入和更新时自动填充

### 数据类型使用
- 时间字段统一使用 `LocalDate` 和 `LocalDateTime`
- 金额字段使用 `BigDecimal` 保证精度
- 布尔字段使用 `Boolean` 类型
- 主键统一使用 `Long` 类型

### 枚举常量
每个实体类都定义了内部静态类，包含相关的枚举常量：
- 用户类型、用户状态
- 借阅状态
- 预约状态
- 配置类型和分组
- 操作类型和模块

## 使用示例

### 创建用户
```java
User user = User.builder()
    .username("student001")
    .passwordHash("hashedPassword")
    .realName("张三")
    .userType(User.UserType.STUDENT)
    .studentId("S2024001")
    .classDepartment("计算机学院2024级1班")
    .maxBorrowBooks(5)
    .maxBorrowDays(30)
    .status(User.UserStatus.ACTIVE)
    .build();
```

### 创建图书
```java
Book book = Book.builder()
    .isbn("9787121301234")
    .title("Java编程思想")
    .author("Bruce Eckel")
    .publisher("电子工业出版社")
    .categoryId(25L)
    .totalCopies(3)
    .availableCopies(3)
    .borrowedCopies(0)
    .location("A区1楼101")
    .isActive(true)
    .build();
```

### 创建借阅记录
```java
BorrowRecord record = BorrowRecord.builder()
    .userId(1L)
    .bookId(1L)
    .borrowDate(LocalDate.now())
    .dueDate(LocalDate.now().plusDays(30))
    .status(BorrowRecord.BorrowStatus.BORROWED)
    .renewalTimes(0)
    .maxRenewalTimes(2)
    .build();
```

## 注意事项

1. **数据完整性**: 所有外键关联字段都需要确保引用的数据存在
2. **状态管理**: 业务状态字段应使用预定义的枚举常量
3. **时间处理**: 使用 LocalDate 和 LocalDateTime 避免时区问题
4. **精度要求**: 金额计算使用 BigDecimal 避免精度丢失
5. **自动填充**: 创建时间和更新时间由系统自动管理，无需手动设置 