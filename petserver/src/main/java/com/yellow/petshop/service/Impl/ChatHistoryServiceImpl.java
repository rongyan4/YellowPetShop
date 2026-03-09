package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yellow.petshop.mapper.ChatHistoryMapper;
import com.yellow.petshop.model.chat.ChatHistory;
import com.yellow.petshop.model.chat.ChatHistoryVO;
import com.yellow.petshop.service.ChatHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {
    
    @Override
    public void saveMessage(String sessionId, String content, String role) {
        ChatHistory history = new ChatHistory();
        history.setSessionId(sessionId);
        history.setContent(content);
        history.setRole(role);
        history.setDatetime(LocalDateTime.now());
        this.save(history);
    }
    
    @Override
    public List<ChatHistoryVO> getSessionHistory(String sessionId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getSessionId, sessionId)
               .orderByAsc(ChatHistory::getDatetime);
        
        List<ChatHistory> histories = this.list(wrapper);
        return histories.stream().map(history -> {
            ChatHistoryVO vo = new ChatHistoryVO();
            BeanUtils.copyProperties(history, vo);
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public void clearSessionHistory(String sessionId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getSessionId, sessionId);
        this.remove(wrapper);
    }
}
