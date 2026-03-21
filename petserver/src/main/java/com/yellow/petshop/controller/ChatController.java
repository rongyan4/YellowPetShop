package com.yellow.petshop.controller;

import com.yellow.petshop.config.DatabaseChatMemory;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.chat.ChatHistoryVO;
import com.yellow.petshop.model.chat.ChatSessionVO;
import com.yellow.petshop.service.ChatHistoryService;
import com.yellow.petshop.service.ChatSessionService;
import com.yellow.petshop.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private final DeepSeekChatModel chatModel;
    @Resource
    private ChatClient chatClient;
    private String system_prompt;
    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private ChatSessionService chatSessionService;
    @Resource
    private DatabaseChatMemory databaseChatMemory;

    @Autowired
    public ChatController(DeepSeekChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/system_prompt_alpha.md");
            system_prompt = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load system prompt", e);
        }
    }

    /**
     * 从请求中提取 token（优先 Cookie，降级 Authorization 头）
     */
    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 创建新会话
     */
    @PostMapping("/session/create")
    public Result<String> createSession(HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = JwtUtil.getUserIdFromToken(token);
        String sessionId = chatSessionService.createSession(userId);
        return Result.success(sessionId);
    }

    /**
     * 获取用户所有会话
     */
    @GetMapping("/session/list")
    public Result<List<ChatSessionVO>> getUserSessions(HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = JwtUtil.getUserIdFromToken(token);
        List<ChatSessionVO> sessions = chatSessionService.getUserSessions(userId);
        return Result.success(sessions);
    }

    /**
     * 获取会话历史记录
     */
    @GetMapping("/history/{sessionId}")
    public Result<List<ChatHistoryVO>> getSessionHistory(
            @PathVariable String sessionId,
            HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = JwtUtil.getUserIdFromToken(token);

        // 验证会话是否属于该用户
        if (!chatSessionService.validateSession(sessionId, userId)) {
            return Result.error("无权访问该会话");
        }

        List<ChatHistoryVO> history = chatHistoryService.getSessionHistory(sessionId);
        return Result.success(history);
    }

    /**
     * 发送消息（SSE 流式输出）
     */
    @GetMapping(value = "/send", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> sendMessage(
            @RequestParam String message,
            @RequestParam String sessionId,
            HttpServletRequest request) {

        String token = extractToken(request);
        Long userId = JwtUtil.getUserIdFromToken(token);

        // 验证会话
        if (!chatSessionService.validateSession(sessionId, userId)) {
            return Flux.just("event: error\ndata: 无权访问该会话\n\n");
        }

        // 保存用户消息
        chatHistoryService.saveMessage(sessionId, message, "user");

        // 收集完整回复并保存，同时将每个 chunk 包装成 SSE data 帧推送给前端
        StringBuilder fullReply = new StringBuilder();
        // 调用AI生成流式回复
        return chatClient.prompt()
                .system(system_prompt)
                .user(message)
//                .advisors(MessageChatMemoryAdvisor.builder(databaseChatMemory)
//                        .conversationId(sessionId)
//                        .build())
                .stream()
                .content()
                .map(chunk -> chunk.replaceFirst("^data:\\s*", ""))// 去掉 data:
                .doOnNext(chunk -> fullReply.append(chunk))
                .doOnComplete(() -> {
                    chatHistoryService.saveMessage(sessionId, fullReply.toString(), "assistant");
                    chatSessionService.updateSessionTime(sessionId);
                })
                .concatWith(Flux.just("[DONE]"));// 流结束时发送 [DONE] 信号通知前端
    }

    /**
     * 清空会话历史
     */
    @DeleteMapping("/history/{sessionId}")
    public Result<String> clearHistory(
            @PathVariable String sessionId,
            HttpServletRequest request) {

        String token = extractToken(request);
        Long userId = JwtUtil.getUserIdFromToken(token);

        if (!chatSessionService.validateSession(sessionId, userId)) {
            return Result.error("无权访问该会话");
        }

        chatHistoryService.clearSessionHistory(sessionId);
        return Result.success("清空成功");
    }
}
