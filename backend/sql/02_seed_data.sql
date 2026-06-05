USE second_hand_trade;
SET NAMES utf8mb4;

-- Seed only stable base data: admin account, categories, sensitive words, and admin settings.
-- Ordinary user, product, and transaction data is cleared so you can create fresh accounts and items.

DELETE FROM chat_messages;
DELETE FROM chats;
DELETE FROM payments;
DELETE FROM order_status_logs;
DELETE FROM orders;
DELETE FROM reviews;
DELETE FROM favorites;
DELETE FROM item_comments;
DELETE FROM item_images;
DELETE FROM files;
DELETE FROM reports;
DELETE FROM disputes;
DELETE FROM swap_requests;
DELETE FROM wanted_posts;
DELETE FROM exchanges;
DELETE FROM purchases;
DELETE FROM notifications;
DELETE FROM announcements;
DELETE FROM items;
DELETE FROM user_privacy;
DELETE FROM users;

ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE user_privacy AUTO_INCREMENT = 1;
ALTER TABLE items AUTO_INCREMENT = 1;
ALTER TABLE item_images AUTO_INCREMENT = 1;
ALTER TABLE favorites AUTO_INCREMENT = 1;
ALTER TABLE item_comments AUTO_INCREMENT = 1;
ALTER TABLE orders AUTO_INCREMENT = 1;
ALTER TABLE order_status_logs AUTO_INCREMENT = 1;
ALTER TABLE payments AUTO_INCREMENT = 1;
ALTER TABLE chats AUTO_INCREMENT = 1;
ALTER TABLE chat_messages AUTO_INCREMENT = 1;
ALTER TABLE purchases AUTO_INCREMENT = 1;
ALTER TABLE exchanges AUTO_INCREMENT = 1;

INSERT INTO admin_users (id, username, password_hash, role, status, last_login_at) VALUES
(1, 'admin', 'sha256$ac0e7d037817094e9e0b4441f9bae3209d67b02fa484917065f71b16109a1a78', 'SUPER_ADMIN', 'NORMAL', NULL)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), role = VALUES(role), status = VALUES(status);

INSERT INTO categories (id, name, sort_order, enabled) VALUES
(1, '教材教辅', 1, 1),
(2, '数码3C', 2, 1),
(3, '生活日用', 3, 1),
(4, '服饰鞋包', 4, 1),
(5, '运动户外', 5, 1),
(6, '其他', 6, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort_order = VALUES(sort_order), enabled = VALUES(enabled);

INSERT INTO category_tags (id, category_id, name, sort_order) VALUES
(1, 1, '公共课教材', 1),
(2, 1, '考研资料', 2),
(3, 1, '四六级资料', 3),
(4, 2, '手机平板', 1),
(5, 2, '电脑配件', 2),
(6, 2, '耳机音箱', 3),
(7, 3, '宿舍电器', 1),
(8, 3, '收纳清洁', 2),
(9, 3, '床上用品', 3),
(10, 4, '箱包', 1),
(11, 4, '鞋服', 2),
(12, 4, '配饰', 3),
(13, 5, '球类', 1),
(14, 5, '健身器材', 2),
(15, 5, '骑行装备', 3),
(16, 6, '票券', 1),
(17, 6, '乐器', 2),
(18, 6, '杂物', 3)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort_order = VALUES(sort_order);

INSERT INTO sensitive_words (id, word, enabled, created_by) VALUES
(1, '私下转账', 1, 1),
(2, '押金', 1, 1),
(3, '脱离平台', 1, 1),
(4, '先付款', 1, 1),
(5, '加微信交易', 1, 1),
(6, '绕过平台', 1, 1),
(7, '定金不退', 1, 1),
(8, '银行卡转账', 1, 1),
(9, '虚拟币', 1, 1),
(10, '不走平台', 1, 1)
ON DUPLICATE KEY UPDATE enabled = VALUES(enabled), created_by = VALUES(created_by);

INSERT INTO system_settings (id, setting_key, setting_value, description, updated_by) VALUES
(1, 'trade_rules', '{"maxImages":9,"disputeDays":3,"creditDeduction":10}', '平台交易规则', 1),
(2, 'payment_wechat', '{"appId":"wx-campus-demo","enabled":false}', '微信支付配置', 1),
(3, 'payment_alipay', '{"appId":"alipay-campus-demo","enabled":false}', '支付宝支付配置', 1),
(4, 'payment_campus_card', '{"merchant":"CAMPUS-2026","enabled":false}', '校园卡支付配置', 1),
(5, 'im_filter', '{"enabled":true,"blockSend":false}', 'IM敏感词过滤配置', 1),
(6, 'item_publish', '{"autoAudit":false,"maxDrafts":20}', '商品发布配置', 1),
(7, 'credit_rule', '{"defaultScore":100,"banBelow":40}', '信用分规则', 1),
(8, 'announcement_popup', '{"enabled":true,"oncePerDay":true}', '公告弹窗配置', 1),
(9, 'campus_recommend', '{"enabled":true,"defaultCampus":"校本部"}', '同校区推荐配置', 1),
(10, 'upload_policy', '{"maxFileSizeMb":10,"allowed":["jpg","png","webp"]}', '上传策略配置', 1)
ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), description = VALUES(description), updated_by = VALUES(updated_by);
