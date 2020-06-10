package com.leo.mall.order.dao;

import com.leo.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Leo
 * @email 
 * @date 2020-06-10 21:59:29
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
