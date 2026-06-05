package com.example.Second_hand.trading.platform.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("files")
public class FileEntity {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long ownerId;
	private String fileType;
	private String originalName;
	private String storageKey;
	private String url;
	private Long sizeBytes;
	private String contentType;
	private LocalDateTime createdAt;
}
