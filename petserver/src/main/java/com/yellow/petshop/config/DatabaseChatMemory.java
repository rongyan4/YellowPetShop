package com.yellow.petshop.config;

import com.yellow.petshop.model.chat.ChatHistoryVO;
import com.yellow.petshop.service.ChatHistoryService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于数据库的持久化ChatMemory实现
 */
@Component
public class DatabaseChatMemory implements ChatMemory {
    
    @Resource
    private ChatHistoryService chatHistoryService;
    
    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            String role = message instanceof UserMessage ? "user" : "assistant";
            String content = message.getText();
            chatHistoryService.saveMessage(conversationId, content, role);
        }
    }
    
    @Override
    public void add(String conversationId, Message message) {
        String role = message instanceof UserMessage ? "user" : "assistant";
        String content = message.getText();
        chatHistoryService.saveMessage(conversationId, content, role);
    }
    
    @Override
    public List<Message> get(String conversationId) {
        List<ChatHistoryVO> histories = chatHistoryService.getSessionHistory(conversationId);
        
        List<Message> messages = new ArrayList<>();
        for (ChatHistoryVO history : histories) {
            if ("user".equals(history.getRole())) {
                messages.add(new UserMessage(history.getContent()));
            } else {
                messages.add(new AssistantMessage(history.getContent()));
            }
        }
        return messages;
    }
    
    /**
     * 获取最后N条消息
     */
    public List<Message> get(String conversationId, int lastN) {
        List<ChatHistoryVO> histories = chatHistoryService.getSessionHistory(conversationId);
        
        // 获取最后N条消息
        int start = Math.max(0, histories.size() - lastN);
        List<ChatHistoryVO> recentHistories = histories.subList(start, histories.size());
        
        List<Message> messages = new ArrayList<>();
        for (ChatHistoryVO history : recentHistories) {
            if ("user".equals(history.getRole())) {
                messages.add(new UserMessage(history.getContent()));
            } else {
                messages.add(new AssistantMessage(history.getContent()));
            }
        }
        return messages;
    }
    
    @Override
    public void clear(String conversationId) {
        chatHistoryService.clearSessionHistory(conversationId);
    }
}
