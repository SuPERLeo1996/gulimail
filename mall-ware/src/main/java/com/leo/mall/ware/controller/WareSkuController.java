package com.leo.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.leo.common.to.SkuHasStockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.leo.mall.ware.entity.WareSkuEntity;
import com.leo.mall.ware.service.WareSkuService;
import com.leo.common.utils.PageUtils;
import com.leo.common.utils.R;



/**
 * 商品库存
 *
 * @author Leo
 * @email 
 * @date 2020-06-10 22:00:44
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/hasstock")
    public R<List<SkuHasStockVO>> getSkusHasStock(@RequestBody List<Long> skuIds) {
       List<SkuHasStockVO> vos = wareSkuService.getSkusHasStock(skuIds);
       R<List<SkuHasStockVO>> ok = R.ok();
       ok.setData(vos);
       return ok;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
