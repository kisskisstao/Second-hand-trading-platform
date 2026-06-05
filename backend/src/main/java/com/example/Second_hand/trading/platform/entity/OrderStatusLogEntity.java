package com.example.Second_hand.trading.platform.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("order_status_logs")
public class OrderStatusLogEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long orderId;
	private String fromStatus;
	private String toStatus;
	private Long operatorId;
	private String operatorType;
	private String remark;
	private LocalDateTime createdAt;
}
