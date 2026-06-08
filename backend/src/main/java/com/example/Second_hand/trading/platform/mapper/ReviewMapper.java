package com.example.Second_hand.trading.platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Second_hand.trading.platform.entity.ReviewEntity;

@Mapper
public interface ReviewMapper extends BaseMapper<ReviewEntity> {
	@Select("""
			SELECT id, order_id, reviewer_id, target_user_id, rating, content, created_at
			FROM reviews
			WHERE target_user_id = #{targetUserId}
			ORDER BY created_at DESC
			""")
	List<ReviewEntity> findByTargetUserId(@Param("targetUserId") Long targetUserId);

	@Select("""
			SELECT id, order_id, reviewer_id, target_user_id, rating, content, created_at
			FROM reviews
			WHERE order_id = #{orderId}
			""")
	List<ReviewEntity> findByOrderId(@Param("orderId") Long orderId);

	@Select("""
			SELECT COALESCE(AVG(rating), 0)
			FROM reviews
			WHERE target_user_id = #{targetUserId}
			""")
	Double getAverageRating(@Param("targetUserId") Long targetUserId);

	@Select("""
			SELECT COUNT(*)
			FROM reviews
			WHERE target_user_id = #{targetUserId}
			""")
	Integer getReviewCount(@Param("targetUserId") Long targetUserId);
}
