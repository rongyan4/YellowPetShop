package com.yellow.petshop.service;

import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.model.pet.PetProfile;
import com.yellow.petshop.model.pet.PetProfileDTO;
import com.yellow.petshop.model.pet.PetProfileVO;

import java.util.List;

/**
 * 宠物档案服务接口
 */
public interface PetProfileService {
    
    /**
     * 获取当前用户的宠物档案列表
     */
    List<PetProfileVO> getList(Long userId);
    
    /**
     * 获取档案详情
     */
    PetProfileVO getDetail(Long id, Long userId);
    
    /**
     * 添加档案
     */
    Long add(PetProfileDTO dto, Long userId);
    
    /**
     * 更新档案
     */
    void update(PetProfileDTO dto, Long userId);
    
    /**
     * 删除档案
     */
    void delete(Long id, Long userId);
    
    /**
     * 根据档案推荐商品
     */
    List<CommodityInfo> getRecommendGoods(Long petId, Long userId);
}
