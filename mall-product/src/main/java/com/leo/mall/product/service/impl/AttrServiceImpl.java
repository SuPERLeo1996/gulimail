package com.leo.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.leo.common.constant.ProductConstant;
import com.leo.mall.product.dao.AttrAttrgroupRelationDao;
import com.leo.mall.product.dao.AttrGroupDao;
import com.leo.mall.product.dao.CategoryDao;
import com.leo.mall.product.entity.*;
import com.leo.mall.product.service.CategoryService;
import com.leo.mall.product.vo.AttrGroupRelationVO;
import com.leo.mall.product.vo.AttrRespVO;
import com.leo.mall.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.common.utils.PageUtils;
import com.leo.common.utils.Query;

import com.leo.mall.product.dao.AttrDao;
import com.leo.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);

        this.save(attrEntity);


        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type", "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVO> respVOS = records.stream().map(e -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(e, attrRespVO);

            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", e.getAttrId()));
                if (attrId != null && attrId.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }


            CategoryEntity categoryEntity = categoryDao.selectById(e.getCatelogId());
            if (categoryEntity != null) {
                attrRespVO.setCatelogName(categoryEntity.getName());
            }

            return attrRespVO;
        }).collect(Collectors.toList());
        pageUtils.setList(respVOS);
        return pageUtils;
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        AttrRespVO attrRespVO = new AttrRespVO();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVO);

        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (relationEntity != null) {
                attrRespVO.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }


        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVO.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            attrRespVO.setCatelogName(categoryEntity.getName());
        }

        return attrRespVO;
    }

    @Override
    @Transactional
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
         List<Long> attrIds = entities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
         if (CollectionUtils.isEmpty(attrIds)) {
             return null;
         }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);

        return (List<AttrEntity>) attrEntities;
    }


    @Override
    public void deleteRelation(AttrGroupRelationVO[] vos) {
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map(e -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(e, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId) {
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        List<AttrGroupEntity> groups = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        List<Long> collect = groups.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(collect)) {
            return null;
        }

        List<AttrAttrgroupRelationEntity> groupIds = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> attrIds = groupIds.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());


        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());

        if (!CollectionUtils.isEmpty(attrIds)) {
            wrapper.notIn("attr_id", attrIds);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(e -> {
                e.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }
}