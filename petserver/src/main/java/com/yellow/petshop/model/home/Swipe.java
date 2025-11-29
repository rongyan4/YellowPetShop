package com.yellow.petshop.model.home;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("homeswipe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Swipe {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String imageUrl;
}
