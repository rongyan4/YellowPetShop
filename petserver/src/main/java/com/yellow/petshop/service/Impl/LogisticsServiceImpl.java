package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.LogisticsMapper;
import com.yellow.petshop.mapper.OrderLogisticsMapper;
import com.yellow.petshop.model.logistics.Logistics;
import com.yellow.petshop.model.logistics.LogisticsDTO;
import com.yellow.petshop.model.logistics.LogisticsVO;
import com.yellow.petshop.model.logistics.OrderLogistics;
import com.yellow.petshop.service.LogisticsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物流服务实现类
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {
    
    @Autowired
    private LogisticsMapper logisticsMapper;
    
    @Autowired
    private OrderLogisticsMapper orderLogisticsMapper;
    
    @Override
    @Transactional
    public Long addLogistics(LogisticsDTO logisticsDTO) {
        // 创建物流信息
        Logistics logistics = new Logistics();
        logistics.setShippingCompany(logisticsDTO.getShippingCompany());
        logistics.setTrackingNo(logisticsDTO.getTrackingNo());
        logistics.setRemark(logisticsDTO.getRemark());
        logistics.setStatus("SHIPPED");
        logistics.setShippingTime(LocalDateTime.now());
        logistics.setCreateTime(LocalDateTime.now());
        logistics.setUpdateTime(LocalDateTime.now());
        
        logisticsMapper.insert(logistics);
        
        // 创建订单物流映射
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderId(logisticsDTO.getOrderId());
        orderLogistics.setLogisticsId(logistics.getId());
        orderLogistics.setCreateTime(LocalDateTime.now());
        
        orderLogisticsMapper.insert(orderLogistics);
        
        return logistics.getId();
    }
    
    @Override
    @Transactional
    public Boolean updateLogistics(Long logisticsId, LogisticsDTO logisticsDTO) {
        Logistics logistics = logisticsMapper.selectById(logisticsId);
        if (logistics == null) {
            throw new RuntimeException("物流信息不存在");
        }
        
        logistics.setShippingCompany(logisticsDTO.getShippingCompany());
        logistics.setTrackingNo(logisticsDTO.getTrackingNo());
        logistics.setRemark(logisticsDTO.getRemark());
        logistics.setUpdateTime(LocalDateTime.now());
        
        return logisticsMapper.updateById(logistics) > 0;
    }
    
    @Override
    public List<LogisticsVO> getLogisticsByOrderId(Long orderId) {
        // 查询订单的所有物流映射
        QueryWrapper<OrderLogistics> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        List<OrderLogistics> orderLogisticsList = orderLogisticsMapper.selectList(wrapper);
        
        // 查询物流详情
        return orderLogisticsList.stream()
                .map(ol -> {
                    Logistics logistics = logisticsMapper.selectById(ol.getLogisticsId());
                    if (logistics != null) {
                        LogisticsVO vo = new LogisticsVO();
                        BeanUtils.copyProperties(logistics, vo);
                        return vo;
                    }
                    return null;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
    }
    
    @Override
    public LogisticsVO getLogisticsDetail(Long logisticsId) {
        Logistics logistics = logisticsMapper.selectById(logisticsId);
        if (logistics == null) {
            throw new RuntimeException("物流信息不存在");
        }
        
        LogisticsVO vo = new LogisticsVO();
        BeanUtils.copyProperties(logistics, vo);
        return vo;
    }
    
    @Override
    @Transactional
    public Boolean deleteLogistics(Long logisticsId) {
        // 删除订单物流映射
        QueryWrapper<OrderLogistics> wrapper = new QueryWrapper<>();
        wrapper.eq("logistics_id", logisticsId);
        orderLogisticsMapper.delete(wrapper);
        
        // 删除物流信息
        return logisticsMapper.deleteById(logisticsId) > 0;
    }
}
