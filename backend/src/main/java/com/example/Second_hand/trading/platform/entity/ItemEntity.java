package com.example.Second_hand.trading.platform.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("items")
public class ItemEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long sellerId;
	private Long categoryId;
	private String title;
	private String description;
	private BigDecimal price;
	private BigDecimal originalPrice;
	private String conditionLevel;
	private String campus;
	private String dormitory;
	private String tradePlace;
	private String tradeModes;
	private String status;
	private Integer swapSupported;
	private Integer viewCount;
	private Integer favoriteCount;
	private Integer deleted;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
