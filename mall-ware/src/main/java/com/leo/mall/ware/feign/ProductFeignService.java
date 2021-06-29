package com.leo.mall.ware.feign;

import com.leo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-product")
public interface ProductFeignService {

    @RequestMapping("/product/spuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

}
