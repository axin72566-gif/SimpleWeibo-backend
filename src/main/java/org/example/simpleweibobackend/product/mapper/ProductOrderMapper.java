package org.example.simpleweibobackend.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.product.entity.ProductOrder;

@Mapper
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {
}
