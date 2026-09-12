-- 1. 创建数据库
CREATE DATABASE PomodoroDB;
GO

USE PomodoroDB;
GO

-- 2. users 表
CREATE TABLE users (
    id VARCHAR(10) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(128) NOT NULL,
    create_time DATETIME DEFAULT GETDATE()
);

-- 3. tasks 表
CREATE TABLE tasks (
    id VARCHAR(10) PRIMARY KEY,
    user_id VARCHAR(10) NOT NULL,
    title NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    tags NVARCHAR(200),
    priority INT DEFAULT 1,
    type INT NOT NULL,
    timer_mode INT NOT NULL,
    target_count INT DEFAULT 1,
    completed_count INT DEFAULT 0,
    pomodoro_minutes INT DEFAULT 25,
    due_date DATETIME NULL,
    start_date DATETIME NULL,
    repeat_days INT DEFAULT 0,
    daily_deadline NVARCHAR(10) DEFAULT '23:59',
    status INT DEFAULT 0,
    create_time DATETIME DEFAULT GETDATE(),
    complete_time DATETIME NULL,
    deleted BIT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 4. pomodoro_records 表
CREATE TABLE pomodoro_records (
    id VARCHAR(10) PRIMARY KEY,
    user_id VARCHAR(10) NOT NULL,
    task_id VARCHAR(10) NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    duration INT,
    status INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- 5. task_daily_records 表
CREATE TABLE task_daily_records (
    id VARCHAR(10) PRIMARY KEY,
    task_id VARCHAR(10) NOT NULL,
    user_id VARCHAR(10) NOT NULL,
    record_date DATE NOT NULL,
    target_count INT,
    completed_count INT DEFAULT 0,
    overdue BIT DEFAULT 0,
    UNIQUE (task_id, record_date),
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 6. 索引
CREATE INDEX idx_task_user ON tasks(user_id);
CREATE INDEX idx_record_user_time ON pomodoro_records(user_id, start_time);
CREATE INDEX idx_daily_user_date ON task_daily_records(user_id, record_date);
GO