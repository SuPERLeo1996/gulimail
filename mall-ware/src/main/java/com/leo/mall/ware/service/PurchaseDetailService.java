package com.leo.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.common.utils.PageUtils;
import com.leo.mall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Leo
 * @email 
 * @date 2020-06-10 22:00:44
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

