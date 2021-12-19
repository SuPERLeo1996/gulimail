package com.leo.mall.product.app;

import com.leo.common.utils.PageUtils;
import com.leo.common.utils.R;
import com.leo.common.valid.AddGroup;
import com.leo.common.valid.UpdateGroup;
import com.leo.common.valid.UpdateStatusGroup;
import com.leo.mall.product.entity.BrandEntity;
import com.leo.mall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 品牌
 *
 * @author leo
 * @email 
 * @date 2020-06-02 22:11:01
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
//        if (bindingResult.hasErrors()) {
//            Map<String,String> map = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(item -> {
//                String message = item.getDefaultMessage();
//                String field = item.getField();
//                map.put(field,message);
//            });
//            R.error(400,"提交的数据不合法").put("data",map);
//        } else {
//
//        }
        brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
