package com.yellow.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yellow.petshop.model.chat.ChatSession;
import com.yellow.petshop.model.chat.ChatSessionVO;

import java.util.List;

public interface ChatSessionService extends IService<ChatSession> {
    
    /**
     * 创建新会话
     */
    String createSession(Long userId);
    
    /**
     * 获取用户的所有会话
     */
    List<ChatSessionVO> getUserSessions(Long userId);
    
    /**
     * 验证会话是否属于用户
     */
    boolean validateSession(String sessionId, Long userId);
    
    /**
     * 更新会话时间
     */
    void updateSessionTime(String sessionId);
}
