package com.example.Second_hand.trading.platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.dto.PageResponse;
import com.example.Second_hand.trading.platform.service.TradeWorkflowService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
	private final TradeWorkflowService tradeWorkflowService;

	public ChatController(TradeWorkflowService tradeWorkflowService) {
		this.tradeWorkflowService = tradeWorkflowService;
	}

	@GetMapping
	public ApiResponse<List<Map<String, Object>>> list(@RequestAttribute("authId") Long authId) {
		return ApiResponse.success(tradeWorkflowService.chats(authId));
	}

	@PostMapping
	public ApiResponse<Map<String, Object>> createOrGet(
			@RequestAttribute("authId") Long authId,
			@RequestBody Map<String, Object> body) {
		return ApiResponse.success(tradeWorkflowService.createOrGetChat(authId, body));
	}

	@GetMapping("/{chatId}/messages")
	public ApiResponse<PageResponse<Map<String, Object>>> messages(
			@RequestAttribute("authId") Long authId,
			@PathVariable Integer chatId) {
		return ApiResponse.success(PageResponse.of(tradeWorkflowService.messages(authId, chatId), 1, 20));
	}

	@PostMapping("/{chatId}/messages")
	public ApiResponse<Map<String, Object>> send(
			@RequestAttribute("authId") Long authId,
			@PathVariable Integer chatId,
			@RequestBody Map<String, Object> body) {
		return ApiResponse.success(tradeWorkflowService.sendMessage(authId, chatId, body));
	}
}
