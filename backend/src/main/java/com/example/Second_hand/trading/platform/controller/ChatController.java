package com.example.Second_hand.trading.platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.dto.PageResponse;
import com.example.Second_hand.trading.platform.service.TradeDataService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
	private final TradeDataService tradeDataService;

	public ChatController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@GetMapping
	public ApiResponse<List<Map<String, Object>>> list() {
		return ApiResponse.success(tradeDataService.chats());
	}

	@GetMapping("/{chatId}/messages")
	public ApiResponse<PageResponse<Map<String, Object>>> messages(@PathVariable Integer chatId) {
		return ApiResponse.success(PageResponse.of(tradeDataService.messages(), 1, 20));
	}

	@PostMapping("/{chatId}/messages")
	public ApiResponse<Map<String, Object>> send(@PathVariable Integer chatId, @RequestBody Map<String, Object> body) {
		return ApiResponse.success(Map.of("messageId", 7003));
	}
}
