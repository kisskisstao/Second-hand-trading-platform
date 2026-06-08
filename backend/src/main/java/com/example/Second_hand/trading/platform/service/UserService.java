package com.example.Second_hand.trading.platform.service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	private final JdbcTemplate jdbcTemplate;
	private final ItemService itemService;

	public UserService(JdbcTemplate jdbcTemplate, ItemService itemService) {
		this.jdbcTemplate = jdbcTemplate;
		this.itemService = itemService;
	}

	public Map<String, Object> currentUser(Long userId) {
		return userId == null ? Map.of() : userById(userId);
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

	public List<Map<String, Object>> myItems(Long userId) {
		return itemService.itemsBySeller(userId);
	}

	public List<Map<String, Object>> myFavorites(Long userId) {
		return itemService.favoriteItems(userId);
	}

	public List<Map<String, Object>> notifications(Long userId) {
		if (userId == null) {
			return List.of();
		}
		return jdbcTemplate.queryForList("""
				SELECT id AS notificationId, type, title, content, read_at AS readAt, created_at AS createdAt
				FROM notifications
				WHERE user_id = ?
				ORDER BY created_at DESC
				LIMIT 100
				""", userId);
	}

	public List<Map<String, Object>> comments(Long userId) {
		if (userId == null) {
			return List.of();
		}
		return jdbcTemplate.queryForList("""
				SELECT c.id AS commentId, c.item_id AS itemId, i.title AS itemTitle,
				  c.user_id AS userId, commenter.nickname AS userName,
				  i.seller_id AS sellerId, c.parent_id AS parentId,
				  c.content, c.created_at AS createdAt,
				  CASE WHEN c.user_id = ? THEN 'SENT' ELSE 'RECEIVED' END AS relation
				FROM item_comments c
				LEFT JOIN items i ON i.id = c.item_id
				LEFT JOIN users commenter ON commenter.id = c.user_id
				WHERE c.deleted = 0
				  AND i.deleted = 0
				  AND (c.user_id = ? OR i.seller_id = ?)
				ORDER BY c.created_at DESC
				LIMIT 100
				""", userId, userId, userId);
	}

	public List<Map<String, Object>> reviews(Integer userId) {
		if (userId == null) {
			return List.of();
		}
		return jdbcTemplate.queryForList("""
				SELECT r.id AS reviewId, r.order_id AS orderId, r.reviewer_id AS reviewerId,
				  reviewer.nickname AS reviewerName, r.target_user_id AS targetUserId,
				  r.rating, r.content, r.created_at AS createdAt
				FROM reviews r
				LEFT JOIN users reviewer ON reviewer.id = r.reviewer_id
				WHERE r.target_user_id = ?
				ORDER BY r.created_at DESC
				LIMIT 100
				""", userId);
	}

	private Map<String, Object> userById(Long userId) {
		List<Map<String, Object>> users = jdbcTemplate.queryForList("""
				SELECT id AS userId, student_no AS studentNo, nickname, real_name AS realName,
				  phone, email, avatar_url AS avatarUrl, campus, department,
				  enrollment_year AS enrollmentYear, credit_score AS creditScore,
				  created_at AS createdAt
				FROM users
				WHERE deleted = 0 AND id = ?
				LIMIT 1
				""", userId);
		return users.isEmpty() ? Map.of() : users.get(0);
	}

	public void updateCreditScore(Long userId, int scoreChange) {
		jdbcTemplate.update("""
				UPDATE users 
				SET credit_score = GREATEST(0, LEAST(credit_score + ?, 200))
				WHERE id = ?
				""", scoreChange, userId);
	}

	public int getUserCreditScore(Long userId) {
		List<Map<String, Object>> result = jdbcTemplate.queryForList("""
				SELECT credit_score FROM users WHERE id = ? AND deleted = 0
				""", userId);
		if (result.isEmpty()) {
			return 100;
		}
		return ((Number) result.get(0).get("credit_score")).intValue();
	}
}
