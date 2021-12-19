package com.leo.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.common.utils.PageUtils;
import com.leo.mall.product.entity.CategoryEntity;
import com.leo.mall.product.vo.Catelog2VO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author leo
 * @email 
 * @date 2020-06-02 22:11:01
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLeve1Categorys();

    Map<String, List<Catelog2VO>> getCatalogJson();
}

