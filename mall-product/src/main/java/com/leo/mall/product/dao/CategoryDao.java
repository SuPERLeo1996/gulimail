package com.leo.mall.product.dao;

import com.leo.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author leo
 * @email 
 * @date 2020-06-02 22:11:01
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
