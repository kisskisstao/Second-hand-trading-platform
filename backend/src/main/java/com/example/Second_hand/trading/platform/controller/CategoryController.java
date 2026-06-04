package com.example.Second_hand.trading.platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.service.TradeDataService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	private final TradeDataService tradeDataService;

	public CategoryController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@GetMapping
	public ApiResponse<List<Map<String, Object>>> list() {
		return ApiResponse.success(tradeDataService.categories());
	}
}
