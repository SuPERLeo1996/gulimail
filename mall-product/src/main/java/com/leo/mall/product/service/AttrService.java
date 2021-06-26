package com.leo.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.common.utils.PageUtils;
import com.leo.mall.product.entity.AttrEntity;
import com.leo.mall.product.vo.AttrRespVO;
import com.leo.mall.product.vo.AttrVO;

import java.util.Map;

/**
 * 商品属性
 *
 * @author leo
 * @email 
 * @date 2020-06-02 22:11:01
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVO attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVO getAttrInfo(Long attrId);

    void updateAttr(AttrVO attr);
}

