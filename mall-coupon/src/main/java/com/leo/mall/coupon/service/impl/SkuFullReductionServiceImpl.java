package com.leo.mall.coupon.service.impl;

import com.leo.common.to.MemberPrice;
import com.leo.common.to.SkuReductionTO;
import com.leo.mall.coupon.entity.MemberPriceEntity;
import com.leo.mall.coupon.entity.SkuLadderEntity;
import com.leo.mall.coupon.service.MemberPriceService;
import com.leo.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.common.utils.PageUtils;
import com.leo.common.utils.Query;

import com.leo.mall.coupon.dao.SkuFullReductionDao;
import com.leo.mall.coupon.entity.SkuFullReductionEntity;
import com.leo.mall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTO skuReductionTO) {
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTO.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTO.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTO.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTO.getCountStatus());
        if (skuReductionTO.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }



        SkuFullReductionEntity fullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTO,fullReductionEntity);
        if (fullReductionEntity.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            this.save(fullReductionEntity);
        }

        List<MemberPrice> memberPrice = skuReductionTO.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(e -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTO.getSkuId());
            memberPriceEntity.setMemberLevelId(e.getId());
            memberPriceEntity.setMemberLevelName(e.getName());
            memberPriceEntity.setMemberPrice(e.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(e-> e.getMemberPrice().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);


    }

}