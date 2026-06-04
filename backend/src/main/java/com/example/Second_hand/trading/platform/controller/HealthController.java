package com.example.Second_hand.trading.platform.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.service.TradeDataService;

@RestController
@RequestMapping("/api")
public class HealthController {
	private final TradeDataService tradeDataService;

	public HealthController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@GetMapping("/health")
	public ApiResponse<Map<String, Object>> health() {
		return ApiResponse.success(tradeDataService.health());
	}
}
