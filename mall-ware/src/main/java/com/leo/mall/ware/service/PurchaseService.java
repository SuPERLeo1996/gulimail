package com.leo.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.common.utils.PageUtils;
import com.leo.mall.ware.entity.PurchaseEntity;
import com.leo.mall.ware.vo.MergeVO;
import com.leo.mall.ware.vo.PurchaseDoneVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author Leo
 * @email 
 * @date 2020-06-10 22:00:44
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceiveList(Map<String, Object> params);

    void mergePurchase(MergeVO mergeVO);

    void receivedPurchase(List<Long> ids);

    void done(PurchaseDoneVO doneVO);
}

