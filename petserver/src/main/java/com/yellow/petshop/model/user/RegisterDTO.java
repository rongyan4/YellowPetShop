package com.yellow.petshop.model.user;

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
public class RegisterDTO {
    private String username;
    private String password;
    private String email;
    private String nickname;
}
