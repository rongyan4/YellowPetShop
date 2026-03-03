package com.yellow.petshop.model.pet;

import java.util.List;

/**
 * 宠物档案VO（返回给前端）
 */
public class PetProfileVO {
    private Long id;
    private String petName;
    private String petType;
    private String petTypeText;    // 中文：猫咪/狗狗/其他
    private String ageStage;
    private String ageStageText;   // 中文：幼年期/成年期/老年期
    private String bodySize;
    private String bodySizeText;   // 中文：小型/中型/大型
    private String gender;
    private String genderText;     // 中文：公/母
    private String avatarUrl;
    
    // 体质特征标签列表（前端直接展示）
    private List<String> healthTags; // ["易掉毛", "皮肤敏感"]
    
    // 原始布尔值（编辑时使用）
    private Boolean isShedding;
    private Boolean isSkinSensitive;
    private Boolean isStomachSensitive;
    private Boolean hasDentalIssue;
    private Boolean hasJointIssue;
    private Boolean hasTearStain;
    private Boolean isOverweight;
    private Boolean isPickyEater;
    
    private String activityLevel;
    private String activityLevelText;  // 中文：低/中/高
    private String foodPreference;
    private String foodPreferenceText; // 中文：干粮/湿粮/混合
    private String remark;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getPetTypeText() {
        return petTypeText;
    }

    public void setPetTypeText(String petTypeText) {
        this.petTypeText = petTypeText;
    }

    public String getAgeStage() {
        return ageStage;
    }

    public void setAgeStage(String ageStage) {
        this.ageStage = ageStage;
    }

    public String getAgeStageText() {
        return ageStageText;
    }

    public void setAgeStageText(String ageStageText) {
        this.ageStageText = ageStageText;
    }

    public String getBodySize() {
        return bodySize;
    }

    public void setBodySize(String bodySize) {
        this.bodySize = bodySize;
    }

    public String getBodySizeText() {
        return bodySizeText;
    }

    public void setBodySizeText(String bodySizeText) {
        this.bodySizeText = bodySizeText;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGenderText() {
        return genderText;
    }

    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<String> getHealthTags() {
        return healthTags;
    }

    public void setHealthTags(List<String> healthTags) {
        this.healthTags = healthTags;
    }

    public Boolean getIsShedding() {
        return isShedding;
    }

    public void setIsShedding(Boolean isShedding) {
        this.isShedding = isShedding;
    }

    public Boolean getIsSkinSensitive() {
        return isSkinSensitive;
    }

    public void setIsSkinSensitive(Boolean isSkinSensitive) {
        this.isSkinSensitive = isSkinSensitive;
    }

    public Boolean getIsStomachSensitive() {
        return isStomachSensitive;
    }

    public void setIsStomachSensitive(Boolean isStomachSensitive) {
        this.isStomachSensitive = isStomachSensitive;
    }

    public Boolean getHasDentalIssue() {
        return hasDentalIssue;
    }

    public void setHasDentalIssue(Boolean hasDentalIssue) {
        this.hasDentalIssue = hasDentalIssue;
    }

    public Boolean getHasJointIssue() {
        return hasJointIssue;
    }

    public void setHasJointIssue(Boolean hasJointIssue) {
        this.hasJointIssue = hasJointIssue;
    }

    public Boolean getHasTearStain() {
        return hasTearStain;
    }

    public void setHasTearStain(Boolean hasTearStain) {
        this.hasTearStain = hasTearStain;
    }

    public Boolean getIsOverweight() {
        return isOverweight;
    }

    public void setIsOverweight(Boolean isOverweight) {
        this.isOverweight = isOverweight;
    }

    public Boolean getIsPickyEater() {
        return isPickyEater;
    }

    public void setIsPickyEater(Boolean isPickyEater) {
        this.isPickyEater = isPickyEater;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getActivityLevelText() {
        return activityLevelText;
    }

    public void setActivityLevelText(String activityLevelText) {
        this.activityLevelText = activityLevelText;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public String getFoodPreferenceText() {
        return foodPreferenceText;
    }

    public void setFoodPreferenceText(String foodPreferenceText) {
        this.foodPreferenceText = foodPreferenceText;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
