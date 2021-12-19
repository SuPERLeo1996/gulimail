package com.leo.mall.product.app;

import com.leo.common.utils.PageUtils;
import com.leo.common.utils.R;
import com.leo.mall.product.entity.AttrEntity;
import com.leo.mall.product.entity.AttrGroupEntity;
import com.leo.mall.product.service.AttrAttrgroupRelationService;
import com.leo.mall.product.service.AttrGroupService;
import com.leo.mall.product.service.AttrService;
import com.leo.mall.product.service.CategoryService;
import com.leo.mall.product.vo.AttrGroupRelationVO;
import com.leo.mall.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author leo
 * @email 
 * @date 2020-06-02 22:11:01
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @GetMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrGroupId") Long attrGroupId) {
        List<AttrEntity> entities = attrService.getRelationAttr(attrGroupId);
        return R.ok().put("data",entities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@PathVariable("catelogId") Long catelogId,@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVO[] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }

    @GetMapping("/{attrGroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrGroupId") Long attrGroupId,
                            @RequestParam Map<String,Object> params){
        PageUtils page = attrService.getNoRelationAttr(params,attrGroupId);
        return R.ok().put("page",page);
    }

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVO> vos){
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {
        List<AttrGroupWithAttrsVO> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",vos);

    }

}
