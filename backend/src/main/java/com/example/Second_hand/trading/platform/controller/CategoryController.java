package com.example.Second_hand.trading.platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.service.ItemService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	private final ItemService itemService;

	public CategoryController(ItemService itemService) {
		this.itemService = itemService;
	}

	@GetMapping
	public ApiResponse<List<Map<String, Object>>> list() {
		return ApiResponse.success(itemService.categories());
	}
}
