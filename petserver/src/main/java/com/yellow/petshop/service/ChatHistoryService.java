package com.yellow.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yellow.petshop.model.chat.ChatHistory;
import com.yellow.petshop.model.chat.ChatHistoryVO;

import java.util.List;

public interface ChatHistoryService extends IService<ChatHistory> {
    
    /**
     * 保存聊天消息
     */
    void saveMessage(String sessionId, String content, String role);
    
    /**
     * 获取会话历史记录
     */
    List<ChatHistoryVO> getSessionHistory(String sessionId);
    
    /**
     * 清空会话历史
     */
    void clearSessionHistory(String sessionId);
}
