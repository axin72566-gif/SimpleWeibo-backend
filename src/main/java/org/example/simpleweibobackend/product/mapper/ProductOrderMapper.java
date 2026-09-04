package org.example.simpleweibobackend.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.simpleweibobackend.product.entity.ProductOrder;

import java.time.LocalDateTime;

@Mapper
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {

    @Update("""
            UPDATE product_order
            SET status = #{paidStatus}, paid_time = #{paidTime}
            WHERE id = #{orderId}
              AND user_id = #{userId}
              AND status = #{pendingStatus}
              AND expire_time > #{paidTime}
            """)
    int payPendingOrder(@Param("orderId") Long orderId,
                        @Param("userId") Long userId,
                        @Param("pendingStatus") int pendingStatus,
                        @Param("paidStatus") int paidStatus,
                        @Param("paidTime") LocalDateTime paidTime);

    @Update("""
            UPDATE product_order
            SET status = #{closedStatus}, closed_time = #{closedTime}
            WHERE id = #{orderId}
              AND status = #{pendingStatus}
              AND expire_time <= #{closedTime}
            """)
    int closeExpiredOrder(@Param("orderId") Long orderId,
                          @Param("pendingStatus") int pendingStatus,
                          @Param("closedStatus") int closedStatus,
                          @Param("closedTime") LocalDateTime closedTime);
}
