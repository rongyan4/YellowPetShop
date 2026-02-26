package com.yellow.petshop.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String gender;
    private String avatar;
    private String status;
    private String role;
    private String birthday;
    /**
     * 注册时间 / 加入时间（用于前端展示会员天数）
     */
    private String createTime;
    /**
     * 当前总积分
     */
    private Integer points;
    /**
     * 当前会员等级（例如：S1、S2）
     */
    private String level;
    /**
     * 距离下一等级已累计的积分（当前进度分子）
     */
    private Integer currentPoints;
    /**
     * 升级到下一等级所需的总积分（当前进度分母）
     */
    private Integer nextLevelPoints;
    /**
     * 下一等级标识（例如：S2、S3），如果已是最高等级则可能为 null
     */
    private String nextLevel;
}
