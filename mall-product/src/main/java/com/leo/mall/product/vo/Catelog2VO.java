package com.leo.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2VO {
    /**
     * 1级父分类id
     */
    private String catalog1Id;
    /**
     * 三级子分类
     */
    private List<Catelog3VO> catalog3List;

    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3VO {
        private String catalog2Id;
        private String id;
        private String name;
    }

}
