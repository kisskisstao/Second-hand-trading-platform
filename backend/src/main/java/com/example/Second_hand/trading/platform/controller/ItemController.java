package com.example.Second_hand.trading.platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.dto.PageResponse;
import com.example.Second_hand.trading.platform.service.TradeDataService;

@RestController
@RequestMapping("/api/items")
public class ItemController {
	private final TradeDataService tradeDataService;

	public ItemController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@GetMapping
	public ApiResponse<PageResponse<Map<String, Object>>> list() {
		return ApiResponse.success(PageResponse.of(tradeDataService.items(), 1, 10));
	}

	@PostMapping
	public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("itemId", 1009));
	}

	@GetMapping("/{itemId}")
	public ApiResponse<Map<String, Object>> detail(@PathVariable Integer itemId) {
		return ApiResponse.success(tradeDataService.itemDetail(itemId));
	}

	@PutMapping("/{itemId}")
	public ApiResponse<Boolean> update(@PathVariable Integer itemId, @RequestBody Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@PatchMapping("/{itemId}/remove")
	public ApiResponse<Boolean> remove(@PathVariable Integer itemId) {
		return ApiResponse.success(true);
	}

	@PostMapping("/{itemId}/favorite")
	public ApiResponse<Boolean> favorite(@PathVariable Integer itemId) {
		return ApiResponse.success(true);
	}

	@DeleteMapping("/{itemId}/favorite")
	public ApiResponse<Boolean> unfavorite(@PathVariable Integer itemId) {
		return ApiResponse.success(true);
	}

	@GetMapping("/{itemId}/comments")
	public ApiResponse<List<Map<String, Object>>> comments(@PathVariable Integer itemId) {
		return ApiResponse.success(tradeDataService.comments());
	}

	@PostMapping("/{itemId}/comments")
	public ApiResponse<Map<String, Object>> createComment(@PathVariable Integer itemId,
			@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("commentId", 9003));
	}
}
