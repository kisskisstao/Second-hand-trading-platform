package com.example.Second_hand.trading.platform.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("chat_messages")
public class ChatMessageEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long chatId;
	private Long senderId;
	private String messageType;
	private String content;
	private String imageUrl;
	private Long itemId;
	private Integer filtered;
	private LocalDateTime readAt;
	private LocalDateTime createdAt;
}
