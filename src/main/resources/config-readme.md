# 图书馆管理系统配置说明

## 配置文件结构

```
src/main/resources/
├── application.properties          # 主配置文件
├── application-dev.properties      # 开发环境配置
├── application-prod.properties     # 生产环境配置
├── logback-spring.xml              # 日志配置文件
└── config-readme.md                # 本说明文档
```

## 环境配置切换

### 开发环境启动
```bash
java -jar library-management-system.jar --spring.profiles.active=dev
```

### 生产环境启动
```bash
java -jar library-management-system.jar --spring.profiles.active=prod
```

### 默认环境
如果不指定环境，系统默认使用开发环境配置。

## 主要配置说明

### 数据库配置
- **数据库文件**: `library_management_system.db`
- **位置**: 项目根目录
- **驱动**: SQLite JDBC
- **连接池**: HikariCP

### MyBatis Plus配置
- **实体类包路径**: `com.school.library.entity`
- **Mapper XML位置**: `classpath*:mapper/**/*.xml`
- **分页插件**: 已启用
- **SQL性能监控**: 开发环境启用

### 服务器配置
- **端口**: 8080
- **最大线程数**: 200（开发）/ 300（生产）
- **会话超时**: 30分钟
- **字符编码**: UTF-8

### 静态资源配置
- **位置**: `src/main/resources/static/`
- **访问路径**: `/**`
- **缓存策略**: 开发环境无缓存，生产环境24小时缓存

### 监控配置
- **Actuator端点**: http://localhost:8081/actuator
- **健康检查**: http://localhost:8081/actuator/health
- **指标监控**: http://localhost:8081/actuator/metrics

### 日志配置
- **日志目录**: `logs/`
- **主日志文件**: `library-system.log`
- **错误日志**: `library-system-error.log`
- **SQL日志**: `library-system-sql.log`
- **日志级别**: 开发环境DEBUG，生产环境WARN

### 业务配置
- **默认借阅天数**: 30天
- **学生最大借书数**: 5本
- **教师最大借书数**: 10本
- **最大续借次数**: 2次
- **逾期费用**: 0.5元/天
- **预约保留天数**: 3天

## 自定义配置

### 修改数据库位置
如需修改数据库文件位置，编辑对应环境的配置文件：
```properties
spring.datasource.url=jdbc:sqlite:/path/to/your/database.db
```

### 修改服务端口
```properties
server.port=9090
```

### 修改日志级别
```properties
logging.level.com.school.library=INFO
```

### 修改业务参数
```properties
library.config.default-borrow-days=45
library.config.max-borrow-books-student=8
```

## 首次运行配置

1. 确保数据库文件 `library_management_system.db` 存在于项目根目录
2. 如果数据库文件不存在，系统启动时会自动创建
3. 使用提供的SQL脚本 `res/03-Library.sql` 初始化数据库
4. 默认管理员账号：admin/admin123

## 部署注意事项

### 生产环境部署
1. 使用生产环境配置文件
2. 确保 `logs` 目录有写入权限
3. 配置适当的数据库备份策略
4. 监控日志文件大小和磁盘空间

### 性能优化建议
1. 根据实际用户量调整连接池大小
2. 根据服务器配置调整线程池参数
3. 定期清理日志文件
4. 监控系统资源使用情况

## 故障排查

### 常见问题
1. **数据库连接失败**: 检查数据库文件路径和权限
2. **静态资源无法访问**: 检查 `src/main/resources/static` 目录
3. **日志文件过大**: 配置文件已设置自动滚动，检查磁盘空间
4. **端口占用**: 修改 `server.port` 配置

### 日志查看
- 应用日志: `tail -f logs/library-system.log`
- 错误日志: `tail -f logs/library-system-error.log`
- SQL日志: `tail -f logs/library-system-sql.log` 