package com.example.Second_hand.trading.platform.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class HealthService {
	public Map<String, Object> health() {
		return Map.of(
				"status", "UP",
				"service", "Second-hand-trading-platform",
				"database", "second_hand_trade",
				"timestamp", Instant.now().toString());
	}
}
