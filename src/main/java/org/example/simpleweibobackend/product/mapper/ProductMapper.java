package org.example.simpleweibobackend.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.product.entity.Product;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
