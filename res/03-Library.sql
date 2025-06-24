1-- ========================================
-- 图书馆管理系统数据库DDL脚本
-- Database: library_management_system
-- Version: 1.0
-- Date: 2025-06-12
-- ========================================

-- 开启外键约束
PRAGMA foreign_keys = ON;

-- ========================================
-- 1. 创建用户信息表
-- ========================================
CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    real_name VARCHAR(100) NOT NULL,
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('student', 'teacher', 'admin')),
    student_id VARCHAR(20),
    class_department VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    registration_date DATE NOT NULL DEFAULT (DATE('now')),
    status VARCHAR(20) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'frozen', 'graduated')),
    max_borrow_books INTEGER NOT NULL DEFAULT 5,
    max_borrow_days INTEGER NOT NULL DEFAULT 30,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- 2. 创建图书分类表
-- ========================================
CREATE TABLE categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_code VARCHAR(20) NOT NULL UNIQUE,
    category_name VARCHAR(100) NOT NULL,
    parent_category_id INTEGER,
    category_level INTEGER NOT NULL DEFAULT 1,
    sort_order INTEGER DEFAULT 0,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES categories(category_id)
);

-- ========================================
-- 3. 创建图书信息表
-- ========================================
CREATE TABLE books (
    book_id INTEGER PRIMARY KEY AUTOINCREMENT,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(200) NOT NULL,
    publisher VARCHAR(100),
    publication_date DATE,
    category_id INTEGER NOT NULL,
    language VARCHAR(20) DEFAULT 'Chinese',
    pages INTEGER,
    price DECIMAL(10,2),
    description TEXT,
    location VARCHAR(50),
    total_copies INTEGER NOT NULL DEFAULT 1 CHECK (total_copies > 0),
    available_copies INTEGER NOT NULL DEFAULT 1 CHECK (available_copies >= 0),
    borrowed_copies INTEGER NOT NULL DEFAULT 0 CHECK (borrowed_copies >= 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CHECK (available_copies + borrowed_copies <= total_copies)
);

-- ========================================
-- 4. 创建借阅记录表
-- ========================================
CREATE TABLE borrow_records (
    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    book_id INTEGER NOT NULL,
    borrow_date DATE NOT NULL DEFAULT (DATE('now')),
    due_date DATE NOT NULL,
    return_date DATE,
    actual_return_date DATE,
    overdue_days INTEGER DEFAULT 0 CHECK (overdue_days >= 0),
    overdue_fee DECIMAL(10,2) DEFAULT 0.00 CHECK (overdue_fee >= 0),
    renewal_times INTEGER DEFAULT 0 CHECK (renewal_times >= 0),
    max_renewal_times INTEGER DEFAULT 2,
    status VARCHAR(20) NOT NULL DEFAULT 'borrowed' CHECK (status IN ('borrowed', 'returned', 'overdue', 'lost')),
    operator_id INTEGER,
    notes TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (operator_id) REFERENCES users(user_id)
);

-- ========================================
-- 5. 创建预约记录表
-- ========================================
CREATE TABLE reservations (
    reservation_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    book_id INTEGER NOT NULL,
    reservation_date DATE NOT NULL DEFAULT (DATE('now')),
    expected_date DATE,
    notification_date DATE,
    expiry_date DATE,
    queue_position INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'waiting' CHECK (status IN ('waiting', 'notified', 'fulfilled', 'cancelled', 'expired')),
    notes TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- ========================================
-- 6. 创建系统配置表
-- ========================================
CREATE TABLE system_config (
    config_id INTEGER PRIMARY KEY AUTOINCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type VARCHAR(20) NOT NULL DEFAULT 'string',
    config_group VARCHAR(50),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- 7. 创建操作日志表
-- ========================================
CREATE TABLE operation_logs (
    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    operation_type VARCHAR(50) NOT NULL,
    operation_module VARCHAR(50) NOT NULL,
    operation_description TEXT NOT NULL,
    target_id INTEGER,
    target_type VARCHAR(50),
    ip_address VARCHAR(45),
    user_agent TEXT,
    operation_result VARCHAR(20) NOT NULL CHECK (operation_result IN ('success', 'failure')),
    error_message TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ========================================
-- 8. 创建索引
-- ========================================

-- 用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_student_id ON users(student_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_type ON users(user_type);

-- 分类表索引
CREATE INDEX idx_categories_code ON categories(category_code);
CREATE INDEX idx_categories_parent ON categories(parent_category_id);

-- 图书表索引
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_category ON books(category_id);
CREATE INDEX idx_books_status ON books(is_active);

-- 借阅记录表索引
CREATE INDEX idx_borrow_user_book ON borrow_records(user_id, book_id);
CREATE INDEX idx_borrow_status ON borrow_records(status);
CREATE INDEX idx_borrow_due_date ON borrow_records(due_date);
CREATE INDEX idx_borrow_user_status ON borrow_records(user_id, status);

-- 预约记录表索引
CREATE INDEX idx_reservation_user_book ON reservations(user_id, book_id);
CREATE INDEX idx_reservation_status ON reservations(status);
CREATE INDEX idx_reservation_book_status ON reservations(book_id, status);

-- 系统配置表索引
CREATE INDEX idx_config_key ON system_config(config_key);
CREATE INDEX idx_config_group ON system_config(config_group);

-- 操作日志表索引
CREATE INDEX idx_logs_user ON operation_logs(user_id);
CREATE INDEX idx_logs_type ON operation_logs(operation_type);
CREATE INDEX idx_logs_time ON operation_logs(created_time);

-- ========================================
-- 9. 初始化数据
-- ========================================

-- 插入系统管理员用户（密码为admin123的MD5哈希值）
INSERT INTO users (username, password_hash, real_name, user_type, student_id, class_department, phone, email, max_borrow_books, max_borrow_days) VALUES 
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin', 'ADMIN001', '图书馆', '13800000000', 'admin@school.edu.cn', 50, 90),
('librarian', '5d41402abc4b2a76b9719d911017c592', '图书管理员', 'admin', 'LIB001', '图书馆', '13800000001', 'librarian@school.edu.cn', 30, 60),
('teacher001', 'e10adc3949ba59abbe56e057f20f883e', '张教授', 'teacher', 'T001', '计算机学院', '13900000001', 'zhang@school.edu.cn', 10, 60),
('student001', 'e10adc3949ba59abbe56e057f20f883e', '李小明', 'student', 'S2024001', '计算机学院2024级1班', '13900000002', 'liming@school.edu.cn', 5, 30),
('student002', 'e10adc3949ba59abbe56e057f20f883e', '王小红', 'student', 'S2024002', '计算机学院2024级1班', '13900000003', 'wanghong@school.edu.cn', 5, 30);

-- 插入图书分类数据
INSERT INTO categories (category_code, category_name, parent_category_id, category_level, sort_order, description) VALUES 
('A', '马克思主义、列宁主义、毛泽东思想、邓小平理论', NULL, 1, 1, '政治理论类图书'),
('B', '哲学、宗教', NULL, 1, 2, '哲学和宗教类图书'),
('C', '社会科学总论', NULL, 1, 3, '社会科学综合类图书'),
('D', '政治、法律', NULL, 1, 4, '政治法律类图书'),
('E', '军事', NULL, 1, 5, '军事类图书'),
('F', '经济', NULL, 1, 6, '经济管理类图书'),
('G', '文化、科学、教育、体育', NULL, 1, 7, '文化教育类图书'),
('H', '语言、文字', NULL, 1, 8, '语言文学类图书'),
('I', '文学', NULL, 1, 9, '文学作品类图书'),
('J', '艺术', NULL, 1, 10, '艺术类图书'),
('K', '历史、地理', NULL, 1, 11, '历史地理类图书'),
('N', '自然科学总论', NULL, 1, 12, '自然科学综合类图书'),
('O', '数理科学和化学', NULL, 1, 13, '数理化学类图书'),
('P', '天文学、地球科学', NULL, 1, 14, '天文地学类图书'),
('Q', '生物科学', NULL, 1, 15, '生物学类图书'),
('R', '医药、卫生', NULL, 1, 16, '医学类图书'),
('S', '农业科学', NULL, 1, 17, '农业类图书'),
('T', '工业技术', NULL, 1, 18, '工程技术类图书'),
('U', '交通运输', NULL, 1, 19, '交通运输类图书'),
('V', '航空、航天', NULL, 1, 20, '航空航天类图书'),
('X', '环境科学、安全科学', NULL, 1, 21, '环境安全类图书'),
('Z', '综合性图书', NULL, 1, 22, '综合类图书');

-- 插入二级分类（计算机相关）
INSERT INTO categories (category_code, category_name, parent_category_id, category_level, sort_order, description) VALUES 
('TP', '自动化技术、计算机技术', 18, 2, 1, '计算机技术类图书'),
('TP3', '计算技术、计算机技术', 23, 3, 1, '计算机硬件技术'),
('TP31', '计算机软件', 24, 4, 1, '计算机软件开发'),
('TP393', '计算机网络', 24, 4, 2, '计算机网络技术');

-- 插入系统配置数据
INSERT INTO system_config (config_key, config_value, config_type, config_group, description) VALUES 
('system.name', '图书馆管理系统', 'string', 'system', '系统名称'),
('system.version', '1.0.0', 'string', 'system', '系统版本'),
('borrow.default_days', '30', 'integer', 'borrow', '默认借阅天数'),
('borrow.max_books_student', '5', 'integer', 'borrow', '学生最大借书数量'),
('borrow.max_books_teacher', '10', 'integer', 'borrow', '教师最大借书数量'),
('borrow.max_renewal_times', '2', 'integer', 'borrow', '最大续借次数'),
('borrow.renewal_days', '15', 'integer', 'borrow', '续借天数'),
('fine.overdue_fee_per_day', '0.50', 'decimal', 'fine', '每日逾期费用（元）'),
('fine.max_overdue_fee', '50.00', 'decimal', 'fine', '最大逾期费用（元）'),
('reservation.expiry_days', '3', 'integer', 'reservation', '预约保留天数'),
('reservation.max_reservations', '3', 'integer', 'reservation', '每人最大预约数量'),
('notification.overdue_advance_days', '3', 'integer', 'notification', '逾期提前通知天数'),
('backup.auto_backup', 'true', 'boolean', 'backup', '是否自动备份'),
('backup.backup_interval_hours', '24', 'integer', 'backup', '备份间隔（小时）');

-- 插入示例图书数据
INSERT INTO books (isbn, title, author, publisher, publication_date, category_id, language, pages, price, description, location, total_copies, available_copies) VALUES 
('9787121301234', 'Java编程思想（第4版）', '[美] Bruce Eckel', '电子工业出版社', '2021-03-01', 25, 'Chinese', 880, 89.00, 'Java编程经典教材，深入讲解Java语言特性和编程思想', 'A区1楼101', 3, 3),
('9787111627905', '深入理解计算机系统（第3版）', '[美] Randal E. Bryant, David R. O''Hallaron', '机械工业出版社', '2020-05-01', 24, 'Chinese', 736, 139.00, '计算机系统原理经典教材', 'A区1楼102', 2, 2),
('9787115548439', 'Python编程：从入门到实践（第2版）', '[美] Eric Matthes', '人民邮电出版社', '2020-07-01', 25, 'Chinese', 564, 89.00, 'Python编程入门经典教材', 'A区1楼103', 5, 4),
('9787302547891', '数据结构与算法分析', '[美] Mark Allen Weiss', '清华大学出版社', '2021-01-01', 25, 'Chinese', 432, 69.00, '数据结构与算法经典教材', 'A区1楼104', 4, 4),
('9787121385902', '计算机网络（第7版）', '谢希仁', '电子工业出版社', '2020-09-01', 26, 'Chinese', 512, 79.00, '计算机网络原理教材', 'A区1楼105', 3, 3),
('9787040525656', '高等数学（第七版上册）', '同济大学数学系', '高等教育出版社', '2019-08-01', 13, 'Chinese', 398, 45.00, '高等数学教材上册', 'B区1楼201', 10, 10),
('9787040525663', '高等数学（第七版下册）', '同济大学数学系', '高等教育出版社', '2019-08-01', 13, 'Chinese', 356, 42.00, '高等数学教材下册', 'B区1楼202', 10, 10),
('9787040396638', '线性代数（第六版）', '同济大学数学系', '高等教育出版社', '2021-02-01', 13, 'Chinese', 312, 38.00, '线性代数教材', 'B区1楼203', 8, 8),
('9787302569312', '数据库系统概论（第5版）', '王珊，萨师煊', '清华大学出版社', '2020-11-01', 25, 'Chinese', 628, 89.00, '数据库系统经典教材', 'A区1楼106', 3, 2),
('9787111687238', '操作系统概念（第9版）', '[美] Abraham Silberschatz', '机械工业出版社', '2021-06-01', 25, 'Chinese', 764, 128.00, '操作系统原理经典教材', 'A区1楼107', 2, 2);

-- 插入示例借阅记录
INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, status, operator_id) VALUES 
(4, 3, '2024-06-01', '2024-07-01', 'borrowed', 1),
(4, 9, '2024-06-05', '2024-07-05', 'borrowed', 1),
(5, 1, '2024-06-03', '2024-07-03', 'borrowed', 2);

-- 更新图书库存
UPDATE books SET available_copies = available_copies - 1, borrowed_copies = borrowed_copies + 1 WHERE book_id IN (1, 3, 9);

-- 插入示例预约记录
INSERT INTO reservations (user_id, book_id, reservation_date, queue_position, status) VALUES 
(5, 9, '2024-06-10', 1, 'waiting');

-- 插入初始操作日志
INSERT INTO operation_logs (user_id, operation_type, operation_module, operation_description, operation_result) VALUES 
(1, '登录', '用户管理', '管理员登录系统', 'success'),
(1, '数据初始化', '系统管理', '初始化系统基础数据', 'success'),
(1, '图书录入', '图书管理', '批量录入示例图书', 'success');

-- ========================================
-- 10. 创建触发器（用于自动更新时间戳）
-- ========================================

-- 用户表更新时间触发器
CREATE TRIGGER trigger_users_updated_time 
    AFTER UPDATE ON users
    FOR EACH ROW
BEGIN
    UPDATE users SET updated_time = CURRENT_TIMESTAMP WHERE user_id = NEW.user_id;
END;

-- 分类表更新时间触发器
CREATE TRIGGER trigger_categories_updated_time 
    AFTER UPDATE ON categories
    FOR EACH ROW
BEGIN
    UPDATE categories SET updated_time = CURRENT_TIMESTAMP WHERE category_id = NEW.category_id;
END;

-- 图书表更新时间触发器
CREATE TRIGGER trigger_books_updated_time 
    AFTER UPDATE ON books
    FOR EACH ROW
BEGIN
    UPDATE books SET updated_time = CURRENT_TIMESTAMP WHERE book_id = NEW.book_id;
END;

-- 借阅记录表更新时间触发器
CREATE TRIGGER trigger_borrow_records_updated_time 
    AFTER UPDATE ON borrow_records
    FOR EACH ROW
BEGIN
    UPDATE borrow_records SET updated_time = CURRENT_TIMESTAMP WHERE record_id = NEW.record_id;
END;

-- 预约记录表更新时间触发器
CREATE TRIGGER trigger_reservations_updated_time 
    AFTER UPDATE ON reservations
    FOR EACH ROW
BEGIN
    UPDATE reservations SET updated_time = CURRENT_TIMESTAMP WHERE reservation_id = NEW.reservation_id;
END;

-- 系统配置表更新时间触发器
CREATE TRIGGER trigger_system_config_updated_time 
    AFTER UPDATE ON system_config
    FOR EACH ROW
BEGIN
    UPDATE system_config SET updated_time = CURRENT_TIMESTAMP WHERE config_id = NEW.config_id;
END;

-- ========================================
-- 数据库初始化完成
-- ========================================

-- 验证数据插入结果
SELECT '用户数据验证' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT '分类数据验证', COUNT(*) FROM categories
UNION ALL
SELECT '图书数据验证', COUNT(*) FROM books
UNION ALL
SELECT '配置数据验证', COUNT(*) FROM system_config
UNION ALL
SELECT '借阅记录验证', COUNT(*) FROM borrow_records
UNION ALL
SELECT '预约记录验证', COUNT(*) FROM reservations
UNION ALL
SELECT '操作日志验证', COUNT(*) FROM operation_logs;