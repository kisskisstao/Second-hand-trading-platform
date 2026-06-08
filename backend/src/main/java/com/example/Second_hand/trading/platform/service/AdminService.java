package com.example.Second_hand.trading.platform.service;

import java.math.BigDecimal;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

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
				SELECT d.id AS disputeId, d.dispute_no AS disputeNo, d.order_id AS orderId,
				  d.applicant_id AS applicantId, applicant.nickname AS applicantName,
				  d.reason, d.evidence_urls AS evidenceUrls, d.status, d.handled_by AS handledBy,
				  d.handled_at AS handledAt, d.result_remark AS resultRemark,
				  o.amount, o.buyer_id AS buyerId, buyer.nickname AS buyerName,
				  o.seller_id AS sellerId, seller.nickname AS sellerName,
				  i.title AS itemTitle, d.created_at AS createdAt, d.updated_at AS updatedAt
				FROM disputes d
				LEFT JOIN orders o ON o.id = d.order_id
				LEFT JOIN items i ON i.id = o.item_id
				LEFT JOIN users applicant ON applicant.id = d.applicant_id
				LEFT JOIN users buyer ON buyer.id = o.buyer_id
				LEFT JOIN users seller ON seller.id = o.seller_id
				ORDER BY d.created_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> reports() {
		return jdbcTemplate.queryForList("""
				SELECT r.id AS reportId, r.reporter_id AS reporterId, reporter.nickname AS reporterName,
				  r.target_type AS targetType, r.target_id AS targetId, r.report_type AS reportType,
				  r.content, r.status, r.handled_by AS handledBy, r.handled_at AS handledAt,
				  r.result_remark AS resultRemark,
				  CASE
				    WHEN r.target_type = 'ITEM' THEN item.title
				    WHEN r.target_type = 'USER' THEN target_user.nickname
				    ELSE CAST(r.target_id AS CHAR)
				  END AS targetName,
				  r.created_at AS createdAt
				FROM reports r
				LEFT JOIN users reporter ON reporter.id = r.reporter_id
				LEFT JOIN items item ON item.id = r.target_id AND r.target_type = 'ITEM'
				LEFT JOIN users target_user ON target_user.id = r.target_id AND r.target_type = 'USER'
				ORDER BY r.created_at DESC
				LIMIT 100
				""");
	}

	public List<Map<String, Object>> categories() {
		return jdbcTemplate.queryForList("""
				SELECT c.id AS categoryId, c.name, c.sort_order AS sortOrder, c.enabled,
				  COUNT(DISTINCT i.id) AS productCount,
				  COALESCE(GROUP_CONCAT(t.name ORDER BY t.sort_order, t.id SEPARATOR ','), '') AS tags
				FROM categories c
				LEFT JOIN items i ON i.category_id = c.id AND i.deleted = 0
				LEFT JOIN category_tags t ON t.category_id = c.id
				WHERE c.enabled = 1
				GROUP BY c.id, c.name, c.sort_order, c.enabled
				ORDER BY c.sort_order, c.id
				""");
	}

	@Transactional
	public Map<String, Object> createCategory(Map<String, Object> body) {
		String name = requiredText(body, "name", "鍒嗙被鍚嶇О");
		Integer sortOrder = intValue(body.get("sortOrder"), nextCategorySortOrder());
		jdbcTemplate.update("""
				INSERT INTO categories (name, sort_order, enabled)
				VALUES (?, ?, 1)
				ON DUPLICATE KEY UPDATE sort_order = VALUES(sort_order), enabled = 1
				""", name, sortOrder);
		Long categoryId = jdbcTemplate.queryForObject("SELECT id FROM categories WHERE name = ? LIMIT 1", Long.class, name);
		replaceCategoryTags(categoryId, tags(body));
		return category(categoryId);
	}

	@Transactional
	public boolean updateCategory(Integer categoryId, Map<String, Object> body) {
		requireCategory(categoryId);
		String name = requiredText(body, "name", "鍒嗙被鍚嶇О");
		Integer sortOrder = intValue(body.get("sortOrder"), categoryId);
		int updated = jdbcTemplate.update("""
				UPDATE categories
				SET name = ?, sort_order = ?, enabled = 1
				WHERE id = ?
				""", name, sortOrder, categoryId);
		if (updated == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "鍒嗙被涓嶅瓨鍦?");
		}
		replaceCategoryTags(categoryId.longValue(), tags(body));
		return true;
	}

	@Transactional
	public boolean deleteCategory(Integer categoryId) {
		requireCategory(categoryId);
		Long itemCount = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM items WHERE category_id = ? AND deleted = 0", Long.class, categoryId);
		if (itemCount != null && itemCount > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "鍒嗙被涓嬭繕鏈夊晢鍝侊紝涓嶈兘鍒犻櫎");
		}
		jdbcTemplate.update("UPDATE categories SET enabled = 0 WHERE id = ?", categoryId);
		jdbcTemplate.update("DELETE FROM category_tags WHERE category_id = ?", categoryId);
		return true;
	}

	@Transactional
	public boolean disableUser(Integer userId) {
		int updated = jdbcTemplate.update("""
				UPDATE users
				SET status = 'DISABLED'
				WHERE id = ? AND deleted = 0
				""", userId);
		if (updated == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "鐢ㄦ埛涓嶅瓨鍦?");
		}
		jdbcTemplate.update("UPDATE items SET status = 'REMOVED' WHERE seller_id = ? AND status = 'ON_SALE'", userId);
		return true;
	}

	@Transactional
	public boolean enableUser(Integer userId) {
		int updated = jdbcTemplate.update("""
				UPDATE users
				SET status = 'NORMAL'
				WHERE id = ? AND deleted = 0
				""", userId);
		if (updated == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "鐢ㄦ埛涓嶅瓨鍦?");
		}
		return true;
	}

	@Transactional
	public boolean resolveDispute(Long adminId, Integer disputeId, Map<String, Object> body) {
		String result = optionalText(body, "result");
		String remark = firstText(optionalText(body, "remark"), optionalText(body, "reason"));
		String status = result.toUpperCase().contains("REJECT") || result.contains("驳回") ? "REJECTED" : "RESOLVED";
		int updated = jdbcTemplate.update("""
				UPDATE disputes
				SET status = ?, handled_by = ?, handled_at = CURRENT_TIMESTAMP, result_remark = ?
				WHERE id = ?
				""", status, adminId, remark, disputeId);
		if (updated == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "绾犵悍涓嶅瓨鍦?");
		}
		return true;
	}

	@Transactional
	public boolean approveReport(Long adminId, Integer reportId, Map<String, Object> body) {
		Map<String, Object> report = report(reportId);
		String remark = firstText(optionalText(body, "remark"), optionalText(body, "reason"));
		jdbcTemplate.update("""
				UPDATE reports
				SET status = 'APPROVED', handled_by = ?, handled_at = CURRENT_TIMESTAMP, result_remark = ?
				WHERE id = ?
				""", adminId, remark, reportId);
		applyReportPenalty(report, remark);
		return true;
	}

	@Transactional
	public boolean rejectReport(Long adminId, Integer reportId, Map<String, Object> body) {
		report(reportId);
		String remark = firstText(optionalText(body, "remark"), optionalText(body, "reason"));
		jdbcTemplate.update("""
				UPDATE reports
				SET status = 'REJECTED', handled_by = ?, handled_at = CURRENT_TIMESTAMP, result_remark = ?
				WHERE id = ?
				""", adminId, remark, reportId);
		return true;
	}

	@Transactional
	public boolean updateSettings(Long adminId, Map<String, Object> body) {
		upsertSetting("trade_rules", jsonObject(Map.of(
				"maxImages", intValue(body.get("maxImages"), 9),
				"disputeDays", intValue(body.get("disputeDays"), 3),
				"creditDeduction", intValue(body.get("creditDeduction"), 10),
				"tradeTip", optionalText(body, "tradeTip"))), "骞冲彴浜ゆ槗瑙勫垯", adminId);
		upsertSetting("payment_wechat", jsonObject(Map.of("appId", optionalText(body, "wechatAppId"))), "寰俊鏀粯閰嶇疆", adminId);
		upsertSetting("payment_alipay", jsonObject(Map.of("appId", optionalText(body, "alipayAppId"))), "鏀粯瀹濇敮浠橀厤缃?", adminId);
		upsertSetting("payment_campus_card", jsonObject(Map.of("merchant", optionalText(body, "campusCardMerchant"))), "鏍″洯鍗℃敮浠橀厤缃?", adminId);

		if (body.get("sensitiveWords") instanceof List<?> list) {
			jdbcTemplate.update("UPDATE sensitive_words SET enabled = 0");
			int sort = 0;
			for (Object wordValue : list) {
				if (wordValue == null || !StringUtils.hasText(String.valueOf(wordValue))) {
					continue;
				}
				String word = String.valueOf(wordValue).trim();
				jdbcTemplate.update("""
						INSERT INTO sensitive_words (word, enabled, created_by)
						VALUES (?, 1, ?)
						ON DUPLICATE KEY UPDATE enabled = 1, created_by = VALUES(created_by)
						""", word, adminId == null ? 1L : adminId);
				sort++;
			}
		}
		return true;
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

	@Transactional
	public Map<String, Object> createNotice(Long adminId, Map<String, Object> body) {
		if (adminId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录管理员账号");
		}
		NoticePayload payload = noticePayload(body);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			var statement = connection.prepareStatement("""
					INSERT INTO announcements (
					  title, content, scope_type, campus, popup_enabled, status, published_at, created_by
					) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
					""", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, payload.title());
			statement.setString(2, payload.content());
			statement.setString(3, payload.scopeType());
			statement.setString(4, payload.campus());
			statement.setInt(5, payload.popupEnabled() ? 1 : 0);
			statement.setString(6, payload.status());
			statement.setObject(7, payload.publishedAt());
			statement.setLong(8, adminId);
			return statement;
		}, keyHolder);
		Number key = keyHolder.getKey();
		if (key == null) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "公告创建失败");
		}
		if ("PUBLISHED".equals(payload.status())) {
			notifyAnnouncementUsers(key.longValue(), payload);
		}
		return notice(key.longValue());
	}

	@Transactional
	public boolean updateNotice(Integer noticeId, Map<String, Object> body) {
		String oldStatus = noticeStatusById(noticeId);
		NoticePayload payload = noticePayload(body);
		int updated = jdbcTemplate.update("""
				UPDATE announcements
				SET title = ?, content = ?, scope_type = ?, campus = ?, popup_enabled = ?, status = ?, published_at = ?
				WHERE id = ?
				""",
				payload.title(), payload.content(), payload.scopeType(), payload.campus(),
				payload.popupEnabled() ? 1 : 0, payload.status(), payload.publishedAt(), noticeId);
		if (updated == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公告不存在");
		}
		if (!"PUBLISHED".equals(oldStatus) && "PUBLISHED".equals(payload.status())) {
			notifyAnnouncementUsers(noticeId.longValue(), payload);
		}
		return true;
	}

	@Transactional
	public boolean deleteNotice(Integer noticeId) {
		int deleted = jdbcTemplate.update("DELETE FROM announcements WHERE id = ?", noticeId);
		if (deleted == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公告不存在");
		}
		return true;
	}

	private Map<String, Object> notice(Long noticeId) {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
				SELECT id AS noticeId, title, content, scope_type AS scopeType, campus, popup_enabled AS popupEnabled,
				  status, published_at AS publishedAt, created_by AS createdBy, created_at AS createdAt, updated_at AS updatedAt
				FROM announcements
				WHERE id = ?
				LIMIT 1
				""", noticeId);
		if (rows.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公告不存在");
		}
		return rows.get(0);
	}

	private String noticeStatusById(Integer noticeId) {
		List<String> statuses = jdbcTemplate.queryForList(
				"SELECT status FROM announcements WHERE id = ? LIMIT 1", String.class, noticeId);
		if (statuses.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公告不存在");
		}
		return statuses.get(0);
	}

	private void notifyAnnouncementUsers(Long noticeId, NoticePayload payload) {
		String targetSql = "ALL".equals(payload.scopeType())
				? "SELECT id FROM users WHERE deleted = 0 AND status = 'NORMAL'"
				: "SELECT id FROM users WHERE deleted = 0 AND status = 'NORMAL' AND campus = ?";
		List<Long> userIds = "ALL".equals(payload.scopeType())
				? jdbcTemplate.queryForList(targetSql, Long.class)
				: jdbcTemplate.queryForList(targetSql, Long.class, payload.campus());
		for (Long userId : userIds) {
			jdbcTemplate.update("""
					INSERT INTO notifications (user_id, type, title, content)
					VALUES (?, 'SYSTEM', ?, ?)
					""", userId, truncate("平台公告：" + payload.title(), 150),
					truncate(payload.content(), 1000));
		}
	}

	private String truncate(String value, int maxLength) {
		if (value == null || value.length() <= maxLength) {
			return value;
		}
		return value.substring(0, maxLength);
	}

	private NoticePayload noticePayload(Map<String, Object> body) {
		String title = requiredText(body, "title", "公告标题");
		String content = requiredText(body, "content", "公告内容");
		String status = noticeStatus(optionalText(body, "status"));
		String scope = optionalText(body, "scope");
		String scopeType = optionalText(body, "scopeType");
		String campus = optionalText(body, "campus");

		if (!StringUtils.hasText(scopeType)) {
			scopeType = StringUtils.hasText(scope) && !"全平台".equals(scope) ? "CAMPUS" : "ALL";
		}
		scopeType = scopeType.toUpperCase();
		if ("ALL".equals(scopeType)) {
			campus = null;
		} else if (!StringUtils.hasText(campus)) {
			campus = scope;
		}
		if (!"ALL".equals(scopeType) && !StringUtils.hasText(campus)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请选择公告推送校区");
		}

		boolean popupEnabled = boolValue(body.get("popupEnabled"));
		LocalDateTime publishedAt = "PUBLISHED".equals(status) ? LocalDateTime.now() : null;
		return new NoticePayload(title, content, scopeType, campus, popupEnabled, status, publishedAt);
	}

	private String noticeStatus(String value) {
		if (!StringUtils.hasText(value)) {
			return "DRAFT";
		}
		String text = value.trim().toUpperCase();
		return text.contains("PUBLISH") || value.contains("发布") || value.contains("已发布") ? "PUBLISHED" : "DRAFT";
	}

	private String requiredText(Map<String, Object> body, String key, String label) {
		String value = optionalText(body, key);
		if (!StringUtils.hasText(value)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请填写" + label);
		}
		return value.trim();
	}

	private String optionalText(Map<String, Object> body, String key) {
		if (body == null) {
			return "";
		}
		Object value = body.get(key);
		return value == null || !StringUtils.hasText(String.valueOf(value)) ? "" : String.valueOf(value).trim();
	}

	private String firstText(String first, String second) {
		return StringUtils.hasText(first) ? first : (StringUtils.hasText(second) ? second : "");
	}

	private Integer intValue(Object value, Integer fallback) {
		if (value == null || !StringUtils.hasText(String.valueOf(value))) {
			return fallback;
		}
		if (value instanceof Number number) {
			return number.intValue();
		}
		try {
			return Integer.valueOf(String.valueOf(value).trim());
		} catch (NumberFormatException ex) {
			return fallback;
		}
	}

	private Integer nextCategorySortOrder() {
		Integer value = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(sort_order), 0) + 1 FROM categories", Integer.class);
		return value == null ? 1 : value;
	}

	private void requireCategory(Integer categoryId) {
		Long count = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM categories WHERE id = ? AND enabled = 1", Long.class, categoryId);
		if (count == null || count == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category does not exist");
		}
	}

	private Map<String, Object> category(Long categoryId) {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
				SELECT c.id AS categoryId, c.name, c.sort_order AS sortOrder, c.enabled,
				  COUNT(DISTINCT i.id) AS productCount,
				  COALESCE(GROUP_CONCAT(t.name ORDER BY t.sort_order, t.id SEPARATOR ','), '') AS tags
				FROM categories c
				LEFT JOIN items i ON i.category_id = c.id AND i.deleted = 0
				LEFT JOIN category_tags t ON t.category_id = c.id
				WHERE c.id = ? AND c.enabled = 1
				GROUP BY c.id, c.name, c.sort_order, c.enabled
				LIMIT 1
				""", categoryId);
		if (rows.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category does not exist");
		}
		return rows.get(0);
	}

	private List<String> tags(Map<String, Object> body) {
		Object value = body == null ? null : body.get("tags");
		if (value instanceof List<?> list) {
			return list.stream()
					.filter(item -> item != null && StringUtils.hasText(String.valueOf(item)))
					.map(item -> String.valueOf(item).trim())
					.distinct()
					.toList();
		}
		if (value == null || !StringUtils.hasText(String.valueOf(value))) {
			return List.of();
		}
		return List.of(String.valueOf(value).split("[,，]")).stream()
				.map(String::trim)
				.filter(StringUtils::hasText)
				.distinct()
				.toList();
	}

	private void replaceCategoryTags(Long categoryId, List<String> tags) {
		jdbcTemplate.update("DELETE FROM category_tags WHERE category_id = ?", categoryId);
		int sortOrder = 1;
		for (String tag : tags) {
			jdbcTemplate.update("""
					INSERT INTO category_tags (category_id, name, sort_order)
					VALUES (?, ?, ?)
					""", categoryId, tag, sortOrder++);
		}
	}

	private Map<String, Object> report(Integer reportId) {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
				SELECT id AS reportId, target_type AS targetType, target_id AS targetId, reporter_id AS reporterId
				FROM reports
				WHERE id = ?
				LIMIT 1
				""", reportId);
		if (rows.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report does not exist");
		}
		return rows.get(0);
	}

	private void applyReportPenalty(Map<String, Object> report, String remark) {
		String targetType = String.valueOf(report.get("targetType"));
		Long targetId = ((Number) report.get("targetId")).longValue();
		if ("ITEM".equalsIgnoreCase(targetType)) {
			jdbcTemplate.update("UPDATE items SET status = 'REMOVED' WHERE id = ? AND deleted = 0", targetId);
			List<Long> sellerIds = jdbcTemplate.queryForList("SELECT seller_id FROM items WHERE id = ? LIMIT 1", Long.class, targetId);
			if (!sellerIds.isEmpty()) {
				jdbcTemplate.update("UPDATE users SET credit_score = GREATEST(credit_score - 10, 0) WHERE id = ?", sellerIds.get(0));
			}
		} else if ("USER".equalsIgnoreCase(targetType)) {
			jdbcTemplate.update("UPDATE users SET credit_score = GREATEST(credit_score - 10, 0) WHERE id = ?", targetId);
			if (StringUtils.hasText(remark) && remark.toUpperCase().contains("DISABLE")) {
				jdbcTemplate.update("UPDATE users SET status = 'DISABLED' WHERE id = ?", targetId);
			}
		}
	}

	private void upsertSetting(String key, String value, String description, Long adminId) {
		jdbcTemplate.update("""
				INSERT INTO system_settings (setting_key, setting_value, description, updated_by)
				VALUES (?, ?, ?, ?)
				ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value),
				  description = VALUES(description), updated_by = VALUES(updated_by)
				""", key, value, description, adminId == null ? 1L : adminId);
	}

	private String jsonObject(Map<String, Object> values) {
		return values.entrySet().stream()
				.map(entry -> "\"" + escapeJson(entry.getKey()) + "\":" + jsonValue(entry.getValue()))
				.collect(java.util.stream.Collectors.joining(",", "{", "}"));
	}

	private String jsonValue(Object value) {
		if (value instanceof Number || value instanceof Boolean) {
			return String.valueOf(value);
		}
		return "\"" + escapeJson(value == null ? "" : String.valueOf(value)) + "\"";
	}

	private String escapeJson(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private boolean boolValue(Object value) {
		return Boolean.TRUE.equals(value) || "true".equalsIgnoreCase(String.valueOf(value))
				|| "1".equals(String.valueOf(value));
	}

	private record NoticePayload(String title, String content, String scopeType, String campus, boolean popupEnabled,
			String status, LocalDateTime publishedAt) {
	}
}
