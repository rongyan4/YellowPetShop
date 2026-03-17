package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.model.pet.PetProfileDTO;
import com.yellow.petshop.model.pet.PetProfileVO;
import com.yellow.petshop.service.PetProfileService;
import com.yellow.petshop.util.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 宠物档案控制器
 */
@RestController
@RequestMapping("/api/pet-profile")
public class PetProfileController extends BaseController {

    @Autowired
    private PetProfileService petProfileService;

    /**
     * 获取当前用户的宠物档案列表
     */
    @GetMapping("/list")
    public Result<List<PetProfileVO>> getList(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<PetProfileVO> list = petProfileService.getList(userId);
        return Result.success(list);
    }

    /**
     * 获取档案详情
     */
    @GetMapping("/{id}")
    public Result<PetProfileVO> getDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        PetProfileVO detail = petProfileService.getDetail(id, userId);
        return Result.success(detail);
    }

    /**
     * 添加档案
     */
    @PostMapping("/add")
    public Result<Long> add(@RequestBody PetProfileDTO dto, HttpServletRequest request) {
        Long userId = getUserId(request);
        Long id = petProfileService.add(dto, userId);
        return Result.success(id);
    }

    /**
     * 更新档案
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody PetProfileDTO dto, HttpServletRequest request) {
        Long userId = getUserId(request);
        petProfileService.update(dto, userId);
        return Result.success(null);
    }

    /**
     * 删除档案
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        petProfileService.delete(id, userId);
        return Result.success(null);
    }

    /**
     * 上传宠物头像（无需鉴权）
     */
    @PostMapping("/upload-avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        FileUploadUtil.UploadResult result = FileUploadUtil.uploadFile(
                file,
                FileUploadUtil.BusinessType.USER_AVATAR,
                null
        );
        if (!result.isSuccess()) {
            return Result.error(result.getMessage());
        }
        return Result.success(result.getImageUrl());
    }

    /**
     * 根据档案推荐商品
     */
    @GetMapping("/recommend/{petId}")
    public Result<List<CommodityInfo>> getRecommendGoods(@PathVariable Long petId, HttpServletRequest request) {
        Long userId = getUserId(request);
        List<CommodityInfo> goods = petProfileService.getRecommendGoods(petId, userId);
        return Result.success(goods);
    }
}
