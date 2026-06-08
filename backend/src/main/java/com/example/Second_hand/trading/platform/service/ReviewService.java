package com.example.Second_hand.trading.platform.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.Second_hand.trading.platform.entity.OrderEntity;
import com.example.Second_hand.trading.platform.entity.ReviewEntity;
import com.example.Second_hand.trading.platform.mapper.OrderMapper;
import com.example.Second_hand.trading.platform.mapper.ReviewMapper;

@Service
public class ReviewService {

	@Autowired
	private ReviewMapper reviewMapper;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private UserService userService;

	@Transactional
	public ReviewEntity createReview(Long orderId, Long reviewerId, Integer rating, String content) {
		if (orderId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单 ID 不能为空");
		}
		if (reviewerId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
		}
		if (rating == null || rating < 1 || rating > 5) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评分必须是 1-5 星");
		}

		OrderEntity order = orderMapper.selectById(orderId);
		if (order == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在");
		}

		if (!"COMPLETED".equals(order.getStatus())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "只有已完成的订单才能评价");
		}
		if (!order.getBuyerId().equals(reviewerId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有买家可以评价该订单");
		}

		List<ReviewEntity> existingReviews = reviewMapper.findByOrderId(orderId);
		for (ReviewEntity review : existingReviews) {
			if (review.getReviewerId().equals(reviewerId)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "该订单已经评价过");
			}
		}

		Long targetUserId = order.getSellerId();
		ReviewEntity review = new ReviewEntity();
		review.setOrderId(orderId);
		review.setReviewerId(reviewerId);
		review.setTargetUserId(targetUserId);
		review.setRating(rating);
		review.setContent(normalizeContent(content));
		reviewMapper.insert(review);

		updateCreditScore(targetUserId, rating);

		return review;
	}

	private void updateCreditScore(Long userId, Integer rating) {
		int scoreChange = 0;
		if (rating >= 4) {
			scoreChange = 5;
		} else if (rating == 3) {
			scoreChange = 0;
		} else {
			scoreChange = -10;
		}
		if (scoreChange != 0) {
			userService.updateCreditScore(userId, scoreChange);
		}
	}

	public List<ReviewEntity> getReviewsByTargetUserId(Long targetUserId) {
		return reviewMapper.findByTargetUserId(targetUserId);
	}

	public ReviewEntity getReviewById(Long id) {
		return reviewMapper.selectById(id);
	}

	public Map<String, Object> getUserRatingStats(Long userId) {
		Map<String, Object> stats = new HashMap<>();
		stats.put("averageRating", reviewMapper.getAverageRating(userId));
		stats.put("reviewCount", reviewMapper.getReviewCount(userId));
		return stats;
	}

	private String normalizeContent(String content) {
		if (!StringUtils.hasText(content)) {
			return "";
		}
		String normalized = content.trim();
		if (normalized.length() > 500) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评价内容不能超过 500 字");
		}
		return normalized;
	}
}
