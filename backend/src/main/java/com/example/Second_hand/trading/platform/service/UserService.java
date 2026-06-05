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

	public List<Map<String, Object>> reviews(Integer userId) {
		return List.of();
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
}
