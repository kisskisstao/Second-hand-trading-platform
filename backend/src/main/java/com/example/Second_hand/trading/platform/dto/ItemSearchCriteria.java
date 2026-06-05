package com.example.Second_hand.trading.platform.dto;

import java.math.BigDecimal;

public record ItemSearchCriteria(
		String keyword,
		Long categoryId,
		String categories,
		String conditions,
		String campus,
		BigDecimal minPrice,
		BigDecimal maxPrice,
		String sort,
		int page,
		int pageSize) {
}
