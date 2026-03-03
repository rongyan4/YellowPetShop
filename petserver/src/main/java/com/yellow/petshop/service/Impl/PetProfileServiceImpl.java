package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.CommodityMapper;
import com.yellow.petshop.mapper.PetProfileMapper;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.model.pet.PetProfile;
import com.yellow.petshop.model.pet.PetProfileDTO;
import com.yellow.petshop.model.pet.PetProfileVO;
import com.yellow.petshop.service.PetProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宠物档案服务实现类
 */
@Service
public class PetProfileServiceImpl implements PetProfileService {
    
    @Autowired
    private PetProfileMapper petProfileMapper;
    
    @Autowired
    private CommodityMapper commodityMapper;
    
    @Override
    public List<PetProfileVO> getList(Long userId) {
        List<PetProfile> profiles = petProfileMapper.findByUserId(userId);
        return profiles.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public PetProfileVO getDetail(Long id, Long userId) {
        // 检查权限
        if (petProfileMapper.checkOwnership(id, userId) == 0) {
            throw new RuntimeException("无权访问该档案");
        }
        
        PetProfile profile = petProfileMapper.findById(id);
        if (profile == null) {
            throw new RuntimeException("档案不存在");
        }
        
        return convertToVO(profile);
    }
    
    @Override
    public Long add(PetProfileDTO dto, Long userId) {
        // 验证必填字段
        if (dto.getPetName() == null || dto.getPetName().trim().isEmpty()) {
            throw new RuntimeException("宠物名称不能为空");
        }
        if (dto.getPetType() == null || dto.getPetType().trim().isEmpty()) {
            throw new RuntimeException("宠物类型不能为空");
        }
        if (dto.getAgeStage() == null || dto.getAgeStage().trim().isEmpty()) {
            throw new RuntimeException("年龄段不能为空");
        }
        
        PetProfile profile = new PetProfile();
        BeanUtils.copyProperties(dto, profile);
        profile.setUserId(userId);
        
        // 设置默认值
        if (profile.getIsShedding() == null) profile.setIsShedding(false);
        if (profile.getIsSkinSensitive() == null) profile.setIsSkinSensitive(false);
        if (profile.getIsStomachSensitive() == null) profile.setIsStomachSensitive(false);
        if (profile.getHasDentalIssue() == null) profile.setHasDentalIssue(false);
        if (profile.getHasJointIssue() == null) profile.setHasJointIssue(false);
        if (profile.getHasTearStain() == null) profile.setHasTearStain(false);
        if (profile.getIsOverweight() == null) profile.setIsOverweight(false);
        if (profile.getIsPickyEater() == null) profile.setIsPickyEater(false);
        
        petProfileMapper.insert(profile);
        return profile.getId();
    }
    
    @Override
    public void update(PetProfileDTO dto, Long userId) {
        if (dto.getId() == null) {
            throw new RuntimeException("档案ID不能为空");
        }
        
        // 检查权限
        if (petProfileMapper.checkOwnership(dto.getId(), userId) == 0) {
            throw new RuntimeException("无权修改该档案");
        }
        
        // 验证必填字段
        if (dto.getPetName() == null || dto.getPetName().trim().isEmpty()) {
            throw new RuntimeException("宠物名称不能为空");
        }
        if (dto.getPetType() == null || dto.getPetType().trim().isEmpty()) {
            throw new RuntimeException("宠物类型不能为空");
        }
        if (dto.getAgeStage() == null || dto.getAgeStage().trim().isEmpty()) {
            throw new RuntimeException("年龄段不能为空");
        }
        
        PetProfile profile = new PetProfile();
        BeanUtils.copyProperties(dto, profile);
        profile.setUserId(userId);
        
        // 设置默认值
        if (profile.getIsShedding() == null) profile.setIsShedding(false);
        if (profile.getIsSkinSensitive() == null) profile.setIsSkinSensitive(false);
        if (profile.getIsStomachSensitive() == null) profile.setIsStomachSensitive(false);
        if (profile.getHasDentalIssue() == null) profile.setHasDentalIssue(false);
        if (profile.getHasJointIssue() == null) profile.setHasJointIssue(false);
        if (profile.getHasTearStain() == null) profile.setHasTearStain(false);
        if (profile.getIsOverweight() == null) profile.setIsOverweight(false);
        if (profile.getIsPickyEater() == null) profile.setIsPickyEater(false);
        
        int result = petProfileMapper.update(profile);
        if (result == 0) {
            throw new RuntimeException("更新失败");
        }
    }
    
    @Override
    public void delete(Long id, Long userId) {
        // 检查权限
        if (petProfileMapper.checkOwnership(id, userId) == 0) {
            throw new RuntimeException("无权删除该档案");
        }
        
        int result = petProfileMapper.delete(id, userId);
        if (result == 0) {
            throw new RuntimeException("删除失败");
        }
    }
    
    @Override
    public List<CommodityInfo> getRecommendGoods(Long petId, Long userId) {
        // 检查权限
        if (petProfileMapper.checkOwnership(petId, userId) == 0) {
            throw new RuntimeException("无权访问该档案");
        }
        
        PetProfile profile = petProfileMapper.findById(petId);
        if (profile == null) {
            throw new RuntimeException("档案不存在");
        }
        
        // 根据体质特征生成推荐关键词
        List<String> keywords = new ArrayList<>();
        
        if (Boolean.TRUE.equals(profile.getIsShedding())) {
            keywords.addAll(Arrays.asList("美毛", "化毛膏", "卵磷脂", "鱼油"));
        }
        if (Boolean.TRUE.equals(profile.getIsSkinSensitive())) {
            keywords.addAll(Arrays.asList("低敏", "皮肤护理", "无谷"));
        }
        if (Boolean.TRUE.equals(profile.getIsStomachSensitive())) {
            keywords.addAll(Arrays.asList("益生菌", "易消化", "肠胃"));
        }
        if (Boolean.TRUE.equals(profile.getHasDentalIssue())) {
            keywords.addAll(Arrays.asList("洁齿", "口腔", "磨牙"));
        }
        if (Boolean.TRUE.equals(profile.getHasJointIssue())) {
            keywords.addAll(Arrays.asList("关节", "软骨素", "钙"));
        }
        if (Boolean.TRUE.equals(profile.getHasTearStain())) {
            keywords.addAll(Arrays.asList("泪痕", "眼部"));
        }
        if (Boolean.TRUE.equals(profile.getIsOverweight())) {
            keywords.addAll(Arrays.asList("减肥", "低脂", "控制体重"));
        }
        if (Boolean.TRUE.equals(profile.getIsPickyEater())) {
            keywords.addAll(Arrays.asList("适口性", "营养", "冻干"));
        }
        
        // 根据年龄段添加关键词
        if ("puppy".equals(profile.getAgeStage())) {
            if ("cat".equals(profile.getPetType())) {
                keywords.add("幼猫");
            } else if ("dog".equals(profile.getPetType())) {
                keywords.add("幼犬");
            }
        } else if ("senior".equals(profile.getAgeStage())) {
            keywords.add("老年");
        }
        
        // 根据宠物类型添加关键词
        if ("cat".equals(profile.getPetType())) {
            keywords.add("猫");
        } else if ("dog".equals(profile.getPetType())) {
            keywords.add("狗");
        }
        
        // 如果没有任何关键词，返回热门商品
        if (keywords.isEmpty()) {
            return commodityMapper.selectHotGoods(20);
        }
        
        // 根据关键词搜索商品（使用OR逻辑，匹配任一关键词即可）
        List<CommodityInfo> result = new ArrayList<>();
        for (String keyword : keywords) {
            List<CommodityInfo> goods = commodityMapper.searchByKeyword(keyword, 5);
            result.addAll(goods);
        }
        
        // 去重并限制数量
        return result.stream()
                .distinct()
                .limit(20)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为VO对象
     */
    private PetProfileVO convertToVO(PetProfile profile) {
        PetProfileVO vo = new PetProfileVO();
        BeanUtils.copyProperties(profile, vo);
        
        // 转换中文文本
        vo.setPetTypeText(convertPetType(profile.getPetType()));
        vo.setAgeStageText(convertAgeStage(profile.getAgeStage()));
        vo.setBodySizeText(convertBodySize(profile.getBodySize()));
        vo.setGenderText(convertGender(profile.getGender()));
        vo.setActivityLevelText(convertActivityLevel(profile.getActivityLevel()));
        vo.setFoodPreferenceText(convertFoodPreference(profile.getFoodPreference()));
        
        // 生成体质特征标签
        List<String> healthTags = new ArrayList<>();
        if (Boolean.TRUE.equals(profile.getIsShedding())) healthTags.add("易掉毛");
        if (Boolean.TRUE.equals(profile.getIsSkinSensitive())) healthTags.add("皮肤敏感");
        if (Boolean.TRUE.equals(profile.getIsStomachSensitive())) healthTags.add("肠胃敏感");
        if (Boolean.TRUE.equals(profile.getHasDentalIssue())) healthTags.add("口腔问题");
        if (Boolean.TRUE.equals(profile.getHasJointIssue())) healthTags.add("关节问题");
        if (Boolean.TRUE.equals(profile.getHasTearStain())) healthTags.add("泪痕问题");
        if (Boolean.TRUE.equals(profile.getIsOverweight())) healthTags.add("肥胖倾向");
        if (Boolean.TRUE.equals(profile.getIsPickyEater())) healthTags.add("挑食");
        vo.setHealthTags(healthTags);
        
        return vo;
    }
    
    private String convertPetType(String type) {
        if (type == null) return "";
        switch (type) {
            case "cat": return "猫咪";
            case "dog": return "狗狗";
            case "other": return "其他";
            default: return type;
        }
    }
    
    private String convertAgeStage(String stage) {
        if (stage == null) return "";
        switch (stage) {
            case "puppy": return "幼年期";
            case "adult": return "成年期";
            case "senior": return "老年期";
            default: return stage;
        }
    }
    
    private String convertBodySize(String size) {
        if (size == null) return "";
        switch (size) {
            case "small": return "小型";
            case "medium": return "中型";
            case "large": return "大型";
            default: return size;
        }
    }
    
    private String convertGender(String gender) {
        if (gender == null) return "";
        switch (gender) {
            case "male": return "公";
            case "female": return "母";
            default: return gender;
        }
    }
    
    private String convertActivityLevel(String level) {
        if (level == null) return "";
        switch (level) {
            case "low": return "低";
            case "medium": return "中";
            case "high": return "高";
            default: return level;
        }
    }
    
    private String convertFoodPreference(String preference) {
        if (preference == null) return "";
        switch (preference) {
            case "dry": return "干粮";
            case "wet": return "湿粮";
            case "mixed": return "混合";
            default: return preference;
        }
    }
}
