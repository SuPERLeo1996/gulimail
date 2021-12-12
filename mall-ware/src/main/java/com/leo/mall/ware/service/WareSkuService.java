package com.leo.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.common.to.SkuHasStockVO;
import com.leo.common.utils.PageUtils;
import com.leo.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author Leo
 * @email 
 * @date 2020-06-10 22:00:44
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVO> getSkusHasStock(List<Long> skuIds);
}

