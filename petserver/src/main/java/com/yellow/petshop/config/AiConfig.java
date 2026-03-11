package com.yellow.petshop.config;

import com.yellow.petshop.tools.CommodityTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(DeepSeekChatModel chatModel, CommodityTools commodityTools){
        return ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())//会话日志
                .defaultTools(commodityTools)
                .build();
    }
}
