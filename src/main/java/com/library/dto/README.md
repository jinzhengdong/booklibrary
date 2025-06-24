# 图书馆管理系统DTO说明文档

本文档介绍了图书馆管理系统的所有数据传输对象（DTO）类。

## 目录结构

```
dto/
├── request/                # 请求DTO
│   ├── UserLoginRequest.java
│   ├── UserCreateRequest.java
│   ├── UserUpdateRequest.java
│   ├── UserQueryRequest.java
│   ├── UserPasswordChangeRequest.java
│   ├── UserBatchUpdateStatusRequest.java
│   ├── CategoryCreateRequest.java
│   ├── CategoryUpdateRequest.java
│   ├── CategoryQueryRequest.java
│   └── CategoryBatchUpdateRequest.java
├── response/               # 响应DTO
│   ├── ApiResponse.java
│   ├── UserLoginResponse.java
│   ├── UserInfoResponse.java
│   ├── UserSimpleResponse.java
│   ├── UserListResponse.java
│   ├── UserStatisticsResponse.java
│   ├── CategoryInfoResponse.java
│   ├── CategorySimpleResponse.java
│   ├── CategoryListResponse.java
│   ├── CategoryTreeResponse.java
│   └── CategoryStatisticsResponse.java
├── converter/              # 转换工具类
│   ├── UserConverter.java
│   └── CategoryConverter.java
└── README.md
```

## 用户管理模块 DTOs

### Request DTOs（请求数据传输对象）

#### 1. UserLoginRequest
**用途**: 用户登录请求
**字段**:
- `username`: 用户名（必填，3-50字符）
- `password`: 密码（必填，6-20字符）
- `rememberMe`: 是否记住登录状态

**使用场景**: 用户登录接口

#### 2. UserCreateRequest
**用途**: 创建新用户请求
**字段**:
- `username`: 用户名（必填，唯一）
- `password`: 密码（必填，6-20字符）
- `confirmPassword`: 确认密码
- `realName`: 真实姓名（必填）
- `userType`: 用户类型（student/teacher/admin）
- `studentId`: 学号或工号
- `classDepartment`: 班级或部门
- `phone`: 联系电话
- `email`: 电子邮箱
- `maxBorrowBooks`: 最大借书数量
- `maxBorrowDays`: 最大借阅天数

**使用场景**: 管理员创建用户接口

#### 3. UserUpdateRequest
**用途**: 更新用户信息请求
**字段**:
- `userId`: 用户ID（必填）
- 其他可更新字段（除用户名和密码外的所有字段）

**使用场景**: 用户信息更新接口

#### 4. UserQueryRequest
**用途**: 用户查询请求（支持分页和条件查询）
**字段**:
- `pageNum`: 页码（默认1）
- `pageSize`: 每页大小（默认10，最大100）
- `userType`: 用户类型筛选
- `status`: 用户状态筛选
- `keyword`: 关键词搜索（姓名、用户名、学号）
- `classDepartment`: 班级或部门筛选
- `sortField`: 排序字段（默认创建时间）
- `sortOrder`: 排序方向（asc/desc，默认desc）

**使用场景**: 用户列表查询接口

#### 5. UserPasswordChangeRequest
**用途**: 用户密码修改请求
**字段**:
- `userId`: 用户ID（必填）
- `oldPassword`: 旧密码（必填）
- `newPassword`: 新密码（必填，6-20字符）
- `confirmNewPassword`: 确认新密码

**使用场景**: 用户修改密码接口

#### 6. UserBatchUpdateStatusRequest
**用途**: 批量更新用户状态请求
**字段**:
- `userIds`: 用户ID列表（必填，最多100个）
- `status`: 新状态（active/frozen/graduated）
- `reason`: 操作原因

**使用场景**: 批量操作用户状态接口

### Response DTOs（响应数据传输对象）

#### 1. ApiResponse<T>
**用途**: 通用API响应封装类
**字段**:
- `code`: 响应状态码
- `message`: 响应消息
- `data`: 响应数据（泛型）
- `timestamp`: 响应时间戳
- `success`: 是否成功
- `error`: 错误详情

**静态方法**:
- `success(data)`: 创建成功响应
- `error(code, message)`: 创建失败响应
- `badRequest(message)`: 400错误响应
- `unauthorized(message)`: 401错误响应
- `forbidden(message)`: 403错误响应
- `notFound(message)`: 404错误响应
- `serverError(message)`: 500错误响应

#### 2. UserLoginResponse
**用途**: 用户登录响应
**字段**:
- 用户基本信息
- `token`: 访问令牌
- `tokenExpireTime`: 令牌过期时间
- `lastLoginTime`: 最后登录时间

**使用场景**: 登录成功后返回用户信息和token

#### 3. UserInfoResponse
**用途**: 用户详细信息响应
**字段**:
- 用户完整信息
- `userTypeName`: 用户类型显示名称
- `statusName`: 用户状态显示名称
- `currentBorrowCount`: 当前借阅数量
- `totalBorrowCount`: 累计借阅数量
- `overdueCount`: 逾期次数

**使用场景**: 查询用户详情接口

#### 4. UserSimpleResponse
**用途**: 用户简化信息响应（用于列表显示）
**字段**:
- 用户基本信息
- `userTypeName`: 用户类型显示名称
- `statusName`: 用户状态显示名称
- `currentBorrowCount`: 当前借阅数量

**使用场景**: 用户列表查询接口

#### 5. UserListResponse
**用途**: 用户列表响应（包含分页信息）
**字段**:
- `users`: 用户列表（UserSimpleResponse类型）
- `total`: 总记录数
- `pageNum`: 当前页码
- `pageSize`: 每页大小
- `totalPages`: 总页数
- `hasPrevious`: 是否有上一页
- `hasNext`: 是否有下一页
- `isFirst`: 是否为第一页
- `isLast`: 是否为最后一页

**使用场景**: 分页查询用户列表接口

#### 6. UserStatisticsResponse
**用途**: 用户统计响应
**字段**:
- `totalUsers`: 总用户数
- `activeUsers`: 活跃用户数
- `frozenUsers`: 冻结用户数
- `graduatedUsers`: 毕业用户数
- `studentCount`: 学生数量
- `teacherCount`: 教师数量
- `adminCount`: 管理员数量
- `userTypeStatistics`: 按用户类型统计
- `userStatusStatistics`: 按用户状态统计
- `classDepartmentStatistics`: 按班级/部门统计
- `monthlyNewUsers`: 本月新增用户数
- `yearlyNewUsers`: 本年新增用户数
- `currentBorrowingUsers`: 当前借阅用户数
- `overdueUsers`: 逾期用户数

**使用场景**: 用户统计数据接口

## 分类管理模块 DTOs

### Request DTOs（请求数据传输对象）

#### 1. CategoryCreateRequest
**用途**: 创建新分类请求
**字段**:
- `categoryCode`: 分类代码（必填，唯一，1-20字符，大写字母和数字）
- `categoryName`: 分类名称（必填，1-100字符）
- `parentCategoryId`: 父分类ID（可选，为空表示顶级分类）
- `categoryLevel`: 分类层级（1-10，可选）
- `sortOrder`: 排序顺序（0-9999，可选）
- `description`: 分类描述（最多500字符）
- `isActive`: 是否启用（默认启用）

**使用场景**: 管理员创建分类接口

#### 2. CategoryUpdateRequest
**用途**: 更新分类信息请求
**字段**:
- `categoryId`: 分类ID（必填）
- `categoryCode`: 分类代码（必填，唯一）
- `categoryName`: 分类名称（必填）
- `parentCategoryId`: 父分类ID（可选）
- `categoryLevel`: 分类层级（可选）
- `sortOrder`: 排序顺序（可选）
- `description`: 分类描述
- `isActive`: 是否启用

**使用场景**: 分类信息更新接口

#### 3. CategoryQueryRequest
**用途**: 分类查询请求（支持分页和条件查询）
**字段**:
- `pageNum`: 页码（默认1）
- `pageSize`: 每页大小（默认10，最大100）
- `categoryCode`: 分类代码（精确匹配）
- `categoryName`: 分类名称（模糊匹配）
- `parentCategoryId`: 父分类ID
- `categoryLevel`: 分类层级
- `isActive`: 是否启用状态筛选
- `keyword`: 关键词搜索（分类代码或分类名称）
- `isTopLevel`: 是否查询顶级分类
- `includeChildCount`: 是否包含子分类数量统计
- `includeBookCount`: 是否包含图书数量统计
- `sortField`: 排序字段（默认sort_order）
- `sortOrder`: 排序方向（默认asc）

**使用场景**: 分类列表查询接口

#### 4. CategoryBatchUpdateRequest
**用途**: 分类批量操作请求
**字段**:
- `categoryIds`: 分类ID列表（必填，1-100个）
- `operationType`: 操作类型（enable/disable/delete/updateSort）
- `isActive`: 是否启用状态（用于批量启用/禁用）
- `newParentCategoryId`: 新的父分类ID（用于批量移动分类）
- `sortOrderStart`: 排序起始值（用于批量更新排序）
- `reason`: 操作原因

**使用场景**: 批量操作分类接口

### Response DTOs（响应数据传输对象）

#### 1. CategoryInfoResponse
**用途**: 分类详细信息响应
**字段**:
- `categoryId`: 分类ID
- `categoryCode`: 分类代码
- `categoryName`: 分类名称
- `parentCategoryId`: 父分类ID
- `parentCategoryName`: 父分类名称
- `categoryLevel`: 分类层级
- `sortOrder`: 排序顺序
- `description`: 分类描述
- `isActive`: 是否启用
- `isActiveText`: 启用状态显示名称
- `createdTime`: 创建时间
- `updatedTime`: 更新时间
- `childCount`: 子分类数量
- `bookCount`: 图书数量
- `categoryPath`: 分类路径（从根分类到当前分类）
- `children`: 直接子分类列表
- `hasChildren`: 是否有子分类
- `isLeaf`: 是否为叶子节点
- `depth`: 层级深度

**使用场景**: 查询分类详情接口

#### 2. CategorySimpleResponse
**用途**: 分类简化信息响应（用于列表显示）
**字段**:
- `categoryId`: 分类ID
- `categoryCode`: 分类代码
- `categoryName`: 分类名称
- `parentCategoryId`: 父分类ID
- `parentCategoryName`: 父分类名称
- `categoryLevel`: 分类层级
- `sortOrder`: 排序顺序
- `isActive`: 是否启用
- `isActiveText`: 启用状态显示名称
- `childCount`: 子分类数量
- `bookCount`: 图书数量
- `createdTime`: 创建时间
- `hasChildren`: 是否有子分类
- `isLeaf`: 是否为叶子节点

**使用场景**: 分类列表查询接口

#### 3. CategoryListResponse
**用途**: 分类列表响应（包含分页信息）
**字段**:
- `categories`: 分类列表（CategorySimpleResponse类型）
- `total`: 总记录数
- `pageNum`: 当前页码
- `pageSize`: 每页大小
- `totalPages`: 总页数
- `hasPrevious`: 是否有上一页
- `hasNext`: 是否有下一页
- `isFirst`: 是否为第一页
- `isLast`: 是否为最后一页

**使用场景**: 分页查询分类列表接口

#### 4. CategoryTreeResponse
**用途**: 分类树形结构响应
**字段**:
- 基本分类信息字段
- `children`: 子分类列表（递归结构）
- `hasChildren`: 是否有子分类
- `isLeaf`: 是否为叶子节点
- `depth`: 层级深度
- `expanded`: 是否展开（前端显示用）
- `selected`: 是否选中（前端显示用）

**使用场景**: 分类树形结构展示接口

#### 5. CategoryStatisticsResponse
**用途**: 分类统计响应
**字段**:
- `totalCategories`: 总分类数
- `activeCategories`: 启用分类数
- `inactiveCategories`: 禁用分类数
- `topLevelCategories`: 顶级分类数
- `leafCategories`: 叶子分类数
- `categoriesWithBooks`: 有图书的分类数
- `emptyCategoriesCount`: 空分类数
- `maxCategoryLevel`: 最大分类层级
- `averageBooksPerCategory`: 平均每个分类的图书数
- `categoriesByLevel`: 按层级统计分类数量
- `categoriesByStatus`: 按状态统计分类数量
- `topCategoriesByBookCount`: 图书数量最多的前10个分类
- `recentCategories`: 最近创建的分类
- `monthlyNewCategories`: 本月新增分类数
- `yearlyNewCategories`: 本年新增分类数
- `treeDepthStatistics`: 分类树深度统计

**使用场景**: 分类统计数据接口

## Converter（转换工具类）

### UserConverter
**用途**: 用户实体和DTO之间的转换工具类

**主要方法**:
- `toUserInfoResponse(User user)`: User实体转UserInfoResponse
- `toUserSimpleResponse(User user)`: User实体转UserSimpleResponse
- `toUserLoginResponse(User user, String token)`: User实体转UserLoginResponse
- `toUser(UserCreateRequest request)`: UserCreateRequest转User实体
- `updateUser(User user, UserUpdateRequest request)`: 将UpdateRequest应用到User实体
- `toUserSimpleResponseList(List<User> users)`: 批量转换为UserSimpleResponse列表
- `toUserInfoResponseList(List<User> users)`: 批量转换为UserInfoResponse列表

**辅助方法**:
- `getUserTypeName(String userType)`: 获取用户类型显示名称
- `getStatusName(String status)`: 获取用户状态显示名称

### CategoryConverter
**用途**: 分类实体和DTO之间的转换工具类

**主要方法**:
- `toCategoryInfoResponse(Category category)`: Category实体转CategoryInfoResponse
- `toCategorySimpleResponse(Category category)`: Category实体转CategorySimpleResponse
- `toCategoryTreeResponse(Category category)`: Category实体转CategoryTreeResponse
- `toCategory(CategoryCreateRequest request)`: CategoryCreateRequest转Category实体
- `updateCategory(Category category, CategoryUpdateRequest request)`: 将UpdateRequest应用到Category实体
- `toCategorySimpleResponseList(List<Category> categories)`: 批量转换为CategorySimpleResponse列表
- `toCategoryInfoResponseList(List<Category> categories)`: 批量转换为CategoryInfoResponse列表
- `toCategoryTreeResponseList(List<Category> categories)`: 批量转换为CategoryTreeResponse列表

**辅助方法**:
- `getActiveStatusText(Boolean isActive)`: 获取启用状态显示文本
- `buildCategoryTree(List<Category> categories)`: 构建分类树结构
- `setCategoryStatistics(response, childCount, bookCount)`: 设置分类统计信息
- `setParentCategoryName(response, parentCategoryName)`: 设置父分类名称

## 使用说明

### 1. 验证注解
所有Request DTO都使用了Jakarta Validation注解进行数据验证：
- `@NotNull`: 字段不能为null
- `@NotBlank`: 字符串不能为空或空白
- `@Size`: 字符串长度限制
- `@Min/@Max`: 数值范围限制
- `@Pattern`: 正则表达式验证
- `@Email`: 邮箱格式验证

### 2. 转换器使用
转换器类提供了静态方法，可以直接调用：
```java
// 实体转响应DTO
CategoryInfoResponse response = CategoryConverter.toCategoryInfoResponse(category);

// 请求DTO转实体
Category category = CategoryConverter.toCategory(createRequest);

// 批量转换
List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(categories);
```

### 3. 分页响应
所有列表响应都包含完整的分页信息，便于前端实现分页功能。

### 4. 树形结构
分类模块提供了完整的树形结构支持，包括递归的子节点关系和树形构建工具方法。

### 5. 统计信息
统计响应DTO提供了丰富的统计数据，支持多维度的数据分析和报表展示。 