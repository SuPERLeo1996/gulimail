package com.leo.mall.member.feign;

import com.leo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName CouponFeignService
 * @Description
 * @Author Leo
 * @Date 2020/10/10 14:20
 */

@FeignClient("mall-coupon")
public interface CouponFeignService {

//    @RequestMapping("/coupon/coupon/member/list")
//    public R memberCoupons();
}
