package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yellow.petshop.mapper.ChatSessionMapper;
import com.yellow.petshop.model.chat.ChatHistory;
import com.yellow.petshop.model.chat.ChatHistoryVO;
import com.yellow.petshop.model.chat.ChatSession;
import com.yellow.petshop.model.chat.ChatSessionVO;
import com.yellow.petshop.service.ChatHistoryService;
import com.yellow.petshop.service.ChatSessionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatSessionService {
    
    @Resource
    private ChatHistoryService chatHistoryService;
    
    @Override
    public String createSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        this.save(session);
        return sessionId;
    }
    
    @Override
    public List<ChatSessionVO> getUserSessions(Long userId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getUserId, userId)
               .orderByDesc(ChatSession::getUpdateTime);
        
        List<ChatSession> sessions = this.list(wrapper);
        return sessions.stream().map(session -> {
            ChatSessionVO vo = new ChatSessionVO();
            vo.setSessionId(session.getSessionId());
            vo.setCreateTime(session.getCreateTime());
            vo.setUpdateTime(session.getUpdateTime());
            
            // 获取最后一条消息
            List<ChatHistoryVO> histories = chatHistoryService.getSessionHistory(session.getSessionId());
            if (!histories.isEmpty()) {
                vo.setLastMessage(histories.get(histories.size() - 1).getContent());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public boolean validateSession(String sessionId, Long userId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getSessionId, sessionId)
               .eq(ChatSession::getUserId, userId);
        return this.count(wrapper) > 0;
    }
    
    @Override
    public void updateSessionTime(String sessionId) {
        LambdaUpdateWrapper<ChatSession> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatSession::getSessionId, sessionId)
               .set(ChatSession::getUpdateTime, LocalDateTime.now());
        this.update(wrapper);
    }
}
