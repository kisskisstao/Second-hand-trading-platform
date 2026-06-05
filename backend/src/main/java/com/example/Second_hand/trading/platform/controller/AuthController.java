package com.example.Second_hand.trading.platform.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.service.AuthService;
import com.example.Second_hand.trading.platform.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;
	private final JwtService jwtService;

	public AuthController(AuthService authService, JwtService jwtService) {
		this.authService = authService;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("user", authService.registerUser(body)));
	}

	@PostMapping("/login")
	public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
		Map<String, Object> user = authService.loginUser(body);
		return ApiResponse.success(Map.of(
				"accessToken", jwtService.createUserToken(user.get("userId"), String.valueOf(user.get("studentNo"))),
				"user", user));
	}

	@PostMapping("/admin/login")
	public ApiResponse<Map<String, Object>> adminLogin(@RequestBody Map<String, Object> body) {
		Map<String, Object> admin = authService.loginAdmin(body);
		return ApiResponse.success(Map.of(
				"accessToken", jwtService.createAdminToken(
						admin.get("adminId"),
						String.valueOf(admin.get("username")),
						String.valueOf(admin.get("role"))),
				"admin", admin));
	}
}
