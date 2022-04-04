package com.leo.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2VO implements Serializable {
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
    public static class Catelog3VO implements Serializable {
        private String catalog2Id;
        private String id;
        private String name;
    }

}
