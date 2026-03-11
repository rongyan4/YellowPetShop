package com.yellow.petshop.model.chat;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatSessionVO {
    private String sessionId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String lastMessage;
    private List<ChatHistoryVO> messages;
}
