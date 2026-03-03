package com.yellow.petshop.mapper;

import com.yellow.petshop.model.pet.PetProfile;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 宠物档案Mapper
 */
@Mapper
public interface PetProfileMapper {
    
    /**
     * 获取用户的宠物档案列表
     */
    @Select("SELECT * FROM pet_profile WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<PetProfile> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据ID获取档案详情
     */
    @Select("SELECT * FROM pet_profile WHERE id = #{id} AND is_deleted = 0")
    PetProfile findById(@Param("id") Long id);
    
    /**
     * 添加宠物档案
     */
    @Insert("INSERT INTO pet_profile (user_id, pet_name, pet_type, age_stage, body_size, gender, avatar_url, " +
            "is_shedding, is_skin_sensitive, is_stomach_sensitive, has_dental_issue, has_joint_issue, " +
            "has_tear_stain, is_overweight, is_picky_eater, activity_level, food_preference, remark) " +
            "VALUES (#{userId}, #{petName}, #{petType}, #{ageStage}, #{bodySize}, #{gender}, #{avatarUrl}, " +
            "#{isShedding}, #{isSkinSensitive}, #{isStomachSensitive}, #{hasDentalIssue}, #{hasJointIssue}, " +
            "#{hasTearStain}, #{isOverweight}, #{isPickyEater}, #{activityLevel}, #{foodPreference}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PetProfile petProfile);
    
    /**
     * 更新宠物档案
     */
    @Update("UPDATE pet_profile SET pet_name = #{petName}, pet_type = #{petType}, age_stage = #{ageStage}, " +
            "body_size = #{bodySize}, gender = #{gender}, avatar_url = #{avatarUrl}, " +
            "is_shedding = #{isShedding}, is_skin_sensitive = #{isSkinSensitive}, " +
            "is_stomach_sensitive = #{isStomachSensitive}, has_dental_issue = #{hasDentalIssue}, " +
            "has_joint_issue = #{hasJointIssue}, has_tear_stain = #{hasTearStain}, " +
            "is_overweight = #{isOverweight}, is_picky_eater = #{isPickyEater}, " +
            "activity_level = #{activityLevel}, food_preference = #{foodPreference}, remark = #{remark} " +
            "WHERE id = #{id} AND user_id = #{userId} AND is_deleted = 0")
    int update(PetProfile petProfile);
    
    /**
     * 删除宠物档案（软删除）
     */
    @Update("UPDATE pet_profile SET is_deleted = 1 WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 检查档案是否属于该用户
     */
    @Select("SELECT COUNT(*) FROM pet_profile WHERE id = #{id} AND user_id = #{userId} AND is_deleted = 0")
    int checkOwnership(@Param("id") Long id, @Param("userId") Long userId);
}
