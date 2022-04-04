package com.leo.mall.search.feign;

import com.leo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall_product")
public interface ProductFeignService {

    @GetMapping("product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);
}
