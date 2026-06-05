package com.example.Second_hand.trading.platform.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("exchanges")
public class ExchangeEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private String exchangeNo;
	private Long userId;
	private Long itemId;
	private Long targetItemId;
	private Long targetCategoryId;
	private String expectedTitle;
	private String description;
	private String campus;
	private String status;
	private Integer deleted;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
