package com.leo.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.common.utils.PageUtils;
import com.leo.mall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author Leo
 * @email 
 * @date 2020-06-10 21:59:29
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

