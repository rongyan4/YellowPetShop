package com.yellow.petshop.model.pet;

import java.util.Date;

/**
 * 宠物档案实体类（商品推荐版）
 */
public class PetProfile {
    private Long id;
    private Long userId;
    
    // 基本信息（必填）
    private String petName;        // 宠物名称
    private String petType;        // 类型：cat/dog/other
    private String ageStage;       // 年龄段：puppy/adult/senior
    
    // 基本信息（可选）
    private String bodySize;       // 体型：small/medium/large
    private String gender;         // 性别：male/female
    private String avatarUrl;      // 头像URL
    
    // 体质特征（商品推荐核心字段）
    private Boolean isShedding;         // 易掉毛体质
    private Boolean isSkinSensitive;    // 皮肤敏感
    private Boolean isStomachSensitive; // 肠胃敏感
    private Boolean hasDentalIssue;     // 口腔问题
    private Boolean hasJointIssue;      // 关节问题
    private Boolean hasTearStain;       // 泪痕问题
    private Boolean isOverweight;       // 肥胖倾向
    private Boolean isPickyEater;       // 挑食
    
    // 生活习惯（可选）
    private String activityLevel;   // 活动量：low/medium/high
    private String foodPreference;  // 饮食偏好：dry/wet/mixed
    private String remark;          // 备注
    
    // 系统字段
    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
