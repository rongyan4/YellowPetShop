package main.java.com.petshop.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("swipe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Swipe {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String imageUrl;
}
