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
@RequestMapping("/api/admin")
public class AdminController {
	private final TradeDataService tradeDataService;

	public AdminController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@GetMapping("/dashboard")
	public ApiResponse<Map<String, Object>> dashboard() {
		return ApiResponse.success(tradeDataService.dashboard());
	}

	@GetMapping("/users")
	public ApiResponse<PageResponse<Map<String, Object>>> users() {
		return ApiResponse.success(PageResponse.of(tradeDataService.users(), 1, 10));
	}

	@PatchMapping("/users/{userId}/disable")
	public ApiResponse<Boolean> disableUser(@PathVariable Integer userId) {
		return ApiResponse.success(true);
	}

	@PatchMapping("/users/{userId}/enable")
	public ApiResponse<Boolean> enableUser(@PathVariable Integer userId) {
		return ApiResponse.success(true);
	}

	@GetMapping("/items")
	public ApiResponse<PageResponse<Map<String, Object>>> items() {
		return ApiResponse.success(PageResponse.of(tradeDataService.items(), 1, 10));
	}

	@PatchMapping("/items/{itemId}/remove")
	public ApiResponse<Boolean> removeItem(@PathVariable Integer itemId, @RequestBody(required = false) Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@DeleteMapping("/items/{itemId}")
	public ApiResponse<Boolean> deleteItem(@PathVariable Integer itemId) {
		return ApiResponse.success(true);
	}

	@GetMapping("/categories")
	public ApiResponse<List<Map<String, Object>>> categories() {
		return ApiResponse.success(tradeDataService.categories());
	}

	@PostMapping("/categories")
	public ApiResponse<Map<String, Object>> createCategory(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("categoryId", 7));
	}

	@PutMapping("/categories/{categoryId}")
	public ApiResponse<Boolean> updateCategory(@PathVariable Integer categoryId, @RequestBody Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@DeleteMapping("/categories/{categoryId}")
	public ApiResponse<Boolean> deleteCategory(@PathVariable Integer categoryId) {
		return ApiResponse.success(true);
	}

	@GetMapping("/orders")
	public ApiResponse<PageResponse<Map<String, Object>>> orders() {
		return ApiResponse.success(PageResponse.of(tradeDataService.orders(), 1, 10));
	}

	@GetMapping("/disputes")
	public ApiResponse<PageResponse<Map<String, Object>>> disputes() {
		return ApiResponse.success(PageResponse.of(tradeDataService.disputes(), 1, 10));
	}

	@PatchMapping("/disputes/{disputeId}/resolve")
	public ApiResponse<Boolean> resolveDispute(@PathVariable String disputeId, @RequestBody Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@GetMapping("/reports")
	public ApiResponse<PageResponse<Map<String, Object>>> reports() {
		return ApiResponse.success(PageResponse.of(tradeDataService.reports(), 1, 10));
	}

	@PatchMapping("/reports/{reportId}/approve")
	public ApiResponse<Boolean> approveReport(@PathVariable String reportId, @RequestBody(required = false) Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@PatchMapping("/reports/{reportId}/reject")
	public ApiResponse<Boolean> rejectReport(@PathVariable String reportId, @RequestBody(required = false) Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@GetMapping("/settings")
	public ApiResponse<Map<String, Object>> settings() {
		return ApiResponse.success(tradeDataService.settings());
	}

	@PutMapping("/settings")
	public ApiResponse<Boolean> updateSettings(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@GetMapping("/notices")
	public ApiResponse<PageResponse<Map<String, Object>>> notices() {
		return ApiResponse.success(PageResponse.of(tradeDataService.notices(), 1, 10));
	}

	@PostMapping("/notices")
	public ApiResponse<Map<String, Object>> createNotice(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("noticeId", 3));
	}

	@PutMapping("/notices/{noticeId}")
	public ApiResponse<Boolean> updateNotice(@PathVariable Integer noticeId, @RequestBody Map<String, Object> body) {
		return ApiResponse.success(true);
	}

	@DeleteMapping("/notices/{noticeId}")
	public ApiResponse<Boolean> deleteNotice(@PathVariable Integer noticeId) {
		return ApiResponse.success(true);
	}
}
