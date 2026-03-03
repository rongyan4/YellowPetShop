package com.yellow.petshop.model.pet;

/**
 * 宠物档案DTO（前端提交数据）
 */
public class PetProfileDTO {
    private Long id;  // 更新时需要
    
    // 基本信息
    private String petName;
    private String petType;
    private String ageStage;
    private String bodySize;
    private String gender;
    private String avatarUrl;
    
    // 体质特征
    private Boolean isShedding;
    private Boolean isSkinSensitive;
    private Boolean isStomachSensitive;
    private Boolean hasDentalIssue;
    private Boolean hasJointIssue;
    private Boolean hasTearStain;
    private Boolean isOverweight;
    private Boolean isPickyEater;
    
    // 生活习惯
    private String activityLevel;
    private String foodPreference;
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

    public String getAgeStage() {
        return ageStage;
    }

    public void setAgeStage(String ageStage) {
        this.ageStage = ageStage;
    }

    public String getBodySize() {
        return bodySize;
    }

    public void setBodySize(String bodySize) {
        this.bodySize = bodySize;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
