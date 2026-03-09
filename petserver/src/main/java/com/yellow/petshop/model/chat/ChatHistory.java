package com.yellow.petshop.model.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_history")
public class ChatHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String sessionId;
    
    private LocalDateTime datetime;
    
    private String content;
    
    private String role;
}
