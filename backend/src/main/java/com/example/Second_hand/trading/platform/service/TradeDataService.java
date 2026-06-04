package com.example.Second_hand.trading.platform.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TradeDataService {
	private final JdbcTemplate jdbcTemplate;

	public TradeDataService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, Object> health() {
		return Map.of(
				"status", "UP",
				"service", "Second-hand-trading-platform",
				"database", "second_hand_trade",
				"timestamp", Instant.now().toString());
	}

	public List<Map<String, Object>> categories() {
		return jdbcTemplate.queryForList(
				"SELECT id AS categoryId, name FROM categories WHERE enabled = 1 ORDER BY sort_order, id");
	}

	public List<Map<String, Object>> items() {
		return jdbcTemplate.query("""
				SELECT i.*, c.name AS category_name,
				  (SELECT image_url FROM item_images img WHERE img.item_id = i.id ORDER BY sort_order, id LIMIT 1) AS cover_url
				FROM items i
				LEFT JOIN categories c ON c.id = i.category_id
				WHERE i.deleted = 0
				ORDER BY i.created_at DESC
				LIMIT 100
				""", (rs, rowNum) -> itemRow(rs));
	}

	public Map<String, Object> itemDetail(Integer itemId) {
		List<Map<String, Object>> rows = jdbcTemplate.query("""
				SELECT i.*, c.name AS category_name,
				  (SELECT image_url FROM item_images img WHERE img.item_id = i.id ORDER BY sort_order, id LIMIT 1) AS cover_url
				FROM items i
				LEFT JOIN categories c ON c.id = i.category_id
				WHERE i.id = ? AND i.deleted = 0
				""", (rs, rowNum) -> itemRow(rs), itemId);

		if (rows.isEmpty()) {
			return items().isEmpty() ? Map.of() : items().get(0);
		}

		return rows.get(0);
	}

	public Map<String, Object> currentUser() {
		List<Map<String, Object>> users = jdbcTemplate.queryForList("""
				SELECT id AS userId, student_no AS studentNo, nickname, phone, avatar_url AS avatarUrl,
				  campus, department, credit_score AS creditScore, created_at AS createdAt
				FROM users
				WHERE deleted = 0
				ORDER BY id
				LIMIT 1
				""");

		if (!users.isEmpty()) {
			return users.get(0);
		}

		return Map.of(
				"userId", 0,
				"studentNo", "",
				"nickname", "未创建普通用户",
				"phone", "",
				"avatarUrl", "",
				"campus", "",
				"department", "",
				"creditScore", 0);
	}

	public List<Map<String, Object>> users() {
		return jdbcTemplate.queryForList("""
				SELECT id AS userId, student_no AS studentNo, nickname, phone, status,
				  credit_score AS creditScore, created_at AS createdAt
				FROM users
				WHERE deleted = 0
				ORDER BY created_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> orders() {
		return jdbcTemplate.query("""
				SELECT o.*, i.title AS item_title,
				  (SELECT image_url FROM item_images img WHERE img.item_id = o.item_id ORDER BY sort_order, id LIMIT 1) AS cover_url
				FROM orders o
				LEFT JOIN items i ON i.id = o.item_id
				ORDER BY o.created_at DESC
				LIMIT 100
				""", (rs, rowNum) -> orderRow(rs));
	}

	public Map<String, Object> orderDetail(Integer orderId) {
		List<Map<String, Object>> rows = jdbcTemplate.query("""
				SELECT o.*, i.title AS item_title,
				  (SELECT image_url FROM item_images img WHERE img.item_id = o.item_id ORDER BY sort_order, id LIMIT 1) AS cover_url
				FROM orders o
				LEFT JOIN items i ON i.id = o.item_id
				WHERE o.id = ?
				""", (rs, rowNum) -> orderRow(rs), orderId);

		if (rows.isEmpty()) {
			return orders().isEmpty() ? Map.of() : orders().get(0);
		}

		return rows.get(0);
	}

	public List<Map<String, Object>> comments() {
		return jdbcTemplate.queryForList("""
				SELECT id AS commentId, content, user_id AS userId, parent_id AS parentId, created_at AS createdAt
				FROM item_comments
				WHERE deleted = 0
				ORDER BY created_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> wantedPosts() {
		return jdbcTemplate.queryForList("""
				SELECT w.id AS postId, w.user_id AS userId, w.title, w.description,
				  w.category_id AS categoryId, c.name AS categoryName, w.campus,
				  w.budget_min AS budgetMin, w.budget_max AS budgetMax,
				  w.status, w.created_at AS createdAt, w.updated_at AS updatedAt
				FROM wanted_posts w
				LEFT JOIN categories c ON c.id = w.category_id
				ORDER BY w.created_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> swapRequests() {
		return jdbcTemplate.queryForList("""
				SELECT s.id AS swapRequestId, s.request_no AS requestNo, s.requester_id AS requesterId,
				  s.target_item_id AS targetItemId, target_item.title AS targetItemTitle,
				  s.offered_item_id AS offeredItemId, offered_item.title AS offeredItemTitle,
				  s.owner_id AS ownerId, s.status, s.message, s.handled_at AS handledAt,
				  s.created_at AS createdAt, s.updated_at AS updatedAt
				FROM swap_requests s
				LEFT JOIN items target_item ON target_item.id = s.target_item_id
				LEFT JOIN items offered_item ON offered_item.id = s.offered_item_id
				ORDER BY s.created_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> chats() {
		return jdbcTemplate.queryForList("""
				SELECT id AS chatId, item_id AS itemId, buyer_id AS buyerId, seller_id AS sellerId,
				  last_message AS lastMessage, last_message_at AS lastMessageAt, updated_at AS updatedAt
				FROM chats
				ORDER BY updated_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> messages() {
		return jdbcTemplate.queryForList("""
				SELECT id AS messageId, chat_id AS chatId, sender_id AS senderId, message_type AS messageType,
				  content, image_url AS imageUrl, item_id AS itemId, filtered, read_at AS readAt, created_at AS createdAt
				FROM chat_messages
				ORDER BY created_at ASC
				LIMIT 100
				""");
	}

	public Map<String, Object> dashboard() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("totalUsers", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE deleted = 0", Long.class));
		data.put("todayNewUsers", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURRENT_DATE", Long.class));
		data.put("onSaleItems", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM items WHERE status = 'ON_SALE' AND deleted = 0", Long.class));
		data.put("todayAmount", jdbcTemplate.queryForObject("SELECT COALESCE(SUM(amount), 0) FROM orders WHERE DATE(created_at) = CURRENT_DATE", java.math.BigDecimal.class));
		data.put("activeUsers", jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT user_id) FROM notifications", Long.class));
		return data;
	}

	public List<Map<String, Object>> reports() {
		return jdbcTemplate.queryForList("""
				SELECT id AS reportId, reporter_id AS reporterId, target_type AS targetType, target_id AS targetId,
				  report_type AS reportType, content, status, handled_by AS handledBy, handled_at AS handledAt,
				  result_remark AS resultRemark, created_at AS createdAt
				FROM reports
				ORDER BY created_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> disputes() {
		return jdbcTemplate.queryForList("""
				SELECT id AS disputeId, dispute_no AS disputeNo, order_id AS orderId, applicant_id AS applicantId,
				  reason, evidence_urls AS evidenceUrls, status, handled_by AS handledBy, handled_at AS handledAt,
				  result_remark AS resultRemark, created_at AS createdAt, updated_at AS updatedAt
				FROM disputes
				ORDER BY created_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> notices() {
		return jdbcTemplate.queryForList("""
				SELECT id AS noticeId, title, content, scope_type AS scopeType, campus, popup_enabled AS popupEnabled,
				  status, published_at AS publishedAt, created_by AS createdBy, created_at AS createdAt, updated_at AS updatedAt
				FROM announcements
				ORDER BY created_at DESC
				LIMIT 100
				""");
	}

	public Map<String, Object> settings() {
		Map<String, Object> data = new LinkedHashMap<>();
		jdbcTemplate.queryForList("""
				SELECT setting_key AS settingKey, setting_value AS settingValue
				FROM system_settings
				ORDER BY id
				""").forEach(row -> data.put(String.valueOf(row.get("settingKey")), row.get("settingValue")));
		data.put("sensitiveWords", jdbcTemplate.queryForList(
				"SELECT word FROM sensitive_words WHERE enabled = 1 ORDER BY id", String.class));
		return data;
	}

	public String imageUrl() {
		String sql = "SELECT url FROM files WHERE file_type = 'IMAGE' ORDER BY id LIMIT 1";
		List<String> urls = jdbcTemplate.queryForList(sql, String.class);
		return urls.isEmpty() ? "" : urls.get(0);
	}

	private Map<String, Object> itemRow(ResultSet rs) throws SQLException {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("itemId", rs.getLong("id"));
		row.put("title", rs.getString("title"));
		row.put("description", rs.getString("description"));
		row.put("category", Map.of("categoryId", rs.getLong("category_id"), "name", rs.getString("category_name")));
		row.put("price", rs.getBigDecimal("price"));
		row.put("originalPrice", rs.getBigDecimal("original_price"));
		row.put("condition", rs.getString("condition_level"));
		row.put("itemStatus", rs.getString("status"));
		row.put("campus", rs.getString("campus"));
		row.put("tradePlace", rs.getString("trade_place"));
		row.put("swapSupported", rs.getBoolean("swap_supported"));
		row.put("coverUrl", rs.getString("cover_url"));
		row.put("imageUrls", rs.getString("cover_url") == null ? List.of() : List.of(rs.getString("cover_url")));
		row.put("seller", Map.of("userId", rs.getLong("seller_id"), "nickname", "用户" + rs.getLong("seller_id"), "avatarUrl", "", "campus", rs.getString("campus")));
		row.put("favoriteCount", rs.getInt("favorite_count"));
		row.put("viewCount", rs.getInt("view_count"));
		row.put("createdAt", rs.getTimestamp("created_at").toLocalDateTime().toString());
		row.put("updatedAt", rs.getTimestamp("updated_at").toLocalDateTime().toString());
		return row;
	}

	private Map<String, Object> orderRow(ResultSet rs) throws SQLException {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("orderId", rs.getLong("id"));
		row.put("orderNo", rs.getString("order_no"));
		row.put("orderStatus", rs.getString("status"));
		row.put("message", rs.getString("buyer_message"));
		row.put("tradeMode", rs.getString("trade_mode"));
		row.put("tradeCode", rs.getString("trade_code"));
		row.put("item", Map.of(
				"itemId", rs.getLong("item_id"),
				"title", rs.getString("item_title"),
				"price", rs.getBigDecimal("amount"),
				"coverUrl", rs.getString("cover_url") == null ? "" : rs.getString("cover_url")));
		row.put("buyer", Map.of("userId", rs.getLong("buyer_id"), "nickname", "用户" + rs.getLong("buyer_id")));
		row.put("seller", Map.of("userId", rs.getLong("seller_id"), "nickname", "用户" + rs.getLong("seller_id")));
		row.put("createdAt", rs.getTimestamp("created_at").toLocalDateTime().toString());
		row.put("updatedAt", rs.getTimestamp("updated_at").toLocalDateTime().toString());
		return row;
	}
}
