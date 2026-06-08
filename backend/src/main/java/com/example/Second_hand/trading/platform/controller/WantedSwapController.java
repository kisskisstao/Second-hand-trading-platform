package com.example.Second_hand.trading.platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.dto.PageResponse;
import com.example.Second_hand.trading.platform.service.BazaarService;

@RestController
@RequestMapping("/api")
public class WantedSwapController {
	private final BazaarService bazaarService;

	public WantedSwapController(BazaarService bazaarService) {
		this.bazaarService = bazaarService;
	}

	@GetMapping("/purchases")
	public ApiResponse<PageResponse<Map<String, Object>>> purchases(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String campus,
			@RequestParam(required = false) String status,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int pageSize) {
		return ApiResponse.success(bazaarService.purchases(keyword, categoryId, campus, status, page, pageSize));
	}

	@PostMapping("/purchases")
	public ApiResponse<Map<String, Object>> createPurchase(
			@RequestAttribute("authId") Long authId,
			@RequestBody Map<String, Object> body) {
		return ApiResponse.success(bazaarService.createPurchase(authId, body));
	}

	@PatchMapping("/purchases/{purchaseId}/close")
	public ApiResponse<Boolean> closePurchase(
			@RequestAttribute("authId") Long authId,
			@PathVariable Integer purchaseId) {
		return ApiResponse.success(bazaarService.closePurchase(authId, purchaseId));
	}

	@GetMapping("/purchases/{purchaseId}/matches")
	public ApiResponse<List<Map<String, Object>>> purchaseMatches(@PathVariable Integer purchaseId) {
		return ApiResponse.success(bazaarService.purchaseMatches(purchaseId));
	}

	@GetMapping("/wanted-posts")
	public ApiResponse<PageResponse<Map<String, Object>>> wantedPosts(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String campus,
			@RequestParam(required = false) String status,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int pageSize) {
		return ApiResponse.success(bazaarService.purchases(keyword, categoryId, campus, status, page, pageSize));
	}

	@PostMapping("/wanted-posts")
	public ApiResponse<Map<String, Object>> createWantedPost(
			@RequestAttribute("authId") Long authId,
			@RequestBody Map<String, Object> body) {
		Map<String, Object> row = bazaarService.createPurchase(authId, body);
		row.put("postId", row.get("purchaseId"));
		return ApiResponse.success(row);
	}

	@PatchMapping("/wanted-posts/{postId}/close")
	public ApiResponse<Boolean> closeWantedPost(
			@RequestAttribute("authId") Long authId,
			@PathVariable Integer postId) {
		return ApiResponse.success(bazaarService.closePurchase(authId, postId));
	}

}
