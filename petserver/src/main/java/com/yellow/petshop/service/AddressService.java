package com.yellow.petshop.service;

import com.yellow.petshop.model.address.UserAddress;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {
    
    /**
     * 获取用户所有地址
     */
    List<UserAddress> getUserAddresses(Long userId);
    
    /**
     * 获取用户默认地址
     */
    UserAddress getDefaultAddress(Long userId);
    
    /**
     * 添加地址
     */
    Boolean addAddress(Long userId, UserAddress address);
    
    /**
     * 设置默认地址
     */
    Boolean setDefaultAddress(Long userId, Long addressId);
}
