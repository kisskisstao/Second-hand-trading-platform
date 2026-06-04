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
@RequestMapping("/api/orders")
public class OrderController {
	private final TradeDataService tradeDataService;

	public OrderController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@PostMapping
	public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("orderId", 5004, "orderStatus", "PENDING"));
	}

	@GetMapping
	public ApiResponse<PageResponse<Map<String, Object>>> list() {
		return ApiResponse.success(PageResponse.of(tradeDataService.orders(), 1, 10));
	}

	@GetMapping("/{orderId}")
	public ApiResponse<Map<String, Object>> detail(@PathVariable Integer orderId) {
		return ApiResponse.success(tradeDataService.orderDetail(orderId));
	}

	@PatchMapping("/{orderId}/accept")
	public ApiResponse<Boolean> accept(@PathVariable Integer orderId) {
		return ApiResponse.success(true);
	}

	@PatchMapping("/{orderId}/cancel")
	public ApiResponse<Boolean> cancel(@PathVariable Integer orderId, @RequestBody(required = false) Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@PatchMapping("/{orderId}/complete")
	public ApiResponse<Boolean> complete(@PathVariable Integer orderId) {
		return ApiResponse.success(true);
	}

	@PostMapping("/{orderId}/reviews")
	public ApiResponse<Map<String, Object>> review(@PathVariable Integer orderId, @RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("reviewId", 8002));
	}
}
