package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.chat.ChatSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
}
