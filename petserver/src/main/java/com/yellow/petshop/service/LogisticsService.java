package com.yellow.petshop.service;

import com.yellow.petshop.model.logistics.LogisticsDTO;
import com.yellow.petshop.model.logistics.LogisticsVO;

import java.util.List;

/**
 * 物流服务接口
 */
public interface LogisticsService {
    
    /**
     * 添加物流信息
     * @param logisticsDTO 物流信息
     * @return 物流ID
     */
    Long addLogistics(LogisticsDTO logisticsDTO);
    
    /**
     * 更新物流信息
     * @param logisticsId 物流ID
     * @param logisticsDTO 物流信息
     * @return 是否成功
     */
    Boolean updateLogistics(Long logisticsId, LogisticsDTO logisticsDTO);
    
    /**
     * 获取订单的物流信息列表
     * @param orderId 订单ID
     * @return 物流信息列表
     */
    List<LogisticsVO> getLogisticsByOrderId(Long orderId);
    
    /**
     * 获取物流详情
     * @param logisticsId 物流ID
     * @return 物流详情
     */
    LogisticsVO getLogisticsDetail(Long logisticsId);
    
    /**
     * 删除物流信息
     * @param logisticsId 物流ID
     * @return 是否成功
     */
    Boolean deleteLogistics(Long logisticsId);
}
