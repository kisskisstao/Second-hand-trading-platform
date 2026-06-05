package com.example.Second_hand.trading.platform.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("chats")
public class ChatEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long itemId;
	private Long buyerId;
	private Long sellerId;
	private String lastMessage;
	private LocalDateTime lastMessageAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
