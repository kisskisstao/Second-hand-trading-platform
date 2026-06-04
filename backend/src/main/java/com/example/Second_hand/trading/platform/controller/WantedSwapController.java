package com.example.Second_hand.trading.platform.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.dto.PageResponse;
import com.example.Second_hand.trading.platform.service.TradeDataService;

@RestController
@RequestMapping("/api")
public class WantedSwapController {
	private final TradeDataService tradeDataService;

	public WantedSwapController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@GetMapping("/wanted-posts")
	public ApiResponse<PageResponse<Map<String, Object>>> wantedPosts() {
		return ApiResponse.success(PageResponse.of(tradeDataService.wantedPosts(), 1, 10));
	}

	@PostMapping("/wanted-posts")
	public ApiResponse<Map<String, Object>> createWantedPost(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("postId", 11));
	}

	@PatchMapping("/wanted-posts/{postId}/close")
	public ApiResponse<Boolean> closeWantedPost(@PathVariable Integer postId) {
		return ApiResponse.success(true);
	}

	@GetMapping("/swap-requests")
	public ApiResponse<PageResponse<Map<String, Object>>> swapRequests() {
		return ApiResponse.success(PageResponse.of(tradeDataService.swapRequests(), 1, 10));
	}

	@PostMapping("/swap-requests")
	public ApiResponse<Map<String, Object>> createSwapRequest(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("swapRequestId", 11));
	}

	@PatchMapping("/swap-requests/{requestId}/accept")
	public ApiResponse<Boolean> acceptSwapRequest(@PathVariable Integer requestId) {
		return ApiResponse.success(true);
	}

	@PatchMapping("/swap-requests/{requestId}/reject")
	public ApiResponse<Boolean> rejectSwapRequest(@PathVariable Integer requestId, @RequestBody(required = false) Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@PatchMapping("/swap-requests/{requestId}/cancel")
	public ApiResponse<Boolean> cancelSwapRequest(@PathVariable Integer requestId) {
		return ApiResponse.success(true);
	}
}
