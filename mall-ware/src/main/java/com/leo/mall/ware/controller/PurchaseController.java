package com.leo.mall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.leo.mall.ware.vo.MergeVO;
import com.leo.mall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.leo.mall.ware.entity.PurchaseEntity;
import com.leo.mall.ware.service.PurchaseService;
import com.leo.common.utils.PageUtils;
import com.leo.common.utils.R;



/**
 * 采购信息
 *
 * @author Leo
 * @email 
 * @date 2020-06-10 22:00:44
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/done")
    public R finish(@RequestBody PurchaseDoneVO doneVO){
        purchaseService.done(doneVO);
        return R.ok();
    }

    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.receivedPurchase(ids);
        return R.ok();
    }

    @PostMapping("/merge")
    public R merge(@RequestBody MergeVO mergeVO){
        purchaseService.mergePurchase(mergeVO);
        return R.ok();
    }

    @RequestMapping("/unreceive/list")
    public R unReceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceiveList(params);
        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setUpdateTime(new Date());
        purchase.setCreateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
