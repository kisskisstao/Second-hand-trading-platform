package com.example.Second_hand.trading.platform.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
	private final JdbcTemplate jdbcTemplate;

	public AdminService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, Object> dashboard() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("totalUsers", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE deleted = 0", Long.class));
		data.put("todayNewUsers",
				jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURRENT_DATE", Long.class));
		data.put("onSaleItems",
				jdbcTemplate.queryForObject("SELECT COUNT(*) FROM items WHERE status = 'ON_SALE' AND deleted = 0", Long.class));
		data.put("todayAmount", jdbcTemplate.queryForObject(
				"SELECT COALESCE(SUM(amount), 0) FROM orders WHERE DATE(created_at) = CURRENT_DATE",
				java.math.BigDecimal.class));
		data.put("activeUsers", jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT user_id) FROM notifications", Long.class));
		return data;
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
