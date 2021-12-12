package com.leo.mall.product.feign;

import com.leo.common.to.SkuHasStockVO;
import com.leo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    R<List<SkuHasStockVO>> getSkusHasStock(@RequestBody List<Long> skuIds);
}
