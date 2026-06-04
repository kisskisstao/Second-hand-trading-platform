package com.example.Second_hand.trading.platform.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.dto.PageResponse;
import com.example.Second_hand.trading.platform.service.TradeDataService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final TradeDataService tradeDataService;

	public UserController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@GetMapping("/me")
	public ApiResponse<Map<String, Object>> me() {
		return ApiResponse.success(tradeDataService.currentUser());
	}

	@PutMapping("/me")
	public ApiResponse<Boolean> updateMe(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@GetMapping("/me/items")
	public ApiResponse<PageResponse<Map<String, Object>>> myItems() {
		return ApiResponse.success(PageResponse.of(tradeDataService.items(), 1, 10));
	}

	@GetMapping("/me/favorites")
	public ApiResponse<PageResponse<Map<String, Object>>> myFavorites() {
		var items = tradeDataService.items();
		return ApiResponse.success(PageResponse.of(items.subList(0, Math.min(2, items.size())), 1, 10));
	}

	@GetMapping("/{userId}/reviews")
	public ApiResponse<PageResponse<Map<String, Object>>> reviews(@PathVariable Integer userId) {
		return ApiResponse.success(PageResponse.of(
				List.of(Map.of("reviewId", 8001, "rating", 5, "content", "交易顺利，描述准确",
						"reviewer", Map.of("userId", 2, "nickname", "李同学"), "createdAt",
						LocalDateTime.now().toString())),
				1, 10));
	}
}
