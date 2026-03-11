package com.yellow.petshop.model.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatHistoryVO {
    private Long id;
    private String content;
    private String role;
    private LocalDateTime datetime;
}
