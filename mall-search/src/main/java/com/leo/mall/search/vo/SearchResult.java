package com.leo.mall.search.vo;

import com.leo.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    private List<SkuEsModel> products;
    private Integer pageNum;
    private Long total;
    private Integer totalPages;

    private List<BrandVO> brands;
    private List<AttrVO> attrs;
    private List<CatalogVO> catalogs;

    private List<Integer> pageNavs;


    /* 面包屑导航数据 */
    private List<NavVo> navs;

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVO {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVO {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVO {
        private Long catalogId;
        private String catalogName;
    }
}
