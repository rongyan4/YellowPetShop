package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.UserAddressMapper;
import com.yellow.petshop.model.address.UserAddress;
import com.yellow.petshop.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private UserAddressMapper addressMapper;
    
    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        return addressMapper.selectByUserId(userId);
    }
    
    @Override
    public UserAddress getDefaultAddress(Long userId) {
        return addressMapper.selectDefaultByUserId(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addAddress(Long userId, UserAddress address) {
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        
        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressMapper.cancelAllDefault(userId);
        }
        
        return addressMapper.insert(address) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDefaultAddress(Long userId, Long addressId) {
        // 验证地址是否属于该用户
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或无权限");
        }
        
        // 取消所有默认地址
        addressMapper.cancelAllDefault(userId);
        
        // 设置新的默认地址
        address.setIsDefault(true);
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.updateById(address) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAddress(UserAddress address) {
        // 验证地址是否存在
        UserAddress existingAddress = addressMapper.selectById(address.getId());
        if (existingAddress == null) {
            throw new RuntimeException("地址不存在");
        }
        
        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressMapper.cancelAllDefault(address.getUserId());
        }
        
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.updateById(address) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAddress(Long userId, Long addressId) {
        // 验证地址是否属于该用户
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该地址");
        }
        
        return addressMapper.deleteById(addressId) > 0;
    }
}
