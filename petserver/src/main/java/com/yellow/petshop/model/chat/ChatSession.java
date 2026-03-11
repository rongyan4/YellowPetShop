package com.yellow.petshop.model.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_session")
public class ChatSession {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String sessionId;
    
    private Long userId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
