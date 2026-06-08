package com.example.Second_hand.trading.platform.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.Second_hand.trading.platform.entity.NotificationEntity;
import com.example.Second_hand.trading.platform.mapper.NotificationMapper;

@Service
public class MessageService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private NotificationMapper notificationMapper;

	public void sendOrderNotification(Long userId, String title, String content) {
		Map<String, Object> message = new HashMap<>();
		message.put("type", "order");
		message.put("title", title);
		message.put("content", content);
		message.put("timestamp", LocalDateTime.now().toString());

		messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/notifications", message);

		saveNotification(userId, "ORDER", title, content);
	}

	public void sendChatMessage(Long userId, Long chatId, String content) {
		Map<String, Object> message = new HashMap<>();
		message.put("type", "chat");
		message.put("chatId", chatId);
		message.put("content", content);
		message.put("timestamp", LocalDateTime.now().toString());

		messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/messages", message);
	}

	public void sendSystemNotification(Long userId, String title, String content) {
		Map<String, Object> message = new HashMap<>();
		message.put("type", "system");
		message.put("title", title);
		message.put("content", content);
		message.put("timestamp", LocalDateTime.now().toString());

		messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/notifications", message);

		saveNotification(userId, "SYSTEM", title, content);
	}

	private void saveNotification(Long userId, String type, String title, String content) {
		NotificationEntity notification = new NotificationEntity();
		notification.setUserId(userId);
		notification.setType(type);
		notification.setTitle(title);
		notification.setContent(content);
		notificationMapper.insert(notification);
	}

	public void broadcastToAll(String title, String content) {
		Map<String, Object> message = new HashMap<>();
		message.put("type", "broadcast");
		message.put("title", title);
		message.put("content", content);
		message.put("timestamp", LocalDateTime.now().toString());

		messagingTemplate.convertAndSend("/topic/broadcast", (Object) message);
	}
}
