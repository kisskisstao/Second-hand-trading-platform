package com.example.Second_hand.trading.platform.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Second_hand.trading.platform.dto.PageResponse;
import com.example.Second_hand.trading.platform.entity.ItemEntity;
import com.example.Second_hand.trading.platform.entity.PurchaseEntity;
import com.example.Second_hand.trading.platform.mapper.ItemMapper;
import com.example.Second_hand.trading.platform.mapper.PurchaseMapper;

@Service
public class BazaarService {
	private static final int MAX_PAGE_SIZE = 100;

	private final JdbcTemplate jdbcTemplate;
	private final PurchaseMapper purchaseMapper;
	private final ItemMapper itemMapper;

	public BazaarService(JdbcTemplate jdbcTemplate, PurchaseMapper purchaseMapper, ItemMapper itemMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.purchaseMapper = purchaseMapper;
		this.itemMapper = itemMapper;
	}

	public PageResponse<Map<String, Object>> purchases(String keyword, Long categoryId, String campus,
			String status, int page, int pageSize) {
		LambdaQueryWrapper<PurchaseEntity> wrapper = Wrappers.lambdaQuery(PurchaseEntity.class)
				.eq(PurchaseEntity::getDeleted, 0)
				.eq(PurchaseEntity::getStatus, statusValue(status, "OPEN"));
		if (StringUtils.hasText(keyword)) {
			String value = keyword.trim();
			wrapper.and(query -> query.like(PurchaseEntity::getTitle, value)
					.or()
					.like(PurchaseEntity::getDescription, value));
		}
		if (categoryId != null) {
			wrapper.eq(PurchaseEntity::getCategoryId, categoryId);
		}
		if (StringUtils.hasText(campus)) {
			wrapper.eq(PurchaseEntity::getCampus, campus.trim());
		}
		wrapper.orderByDesc(PurchaseEntity::getCreatedAt);

		Page<PurchaseEntity> result = purchaseMapper.selectPage(Page.of(safePage(page), safePageSize(pageSize)), wrapper);
		return PageResponse.of(result.getRecords().stream().map(this::purchaseRow).toList(),
				(int) result.getCurrent(), (int) result.getSize(), result.getTotal());
	}

	@Transactional
	public Map<String, Object> createPurchase(Long userId, Map<String, Object> body) {
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in first");
		}
		Map<String, Object> data = safeBody(body);
		BigDecimal budgetMin = optionalMoney(data, "budgetMin", "minPrice");
		BigDecimal budgetMax = optionalMoney(data, "budgetMax", "maxPrice", "budget");
		if (budgetMin != null && budgetMax != null && budgetMin.compareTo(budgetMax) > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum budget cannot exceed maximum budget");
		}

		PurchaseEntity purchase = new PurchaseEntity();
		purchase.setUserId(userId);
		purchase.setTitle(requiredText(data, "title", "title"));
		purchase.setDescription(optionalText(data, "description", "desc", "message"));
		purchase.setCategoryId(categoryId(data, "categoryId", "category", "categoryName"));
		purchase.setCampus(requiredText(data, "campus", "campus"));
		purchase.setBudgetMin(budgetMin);
		purchase.setBudgetMax(budgetMax);
		purchase.setStatus("OPEN");
		purchase.setDeleted(0);
		purchaseMapper.insert(purchase);

		Map<String, Object> row = purchaseRow(purchase);
		row.put("recommendedItems", recommendForPurchase(purchase, 6));
		return row;
	}

	@Transactional
	public boolean closePurchase(Long userId, Integer purchaseId) {
		PurchaseEntity purchase = requireOwnedPurchase(userId, purchaseId);
		purchase.setStatus("CLOSED");
		purchaseMapper.updateById(purchase);
		return true;
	}

	public List<Map<String, Object>> purchaseMatches(Integer purchaseId) {
		PurchaseEntity purchase = requirePurchase(purchaseId);
		return recommendForPurchase(purchase, 10);
	}

	private List<Map<String, Object>> recommendForPurchase(PurchaseEntity purchase, int limit) {
		LambdaQueryWrapper<ItemEntity> wrapper = Wrappers.lambdaQuery(ItemEntity.class)
				.eq(ItemEntity::getDeleted, 0)
				.eq(ItemEntity::getStatus, "ON_SALE");
		if (purchase.getUserId() != null) {
			wrapper.ne(ItemEntity::getSellerId, purchase.getUserId());
		}
		if (purchase.getBudgetMax() != null) {
			wrapper.le(ItemEntity::getPrice, purchase.getBudgetMax());
		}
		wrapper.orderByDesc(ItemEntity::getCreatedAt).last("LIMIT 80");
		return itemMapper.selectList(wrapper).stream()
				.map(item -> scoredItem(item, purchaseScore(purchase, item), purchaseReasons(purchase, item)))
				.sorted(scoreComparator())
				.limit(limit)
				.toList();
	}

	private int purchaseScore(PurchaseEntity purchase, ItemEntity item) {
		int score = 0;
		if (purchase.getCategoryId() != null && purchase.getCategoryId().equals(item.getCategoryId())) {
			score += 30;
		}
		if (StringUtils.hasText(purchase.getCampus()) && purchase.getCampus().equals(item.getCampus())) {
			score += 20;
		}
		if (purchase.getBudgetMax() != null && item.getPrice() != null
				&& item.getPrice().compareTo(purchase.getBudgetMax()) <= 0) {
			score += 20;
		}
		if (purchase.getBudgetMin() != null && item.getPrice() != null
				&& item.getPrice().compareTo(purchase.getBudgetMin()) >= 0) {
			score += 5;
		}
		score += keywordScore(purchase.getTitle() + " " + purchase.getDescription(),
				item.getTitle() + " " + item.getDescription(), 30);
		return score;
	}

	private List<String> purchaseReasons(PurchaseEntity purchase, ItemEntity item) {
		List<String> reasons = new ArrayList<>();
		if (purchase.getCategoryId() != null && purchase.getCategoryId().equals(item.getCategoryId())) {
			reasons.add("分类一致");
		}
		if (StringUtils.hasText(purchase.getCampus()) && purchase.getCampus().equals(item.getCampus())) {
			reasons.add("校区一致");
		}
		if (purchase.getBudgetMax() != null && item.getPrice() != null
				&& item.getPrice().compareTo(purchase.getBudgetMax()) <= 0) {
			reasons.add("价格在预算内");
		}
		if (keywordScore(purchase.getTitle() + " " + purchase.getDescription(),
				item.getTitle() + " " + item.getDescription(), 30) > 0) {
			reasons.add("关键词相似");
		}
		return reasons;
	}

	private Map<String, Object> scoredItem(ItemEntity item, int score, List<String> reasons) {
		Map<String, Object> row = itemSummary(item);
		row.put("matchScore", score);
		row.put("matchReasons", reasons);
		return row;
	}

	private Comparator<Map<String, Object>> scoreComparator() {
		return Comparator.<Map<String, Object>, Integer>comparing(row -> (Integer) row.get("matchScore"))
				.reversed()
				.thenComparing(row -> String.valueOf(row.get("createdAt")), Comparator.reverseOrder());
	}

	private int keywordScore(String source, String target, int maxScore) {
		if (!StringUtils.hasText(source) || !StringUtils.hasText(target)) {
			return 0;
		}
		String normalizedTarget = target.toLowerCase(Locale.ROOT);
		int score = 0;
		for (String token : source.split("[\\s,，。；;、]+")) {
			String value = token.trim().toLowerCase(Locale.ROOT);
			if (value.length() >= 2 && normalizedTarget.contains(value)) {
				score += 10;
			}
			if (score >= maxScore) {
				return maxScore;
			}
		}
		return score;
	}

	private Map<String, Object> purchaseRow(PurchaseEntity purchase) {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("purchaseId", purchase.getId());
		row.put("userId", purchase.getUserId());
		row.put("title", purchase.getTitle());
		row.put("description", purchase.getDescription());
		row.put("categoryId", purchase.getCategoryId());
		row.put("categoryName", categoryName(purchase.getCategoryId()));
		row.put("campus", purchase.getCampus());
		row.put("budgetMin", purchase.getBudgetMin());
		row.put("budgetMax", purchase.getBudgetMax());
		row.put("status", purchase.getStatus());
		row.put("user", userRow(purchase.getUserId()));
		row.put("createdAt", timeString(purchase.getCreatedAt()));
		row.put("updatedAt", timeString(purchase.getUpdatedAt()));
		return row;
	}

	private Map<String, Object> itemSummary(ItemEntity item) {
		List<String> images = jdbcTemplate.queryForList("""
				SELECT image_url
				FROM item_images
				WHERE item_id = ?
				ORDER BY sort_order, id
				""", String.class, item.getId());
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("itemId", item.getId());
		row.put("title", item.getTitle());
		row.put("description", item.getDescription());
		row.put("categoryId", item.getCategoryId());
		row.put("categoryName", categoryName(item.getCategoryId()));
		row.put("price", item.getPrice());
		row.put("campus", item.getCampus());
		row.put("itemStatus", item.getStatus());
		row.put("coverUrl", images.isEmpty() ? "" : images.get(0));
		row.put("seller", userRow(item.getSellerId()));
		row.put("createdAt", timeString(item.getCreatedAt()));
		return row;
	}

	private PurchaseEntity requireOwnedPurchase(Long userId, Integer purchaseId) {
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in first");
		}
		PurchaseEntity purchase = requirePurchase(purchaseId);
		if (!userId.equals(purchase.getUserId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only your own wanted posts can be changed");
		}
		return purchase;
	}

	private PurchaseEntity requirePurchase(Integer purchaseId) {
		PurchaseEntity purchase = purchaseMapper.selectOne(Wrappers.lambdaQuery(PurchaseEntity.class)
				.eq(PurchaseEntity::getId, purchaseId)
				.eq(PurchaseEntity::getDeleted, 0)
				.last("LIMIT 1"));
		if (purchase == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wanted post does not exist");
		}
		return purchase;
	}

	private Map<String, Object> userRow(Long userId) {
		if (userId == null) {
			return Map.of();
		}
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
				SELECT id AS userId, nickname, avatar_url AS avatarUrl, campus
				FROM users
				WHERE id = ? AND deleted = 0
				LIMIT 1
				""", userId);
		if (rows.isEmpty()) {
			return Map.of("userId", userId, "nickname", "用户" + userId, "avatarUrl", "", "campus", "");
		}
		return rows.get(0);
	}

	private String categoryName(Long categoryId) {
		if (categoryId == null) {
			return "";
		}
		List<String> names = jdbcTemplate.queryForList("SELECT name FROM categories WHERE id = ? LIMIT 1",
				String.class, categoryId);
		return names.isEmpty() ? "" : names.get(0);
	}

	private Long categoryId(Map<String, Object> body, String... keys) {
		for (String key : keys) {
			Object value = body.get(key);
			Long id = optionalLong(value);
			if (id != null) {
				return id;
			}
			if (value != null && StringUtils.hasText(String.valueOf(value))) {
				List<Long> ids = jdbcTemplate.queryForList(
						"SELECT id FROM categories WHERE name = ? LIMIT 1", Long.class, String.valueOf(value).trim());
				if (ids.isEmpty()) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category does not exist");
				}
				return ids.get(0);
			}
		}
		return null;
	}

	private String statusValue(String status, String fallback) {
		if (!StringUtils.hasText(status)) {
			return fallback;
		}
		return status.trim().toUpperCase(Locale.ROOT);
	}

	private String requiredText(Map<String, Object> body, String key, String label) {
		String value = optionalText(body, key);
		if (!StringUtils.hasText(value)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in " + label);
		}
		return value;
	}

	private String optionalText(Map<String, Object> body, String... keys) {
		for (String key : keys) {
			Object value = body.get(key);
			if (value != null && StringUtils.hasText(String.valueOf(value))) {
				return String.valueOf(value).trim();
			}
		}
		return "";
	}

	private BigDecimal optionalMoney(Map<String, Object> body, String... keys) {
		for (String key : keys) {
			Object value = body.get(key);
			if (value == null || !StringUtils.hasText(String.valueOf(value))) {
				continue;
			}
			try {
				return new BigDecimal(String.valueOf(value).trim());
			} catch (NumberFormatException ex) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " has invalid format");
			}
		}
		return null;
	}

	private Long optionalLong(Object value) {
		if (value == null || !StringUtils.hasText(String.valueOf(value))) {
			return null;
		}
		if (value instanceof Number number) {
			return number.longValue();
		}
		try {
			return Long.valueOf(String.valueOf(value).trim());
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private String timeString(java.time.LocalDateTime time) {
		return time == null ? "" : time.toString();
	}

	private int safePage(int page) {
		return Math.max(1, page);
	}

	private int safePageSize(int pageSize) {
		return Math.max(1, Math.min(pageSize, MAX_PAGE_SIZE));
	}

	private Map<String, Object> safeBody(Map<String, Object> body) {
		return body == null ? Map.of() : body;
	}
}
