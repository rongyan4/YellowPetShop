package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.home.Category;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 查询所有分类，按排序顺序
     * @return 分类列表
     */
    @Select("SELECT * FROM category ORDER BY sort_order ASC")
    List<Category> selectAllOrderBySort();
}
