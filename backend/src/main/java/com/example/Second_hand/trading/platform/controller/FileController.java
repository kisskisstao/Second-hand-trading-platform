package com.example.Second_hand.trading.platform.controller;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Second_hand.trading.platform.dto.ApiResponse;
import com.example.Second_hand.trading.platform.service.FileStorageService;

@RestController
@RequestMapping("/api/files")
public class FileController {
	private final FileStorageService fileStorageService;

	public FileController(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	@PostMapping("/images")
	public ApiResponse<Map<String, Object>> uploadImage(
			@RequestAttribute("authId") Long authId,
			@RequestParam("file") MultipartFile file) {
		return ApiResponse.success(fileStorageService.storeImage(authId, file));
	}

	@GetMapping("/images/{storageKey:.+}")
	public ResponseEntity<Resource> image(@PathVariable String storageKey) {
		return fileStorageService.loadImage(storageKey);
	}
}
