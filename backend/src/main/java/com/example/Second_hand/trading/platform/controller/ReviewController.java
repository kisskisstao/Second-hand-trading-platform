package com.example.Second_hand.trading.platform.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.entity.ReviewEntity;
import com.example.Second_hand.trading.platform.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@PostMapping
	public ApiResponse<?> createReview(
			@RequestAttribute("authId") Long userId,
			@RequestBody Map<String, Object> request) {
		Long orderId = requiredLong(request.get("orderId"));
		Integer rating = requiredInteger(request.get("rating"));
		String content = (String) request.get("content");

		ReviewEntity review = reviewService.createReview(orderId, userId, rating, content);

		Map<String, Object> result = new HashMap<>();
		result.put("reviewId", review.getId());
		result.put("targetUserId", review.getTargetUserId());
		result.put("rating", review.getRating());
		result.put("content", review.getContent());
		result.put("createdAt", review.getCreatedAt());

		return ApiResponse.success(result);
	}

	@GetMapping("/user/{userId}")
	public ApiResponse<?> getUserReviews(@PathVariable Long userId) {
		List<ReviewEntity> reviews = reviewService.getReviewsByTargetUserId(userId);
		return ApiResponse.success(reviews);
	}

	@GetMapping("/user/{userId}/stats")
	public ApiResponse<?> getUserRatingStats(@PathVariable Long userId) {
		Map<String, Object> stats = reviewService.getUserRatingStats(userId);
		return ApiResponse.success(stats);
	}

	private Long requiredLong(Object value) {
		if (value instanceof Number number) {
			return number.longValue();
		}
		return Long.valueOf(String.valueOf(value));
	}

	private Integer requiredInteger(Object value) {
		if (value instanceof Number number) {
			return number.intValue();
		}
		return Integer.valueOf(String.valueOf(value));
	}
}
