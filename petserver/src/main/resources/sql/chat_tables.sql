-- 创建聊天会话表
CREATE TABLE IF NOT EXISTS chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键自增ID',
    session_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天会话表';

-- 创建聊天历史记录表
CREATE TABLE IF NOT EXISTS chat_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键自增ID',
    session_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    datetime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间',
    content TEXT NOT NULL COMMENT '消息内容',
    role VARCHAR(20) NOT NULL COMMENT '角色（user/assistant）',
    INDEX idx_session_id (session_id),
    INDEX idx_datetime (datetime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天历史记录表';
