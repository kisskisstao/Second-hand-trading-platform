package com.example.Second_hand.trading.platform.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("payments")
public class PaymentEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private String paymentNo;
	private Long orderId;
	private String orderNo;
	private String provider;
	private BigDecimal amount;
	private String status;
	private String providerTradeNo;
	private String paymentUrl;
	private String qrUrl;
	private String requestPayload;
	private String responsePayload;
	private LocalDateTime paidAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
