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
}
