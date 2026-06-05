package com.example.Second_hand.trading.platform.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("item_comments")
public class ItemCommentEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long itemId;
	private Long userId;
	private Long parentId;
	private String content;
	private Integer deleted;
	private LocalDateTime createdAt;
}
