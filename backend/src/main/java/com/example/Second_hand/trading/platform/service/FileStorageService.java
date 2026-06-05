package com.example.Second_hand.trading.platform.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.Second_hand.trading.platform.entity.FileEntity;
import com.example.Second_hand.trading.platform.mapper.FileMapper;

@Service
public class FileStorageService {
	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

	private final FileMapper fileMapper;
	private final Path imageRoot;

	public FileStorageService(FileMapper fileMapper, @Value("${app.upload.dir:uploads}") String uploadDir) {
		this.fileMapper = fileMapper;
		this.imageRoot = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("images");
	}

	@Transactional
	public Map<String, Object> storeImage(Long ownerId, MultipartFile file) {
		if (ownerId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
		}
		if (file == null || file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请选择要上传的图片");
		}

		String contentType = StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream";
		if (!contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "只能上传图片文件");
		}

		String originalName = originalName(file);
		String storageKey = UUID.randomUUID() + extension(originalName, contentType);
		Path target = imageRoot.resolve(storageKey).normalize();
		if (!target.startsWith(imageRoot)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "图片文件名不合法");
		}

		try {
			Files.createDirectories(imageRoot);
			try (InputStream input = file.getInputStream()) {
				Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "图片保存失败", ex);
		}

		String url = "/api/files/images/" + storageKey;
		FileEntity entity = new FileEntity();
		entity.setOwnerId(ownerId);
		entity.setFileType("IMAGE");
		entity.setOriginalName(originalName);
		entity.setStorageKey(storageKey);
		entity.setUrl(url);
		entity.setSizeBytes(file.getSize());
		entity.setContentType(contentType);
		fileMapper.insert(entity);

		return Map.of(
				"fileId", entity.getId(),
				"filename", originalName,
				"storageKey", storageKey,
				"url", url,
				"sizeBytes", file.getSize(),
				"contentType", contentType);
	}

	public ResponseEntity<Resource> loadImage(String storageKey) {
		if (!StringUtils.hasText(storageKey) || storageKey.contains("/") || storageKey.contains("\\")) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "图片不存在");
		}

		Path target = imageRoot.resolve(storageKey).normalize();
		if (!target.startsWith(imageRoot) || !Files.exists(target) || !Files.isRegularFile(target)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "图片不存在");
		}

		try {
			Resource resource = new UrlResource(target.toUri());
			String contentType = Files.probeContentType(target);
			if (!StringUtils.hasText(contentType)) {
				contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + storageKey + "\"")
					.body(resource);
		} catch (IOException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "图片读取失败", ex);
		}
	}

	private String originalName(MultipartFile file) {
		String original = file.getOriginalFilename();
		if (!StringUtils.hasText(original)) {
			return "image";
		}
		String normalized = original.replace("\\", "/");
		return normalized.substring(normalized.lastIndexOf('/') + 1);
	}

	private String extension(String originalName, String contentType) {
		int dotIndex = originalName.lastIndexOf('.');
		if (dotIndex >= 0) {
			String ext = originalName.substring(dotIndex).toLowerCase(Locale.ROOT);
			if (ALLOWED_EXTENSIONS.contains(ext)) {
				return ext;
			}
		}
		String lowerType = contentType.toLowerCase(Locale.ROOT);
		if (lowerType.contains("png")) return ".png";
		if (lowerType.contains("webp")) return ".webp";
		if (lowerType.contains("gif")) return ".gif";
		return ".jpg";
	}
}
