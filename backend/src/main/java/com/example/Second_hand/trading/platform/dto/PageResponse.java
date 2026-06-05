package com.example.Second_hand.trading.platform.dto;

import java.util.List;

public record PageResponse<T>(List<T> list, int page, int pageSize, long total) {
	public static <T> PageResponse<T> of(List<T> list, int page, int pageSize) {
		return new PageResponse<>(list, page, pageSize, list.size());
	}

	public static <T> PageResponse<T> of(List<T> list, int page, int pageSize, long total) {
		return new PageResponse<>(list, page, pageSize, total);
	}

	public static <T> PageResponse<T> page(List<T> source, int page, int pageSize) {
		int safePage = Math.max(1, page);
		int safePageSize = Math.max(1, Math.min(pageSize, 100));
		int start = Math.min((safePage - 1) * safePageSize, source.size());
		int end = Math.min(start + safePageSize, source.size());
		return new PageResponse<>(source.subList(start, end), safePage, safePageSize, source.size());
	}
}
