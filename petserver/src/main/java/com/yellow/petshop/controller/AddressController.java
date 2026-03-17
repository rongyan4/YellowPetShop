package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.address.UserAddress;
import com.yellow.petshop.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@RestController
@RequestMapping("/api/address")
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取用户所有地址
     */
    @GetMapping("/list")
    public Result<List<UserAddress>> getAddressList(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<UserAddress> addresses = addressService.getUserAddresses(userId);
        return Result.success(addresses);
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public Result<UserAddress> getDefaultAddress(HttpServletRequest request) {
        Long userId = getUserId(request);
        UserAddress address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }

    /**
     * 添加地址
     */
    @PostMapping("/add")
    public Result<String> addAddress(
            @RequestBody UserAddress address,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        Boolean success = addressService.addAddress(userId, address);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/setDefault/{addressId}")
    public Result<String> setDefaultAddress(
            @PathVariable Long addressId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        Boolean success = addressService.setDefaultAddress(userId, addressId);
        return success ? Result.success("设置成功") : Result.error("设置失败");
    }

    /**
     * 更新地址
     */
    @PutMapping("/update/{addressId}")
    public Result<String> updateAddress(
            @PathVariable Long addressId,
            @RequestBody UserAddress address,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        address.setId(addressId);
        address.setUserId(userId);
        Boolean success = addressService.updateAddress(address);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/delete/{addressId}")
    public Result<String> deleteAddress(
            @PathVariable Long addressId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        Boolean success = addressService.deleteAddress(userId, addressId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
