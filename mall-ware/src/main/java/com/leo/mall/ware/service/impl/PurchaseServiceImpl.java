package com.leo.mall.ware.service.impl;

import com.leo.common.constant.WareConstant;
import com.leo.mall.ware.entity.PurchaseDetailEntity;
import com.leo.mall.ware.service.PurchaseDetailService;
import com.leo.mall.ware.service.WareSkuService;
import com.leo.mall.ware.vo.MergeVO;
import com.leo.mall.ware.vo.PurchaseDoneVO;
import com.leo.mall.ware.vo.PurchaseItemDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.common.utils.PageUtils;
import com.leo.common.utils.Query;

import com.leo.mall.ware.dao.PurchaseDao;
import com.leo.mall.ware.entity.PurchaseEntity;
import com.leo.mall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<PurchaseEntity>();
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceiveList(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<PurchaseEntity>();
        wrapper.eq("status", 0).or().eq("status", 1);

        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchase(MergeVO mergeVO) {
        Long purchaseId = mergeVO.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();

            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);

            purchaseId = purchaseEntity.getId();
        }


        List<Long> items = mergeVO.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(e -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(e);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void receivedPurchase(List<Long> ids) {
        List<PurchaseEntity> collect = ids.stream()
                .map(this::getById)
                .filter(e -> e.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || e.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .map(e -> {
                    e.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    e.setUpdateTime(new Date());
                    return e;
                })
                .collect(Collectors.toList());

        this.updateBatchById(collect);

        collect.forEach(e -> {
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(e.getId());
            List<PurchaseDetailEntity> collect1 = entities.stream().map(item -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(item.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);

        });

    }

    @Override
    @Transactional
    public void done(PurchaseDoneVO doneVO) {
        Long id = doneVO.getId();

        Boolean flag = true;
        List<PurchaseItemDoneVO> items = doneVO.getItems();

        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVO item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();

            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                detailEntity.setStatus(item.getStatus());
            } else {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());

                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }

        purchaseDetailService.updateBatchById(updates);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);



    }
}