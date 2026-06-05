package com.example.Second_hand.trading.platform.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
	private static final DateTimeFormatter DAY_KEY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("MM-dd");

	private final JdbcTemplate jdbcTemplate;

	public AdminService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, Object> dashboard() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("totalUsers", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE deleted = 0", Long.class));
		data.put("todayNewUsers",
				jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURRENT_DATE", Long.class));
		data.put("totalItems",
				jdbcTemplate.queryForObject("SELECT COUNT(*) FROM items WHERE deleted = 0", Long.class));
		data.put("onSaleItems",
				jdbcTemplate.queryForObject("SELECT COUNT(*) FROM items WHERE status = 'ON_SALE' AND deleted = 0", Long.class));
		data.put("todayAmount", jdbcTemplate.queryForObject(
				"SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'PAID' AND DATE(paid_at) = CURRENT_DATE",
				BigDecimal.class));
		data.put("totalAmount", jdbcTemplate.queryForObject(
				"SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'PAID'",
				BigDecimal.class));
		data.put("activeUsers", jdbcTemplate.queryForObject("""
				SELECT COUNT(DISTINCT user_id)
				FROM (
				  SELECT id AS user_id FROM users WHERE deleted = 0 AND DATE(last_login_at) = CURRENT_DATE
				  UNION
				  SELECT buyer_id AS user_id FROM orders WHERE DATE(created_at) = CURRENT_DATE
				  UNION
				  SELECT seller_id AS user_id FROM orders WHERE DATE(created_at) = CURRENT_DATE
				  UNION
				  SELECT sender_id AS user_id FROM chat_messages WHERE DATE(created_at) = CURRENT_DATE
				) active_users
				""", Long.class));
		data.put("pendingVerifiedUsers", jdbcTemplate.queryForObject("""
				SELECT COUNT(*)
				FROM users
				WHERE deleted = 0 AND status = 'NORMAL' AND verified_status <> 'VERIFIED'
				""", Long.class));
		data.put("pendingReports", jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM reports WHERE status = 'PENDING'", Long.class));
		data.put("pendingDisputes", jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM disputes WHERE status = 'PENDING'", Long.class));
		data.put("pendingOrders", jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM orders WHERE status IN ('PENDING', 'ACCEPTED', 'PAYING')", Long.class));
		data.put("amountTrend", amountTrend());
		data.put("categoryDistribution", categoryDistribution());
		data.put("campusDistribution", campusDistribution());
		return data;
	}

	private List<Map<String, Object>> amountTrend() {
		Map<String, BigDecimal> amountByDate = new HashMap<>();
		jdbcTemplate.queryForList("""
				SELECT DATE_FORMAT(paid_at, '%Y-%m-%d') AS dayKey, COALESCE(SUM(amount), 0) AS amount
				FROM payments
				WHERE status = 'PAID'
				  AND paid_at >= DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY)
				GROUP BY DATE_FORMAT(paid_at, '%Y-%m-%d')
				ORDER BY dayKey
				""").forEach(row -> amountByDate.put(
						String.valueOf(row.get("dayKey")),
						(BigDecimal) row.get("amount")));

		return java.util.stream.IntStream.rangeClosed(0, 6)
				.mapToObj(index -> LocalDate.now().minusDays(6L - index))
				.map(day -> {
					Map<String, Object> row = new LinkedHashMap<>();
					row.put("date", day.format(DAY_LABEL));
					row.put("dayKey", day.format(DAY_KEY));
					row.put("amount", amountByDate.getOrDefault(day.format(DAY_KEY), BigDecimal.ZERO));
					return row;
				})
				.toList();
	}

	private List<Map<String, Object>> categoryDistribution() {
		return jdbcTemplate.queryForList("""
				SELECT c.id AS categoryId, c.name AS category, COUNT(i.id) AS count
				FROM categories c
				LEFT JOIN items i ON i.category_id = c.id AND i.deleted = 0
				WHERE c.enabled = 1
				GROUP BY c.id, c.name, c.sort_order
				ORDER BY c.sort_order, c.id
				""");
	}

	private List<Map<String, Object>> campusDistribution() {
		return jdbcTemplate.queryForList("""
				SELECT campus, COUNT(*) AS count
				FROM items
				WHERE deleted = 0 AND campus IS NOT NULL AND campus <> ''
				GROUP BY campus
				ORDER BY count DESC, campus
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

	public List<Map<String, Object>> notices() {
		return jdbcTemplate.queryForList("""
				SELECT id AS noticeId, title, content, scope_type AS scopeType, campus, popup_enabled AS popupEnabled,
				  status, published_at AS publishedAt, created_by AS createdBy, created_at AS createdAt, updated_at AS updatedAt
				FROM announcements
				ORDER BY created_at DESC
				LIMIT 100
				""");
	}
}
