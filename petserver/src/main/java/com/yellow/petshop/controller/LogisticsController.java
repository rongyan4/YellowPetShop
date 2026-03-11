package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.logistics.LogisticsDTO;
import com.yellow.petshop.model.logistics.LogisticsVO;
import com.yellow.petshop.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流控制器
 */
@RestController
@RequestMapping("/api/logistics")
public class LogisticsController {
    
    @Autowired
    private LogisticsService logisticsService;
    
    /**
     * 添加物流信息
     */
    @PostMapping("/add")
    public Result<Long> addLogistics(@RequestBody LogisticsDTO logisticsDTO) {
        try {
            Long logisticsId = logisticsService.addLogistics(logisticsDTO);
            return Result.success(logisticsId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新物流信息
     */
    @PutMapping("/update/{logisticsId}")
    public Result<String> updateLogistics(
            @PathVariable Long logisticsId,
            @RequestBody LogisticsDTO logisticsDTO) {
        try {
            logisticsService.updateLogistics(logisticsId, logisticsDTO);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单的物流信息列表
     */
    @GetMapping("/order/{orderId}")
    public Result<List<LogisticsVO>> getLogisticsByOrderId(@PathVariable Long orderId) {
        try {
            List<LogisticsVO> list = logisticsService.getLogisticsByOrderId(orderId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取物流详情
     */
    @GetMapping("/detail/{logisticsId}")
    public Result<LogisticsVO> getLogisticsDetail(@PathVariable Long logisticsId) {
        try {
            LogisticsVO vo = logisticsService.getLogisticsDetail(logisticsId);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除物流信息
     */
    @DeleteMapping("/delete/{logisticsId}")
    public Result<String> deleteLogistics(@PathVariable Long logisticsId) {
        try {
            logisticsService.deleteLogistics(logisticsId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
