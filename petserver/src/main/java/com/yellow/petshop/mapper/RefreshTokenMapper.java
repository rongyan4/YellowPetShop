package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.token.RefreshToken;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {

    /**
     * 根据 token 哈希查询记录
     */
    @Select("SELECT * FROM refresh_token WHERE token_hash = #{tokenHash} LIMIT 1")
    RefreshToken findByTokenHash(@Param("tokenHash") String tokenHash);

    /**
     * 吊销某用户的所有 RT（登录时清理旧token，防止无限累积）
     */
    @Update("UPDATE refresh_token SET revoked = 1 WHERE user_id = #{userId} AND user_type = #{userType} AND revoked = 0")
    void revokeAllByUser(@Param("userId") Long userId, @Param("userType") String userType);

    /**
     * 吊销单条 RT
     */
    @Update("UPDATE refresh_token SET revoked = 1 WHERE token_hash = #{tokenHash}")
    void revokeByTokenHash(@Param("tokenHash") String tokenHash);
}
