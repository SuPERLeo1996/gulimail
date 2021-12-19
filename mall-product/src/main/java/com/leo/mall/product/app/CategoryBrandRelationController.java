package com.leo.mall.product.app;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leo.common.utils.PageUtils;
import com.leo.common.utils.R;
import com.leo.mall.product.entity.BrandEntity;
import com.leo.mall.product.entity.CategoryBrandRelationEntity;
import com.leo.mall.product.service.CategoryBrandRelationService;
import com.leo.mall.product.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author leo
 * @email 
 * @date 2020-06-02 22:11:00
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
        return R.ok().put("data", data);
    }

    @GetMapping("/brands/list")
    public R relationBrandList(@RequestParam("catId") Long catId){

        List<BrandEntity> entities =  categoryBrandRelationService.getBrandsByCatId(catId);

        List<BrandVO> collect = entities.stream().map(e -> {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(e.getBrandId());
            brandVO.setBrandName(e.getName());
            return brandVO;
        }).collect(Collectors.toList());


        return R.ok().put("data", collect);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
