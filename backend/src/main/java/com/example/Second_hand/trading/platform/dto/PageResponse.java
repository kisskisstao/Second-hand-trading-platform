package com.example.Second_hand.trading.platform.dto;

import java.util.List;

public record PageResponse<T>(List<T> list, int page, int pageSize, long total) {
	public static <T> PageResponse<T> of(List<T> list, int page, int pageSize) {
		return new PageResponse<>(list, page, pageSize, list.size());
	}
}
