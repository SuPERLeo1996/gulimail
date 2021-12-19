package com.leo.mall.product.service.impl;

import com.leo.mall.product.service.CategoryBrandRelationService;
import com.leo.mall.product.vo.Catelog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.common.utils.PageUtils;
import com.leo.common.utils.Query;

import com.leo.mall.product.dao.CategoryDao;
import com.leo.mall.product.entity.CategoryEntity;
import com.leo.mall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(null);

        List<CategoryEntity> level1Menus = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildren(menu, entities));
                    return menu;
                }).sorted((menu1, menu2) -> {
                    return menu1.getSort() - menu2.getSort();
                }).collect(Collectors.toList());

        return level1Menus;
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);
        return (Long[]) parentPath.toArray();
    }

    private List<Long> findParentPath(Long catelogId,List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findCatelogPath(byId.getParentCid());
        }
        return paths;
    }

    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Override
    public List<CategoryEntity> getLeve1Categorys() {

        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Override
    public Map<String, List<Catelog2VO>> getCatalogJson() {
        List<CategoryEntity> leve1Categorys = getLeve1Categorys();
        Map<String, List<Catelog2VO>> parent_cid = leve1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2VO> catelog2VOS = null;
            if (categoryEntities != null) {
                catelog2VOS = categoryEntities.stream().map(item -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    List<CategoryEntity> level3Catelog = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
                    if (level3Catelog != null) {
                        List<Catelog2VO.Catelog3VO> collect = level3Catelog.stream().map(e -> {
                            Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO(item.getCatId().toString(), e.getCatId().toString(), e.getName());
                            return catelog3VO;
                        }).collect(Collectors.toList());
                        catelog2VO.setCatalog3List(collect);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());
            }
            return catelog2VOS;
        }));
        return parent_cid;
    }
}