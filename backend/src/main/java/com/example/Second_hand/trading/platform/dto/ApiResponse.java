package com.example.Second_hand.trading.platform.dto;

public record ApiResponse<T>(int code, String message, T data) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(0, "success", data);
	}
}
