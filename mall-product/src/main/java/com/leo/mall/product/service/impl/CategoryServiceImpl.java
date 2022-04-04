package com.leo.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.leo.mall.product.service.CategoryBrandRelationService;
import com.leo.mall.product.vo.Catelog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.common.utils.PageUtils;
import com.leo.common.utils.Query;

import com.leo.mall.product.dao.CategoryDao;
import com.leo.mall.product.entity.CategoryEntity;
import com.leo.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;


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
    @Transactional
    @CacheEvict(value = {"category"},key = "'getLeve1Categorys'")
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Cacheable(value = {"category"},key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLeve1Categorys() {

        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Cacheable(value = {"category"},key = "#root.method.name")
    @Override
    public Map<String, List<Catelog2VO>> getCatalogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> leve1Categorys = getParentCid(selectList,0L);
        Map<String, List<Catelog2VO>> parent_cid = leve1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParentCid(selectList,v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (categoryEntities != null) {
                catelog2VOS = categoryEntities.stream().map(item -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    List<CategoryEntity> level3Catelog = getParentCid(selectList,item.getCatId());
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

    public Map<String, List<Catelog2VO>> getCatalogJson2() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            Map<String, List<Catelog2VO>> catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
        }
        Map<String, List<Catelog2VO>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2VO>>>(){});
        return result;

    }

    public Map<String, List<Catelog2VO>> getCatalogJsonFromDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式成功。。");
            Map<String, List<Catelog2VO>> dataFromDb = null;

            try {
                dataFromDb = getDataFromDb();
            } finally {
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Collections.singletonList("lock"), uuid);
            }


            return dataFromDb;
        } else {
            System.out.println("获取锁失败。。等待重试");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }

    }

    private Map<String, List<Catelog2VO>> getDataFromDb() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> leve1Categorys = getParentCid(selectList,0L);
        Map<String, List<Catelog2VO>> parent_cid = leve1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParentCid(selectList,v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (categoryEntities != null) {
                catelog2VOS = categoryEntities.stream().map(item -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    List<CategoryEntity> level3Catelog = getParentCid(selectList,item.getCatId());
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

        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJson",s,1, TimeUnit.DAYS);
        return parent_cid;
    }


    public Map<String, List<Catelog2VO>> getCatalogJsonFromDb() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> leve1Categorys = getParentCid(selectList,0L);
        Map<String, List<Catelog2VO>> parent_cid = leve1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParentCid(selectList,v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (categoryEntities != null) {
                catelog2VOS = categoryEntities.stream().map(item -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    List<CategoryEntity> level3Catelog = getParentCid(selectList,item.getCatId());
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

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        return selectList.stream().filter(item -> item.getParentCid() == parentCid).collect(Collectors.toList());
    }
}