package org.example.simpleweibobackend.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.simpleweibobackend.product.entity.Product;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("""
            UPDATE product
            SET stock = stock - #{quantity}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{productId}
              AND status = #{status}
              AND stock >= #{quantity}
            """)
    int deductStock(@Param("productId") Long productId,
                    @Param("quantity") Integer quantity,
                    @Param("status") int status);

    @Update("""
            UPDATE product
            SET stock = stock + #{quantity}, updated_at = CURRENT_TIMESTAMP
            WHERE id = #{productId}
            """)
    int restoreStock(@Param("productId") Long productId,
                     @Param("quantity") Integer quantity);
}
