package com.yellow.petshop.tools;

import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.SearchService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Component
public class MemoryTools {

}

//package com.yellow.petshop.tools;
//
//import com.yellow.petshop.model.home.CommodityInfo;
//import com.yellow.petshop.service.SearchService;
//import com.yellow.petshop.util.JwtUtil;
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.ai.tool.annotation.ToolParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.util.List;
//
//@Component
//public class CommodityTools {
//
//    @Autowired
//    private SearchService searchService;
//
//    @Tool(name = "search_commodity",
//            description = "根据搜索内容精确匹配商品，返回商品列表信息")
//    public List<CommodityInfo> search(@ToolParam(description = "关键词") String keyword) {
//        Long userId = null;
//
//        // 尝试从当前请求上下文中获取用户token
//        try {
//            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            if (attributes != null) {
//                String token = attributes.getRequest().getHeader("Authorization");
//                if (token != null && token.startsWith("Bearer ")) {
//                    String jwtToken = token.substring(7);
//                    userId = JwtUtil.getUserIdFromToken(jwtToken);
//                }
//            }
//        } catch (Exception e) {
//            // 忽略异常，允许未登录用户搜索
//        }
//
//        return searchService.searchGoods(keyword, userId);
//    }
//}
