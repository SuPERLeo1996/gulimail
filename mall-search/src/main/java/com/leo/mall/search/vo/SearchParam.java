package com.leo.mall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {

    /**
     * 关键词
     */
    private String keyword;
    /**
     * 三级分类
     */
    private Long catalog3Id;

    /**
     * 排序条件
     * saleCount_asc/desc
     * skuPrice_asc/desc
     * hotScore_asc/desc
     */
    private String sort;

    /**
     * 是否有货
     */
    private Integer hasStock;

    /**
     * 价格区间
     */
    private String skuPrice;

    /**
     * 按照品牌id查询
     */
    private List<Long> brandId;
    /**
     * 按照属性查询
     */
    private List<String> attrs;

    private Integer pageNum = 1;

    private String _queryString;

}
