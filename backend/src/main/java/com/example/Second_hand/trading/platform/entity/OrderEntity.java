package com.example.Second_hand.trading.platform.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("orders")
public class OrderEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private String orderNo;
	private Long itemId;
	private Long buyerId;
	private Long sellerId;
	private BigDecimal amount;
	private String status;
	private String tradeMode;
	private String tradeCode;
	private String tradeQrUrl;
	private String buyerMessage;
	private String cancelReason;
	private LocalDateTime completedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
