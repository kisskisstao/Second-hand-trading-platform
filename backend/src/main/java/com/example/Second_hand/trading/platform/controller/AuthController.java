package com.example.Second_hand.trading.platform.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.service.TradeDataService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final TradeDataService tradeDataService;

	public AuthController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@PostMapping("/register")
	public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of(
				"userId", 1,
				"studentNo", body.getOrDefault("studentNo", "20240001"),
				"nickname", body.getOrDefault("nickname", body.getOrDefault("realName", "新用户"))));
	}

	@PostMapping("/login")
	public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of(
				"accessToken", "mock-user-token",
				"user", tradeDataService.currentUser()));
	}

	@PostMapping("/admin/login")
	public ApiResponse<Map<String, Object>> adminLogin(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of(
				"accessToken", "mock-admin-token",
				"admin", Map.of("username", body.getOrDefault("account", "admin"), "role", "ADMIN")));
	}
}
