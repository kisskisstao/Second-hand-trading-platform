package com.example.Second_hand.trading.platform.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("purchases")
public class PurchaseEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long userId;
	private String title;
	private String description;
	private Long categoryId;
	private String campus;
	private BigDecimal budgetMin;
	private BigDecimal budgetMax;
	private String status;
	private Integer deleted;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
