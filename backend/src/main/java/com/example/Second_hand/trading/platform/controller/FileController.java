package com.example.Second_hand.trading.platform.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.service.TradeDataService;

@RestController
@RequestMapping("/api/files")
public class FileController {
	private final TradeDataService tradeDataService;

	public FileController(TradeDataService tradeDataService) {
		this.tradeDataService = tradeDataService;
	}

	@PostMapping("/images")
	public ApiResponse<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
		return ApiResponse.success(Map.of(
				"filename", file.getOriginalFilename() == null ? "image.png" : file.getOriginalFilename(),
				"url", tradeDataService.imageUrl()));
	}
}
