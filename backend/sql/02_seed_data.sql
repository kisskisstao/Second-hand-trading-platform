USE second_hand_trade;
SET NAMES utf8mb4;

-- Seed only stable base data: admin account, categories, item list, item images, and admin settings.
-- Do not insert demo users, favorites, comments, orders, chats, purchases, exchanges, notifications, reports, disputes, or notices.

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

INSERT INTO items (
  id, seller_id, category_id, title, description, price, original_price, condition_level,
  campus, dormitory, trade_place, trade_modes, status, swap_supported,
  view_count, favorite_count, created_at
) VALUES
(1, 1001, 1, '高等数学同济第七版上下册', '教材干净，少量重点标注，适合期末复习。', 28.00, 68.00, 'LIKE_NEW', '校本部', '桃李园3栋', '图书馆北门', 'OFFLINE', 'ON_SALE', 1, 238, 0, '2026-06-01 10:00:00'),
(2, 1002, 2, 'iPad Air 5 64G 深空灰', '课堂记笔记使用，屏幕无划痕，附保护壳。', 2699.00, 4399.00, 'GOOD', '东校区', '东苑6栋', '东校区食堂门口', 'OFFLINE,ESCROW', 'ON_SALE', 0, 520, 0, '2026-06-01 11:00:00'),
(3, 1003, 3, '宿舍小冰箱52L', '毕业搬宿舍出，制冷正常。', 260.00, 499.00, 'GOOD', '西校区', '西苑2栋', '西苑快递站', 'OFFLINE', 'ON_SALE', 1, 342, 0, '2026-06-01 12:00:00'),
(4, 1004, 4, 'Nike运动双肩包', '容量大，可放电脑和球鞋。', 99.00, 249.00, 'LIKE_NEW', '南校区', '南苑5栋', '操场入口', 'OFFLINE', 'ON_SALE', 0, 155, 0, '2026-06-02 09:00:00'),
(5, 1005, 5, '羽毛球拍双拍套装', '含两支球拍和拍套，适合新手。', 68.00, 168.00, 'FAIR', '大学城校区', '榕园1栋', '体育馆门口', 'OFFLINE', 'ON_SALE', 1, 126, 0, '2026-06-02 10:00:00'),
(6, 1006, 2, '机械键盘87键茶轴', '灯效正常，键帽完整，手感安静。', 128.00, 299.00, 'LIKE_NEW', '校本部', '梅园8栋', '二教门口', 'OFFLINE,ESCROW', 'ON_SALE', 0, 410, 0, '2026-06-02 11:00:00'),
(7, 1007, 1, '考研英语真题套装', '近十年真题，解析册齐全。', 35.00, 118.00, 'GOOD', '东校区', '东苑1栋', '教学楼A座', 'OFFLINE', 'ON_SALE', 0, 287, 0, '2026-06-03 09:00:00'),
(8, 1008, 3, '宿舍折叠桌', '床上学习桌，可折叠，桌面稳定。', 22.00, 59.00, 'FAIR', '西校区', '西苑7栋', '宿舍楼下', 'OFFLINE', 'ON_SALE', 1, 93, 0, '2026-06-03 10:00:00'),
(9, 1009, 6, '入门吉他一把', '适合社团练习，琴弦刚换。', 180.00, 399.00, 'GOOD', '校本部', '松园4栋', '音乐教室门口', 'OFFLINE', 'ON_SALE', 1, 176, 0, '2026-06-03 11:00:00'),
(10, 1010, 5, '山地车通勤自行车', '车况正常，适合校内通勤。', 320.00, 899.00, 'GOOD', '南校区', '南苑3栋', '南门车棚', 'OFFLINE', 'ON_SALE', 0, 301, 0, '2026-06-04 09:00:00'),
(11, 1011, 1, '线性代数辅导讲义', '期末复习讲义和错题整理，重点章节标注清楚。', 18.00, 45.00, 'GOOD', '校本部', '竹园2栋', '三教大厅', 'OFFLINE', 'ON_SALE', 0, 132, 0, '2026-06-04 10:00:00'),
(12, 1012, 1, '四六级词汇书全套', '单词书、听力材料和真题册，适合暑假备考。', 42.00, 128.00, 'LIKE_NEW', '东校区', '东苑4栋', '东校区图书馆', 'OFFLINE', 'ON_SALE', 0, 168, 0, '2026-06-04 11:00:00'),
(13, 1013, 2, '罗技无线鼠标', '办公学习常用，按键正常，接收器齐全。', 55.00, 129.00, 'GOOD', '西校区', '西苑5栋', '西校区食堂', 'OFFLINE', 'ON_SALE', 0, 210, 0, '2026-06-04 12:00:00'),
(14, 1014, 2, '显示器支架升降臂', '适合宿舍桌面改造，承重稳定，螺丝配件齐全。', 88.00, 199.00, 'LIKE_NEW', '南校区', '南苑1栋', '实验楼门口', 'OFFLINE', 'ON_SALE', 1, 145, 0, '2026-06-04 13:00:00'),
(15, 1015, 3, '宿舍小台灯', '三档亮度，Type-C 供电，晚上自习够用。', 26.00, 79.00, 'FAIR', '大学城校区', '榕园3栋', '大学城快递站', 'OFFLINE', 'ON_SALE', 0, 98, 0, '2026-06-04 14:00:00'),
(16, 1016, 3, '收纳箱三件套', '搬宿舍多出来的收纳箱，干净无破损。', 39.00, 99.00, 'GOOD', '校本部', '松园6栋', '宿舍楼下', 'OFFLINE', 'ON_SALE', 0, 120, 0, '2026-06-04 15:00:00'),
(17, 1017, 4, '24寸拉杆箱', '轮子顺滑，箱体有轻微使用痕迹，适合短途出行。', 96.00, 259.00, 'FAIR', '东校区', '东苑2栋', '东门保安亭', 'OFFLINE', 'ON_SALE', 0, 188, 0, '2026-06-04 16:00:00'),
(18, 1018, 5, '运动护膝一对', '篮球和羽毛球训练可用，弹力正常。', 25.00, 69.00, 'GOOD', '西校区', '西苑4栋', '体育馆侧门', 'OFFLINE', 'ON_SALE', 1, 76, 0, '2026-06-04 17:00:00'),
(19, 1019, 2, '蓝牙小音箱', '续航正常，宿舍听歌和户外活动都够用。', 75.00, 179.00, 'LIKE_NEW', '南校区', '南苑7栋', '南校区图书馆', 'OFFLINE', 'ON_SALE', 0, 230, 0, '2026-06-04 18:00:00'),
(20, 1020, 6, '民谣吉他调音器', '社团排练备用调音器，屏幕显示正常。', 19.00, 49.00, 'GOOD', '大学城校区', '榕园5栋', '音乐社活动室', 'OFFLINE', 'ON_SALE', 0, 64, 0, '2026-06-04 19:00:00')
ON DUPLICATE KEY UPDATE
  seller_id = VALUES(seller_id),
  category_id = VALUES(category_id),
  title = VALUES(title),
  description = VALUES(description),
  price = VALUES(price),
  original_price = VALUES(original_price),
  condition_level = VALUES(condition_level),
  campus = VALUES(campus),
  dormitory = VALUES(dormitory),
  trade_place = VALUES(trade_place),
  trade_modes = VALUES(trade_modes),
  status = VALUES(status),
  swap_supported = VALUES(swap_supported),
  view_count = VALUES(view_count),
  favorite_count = VALUES(favorite_count);

INSERT INTO item_images (id, item_id, image_url, sort_order) VALUES
(1, 1, 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=640&q=80', 1),
(2, 2, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&w=640&q=80', 1),
(3, 3, 'https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?auto=format&fit=crop&w=640&q=80', 1),
(4, 4, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=640&q=80', 1),
(5, 5, 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=640&q=80', 1),
(6, 6, 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?auto=format&fit=crop&w=640&q=80', 1),
(7, 7, 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=640&q=80', 1),
(8, 8, 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?auto=format&fit=crop&w=640&q=80', 1),
(9, 9, 'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?auto=format&fit=crop&w=640&q=80', 1),
(10, 10, 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=640&q=80', 1),
(11, 11, 'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=640&q=80', 1),
(12, 12, 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=640&q=80', 1),
(13, 13, 'https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=640&q=80', 1),
(14, 14, 'https://images.unsplash.com/photo-1547082299-de196ea013d6?auto=format&fit=crop&w=640&q=80', 1),
(15, 15, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=640&q=80', 1),
(16, 16, 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=640&q=80', 1),
(17, 17, 'https://images.unsplash.com/photo-1581553680321-4fffae59fccd?auto=format&fit=crop&w=640&q=80', 1),
(18, 18, 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=640&q=80', 1),
(19, 19, 'https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=640&q=80', 1),
(20, 20, 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?auto=format&fit=crop&w=640&q=80', 1)
ON DUPLICATE KEY UPDATE image_url = VALUES(image_url), sort_order = VALUES(sort_order);

INSERT INTO files (id, owner_id, file_type, original_name, storage_key, url, size_bytes, content_type) VALUES
(1, 1001, 'IMAGE', 'math-book.jpg', 'items/1/math-book.jpg', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=640&q=80', 238000, 'image/jpeg'),
(2, 1002, 'IMAGE', 'ipad.jpg', 'items/2/ipad.jpg', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&w=640&q=80', 310000, 'image/jpeg'),
(3, 1003, 'IMAGE', 'fridge.jpg', 'items/3/fridge.jpg', 'https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?auto=format&fit=crop&w=640&q=80', 288000, 'image/jpeg'),
(4, 1004, 'IMAGE', 'bag.jpg', 'items/4/bag.jpg', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=640&q=80', 210000, 'image/jpeg'),
(5, 1005, 'IMAGE', 'badminton.jpg', 'items/5/badminton.jpg', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=640&q=80', 198000, 'image/jpeg'),
(6, 1006, 'IMAGE', 'keyboard.jpg', 'items/6/keyboard.jpg', 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?auto=format&fit=crop&w=640&q=80', 340000, 'image/jpeg'),
(7, 1007, 'IMAGE', 'english-books.jpg', 'items/7/english-books.jpg', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=640&q=80', 270000, 'image/jpeg'),
(8, 1008, 'IMAGE', 'desk.jpg', 'items/8/desk.jpg', 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?auto=format&fit=crop&w=640&q=80', 252000, 'image/jpeg'),
(9, 1009, 'IMAGE', 'guitar.jpg', 'items/9/guitar.jpg', 'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?auto=format&fit=crop&w=640&q=80', 300000, 'image/jpeg'),
(10, 1010, 'IMAGE', 'bike.jpg', 'items/10/bike.jpg', 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=640&q=80', 360000, 'image/jpeg'),
(11, 1011, 'IMAGE', 'linear-algebra.jpg', 'items/11/linear-algebra.jpg', 'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=640&q=80', 242000, 'image/jpeg'),
(12, 1012, 'IMAGE', 'cet-books.jpg', 'items/12/cet-books.jpg', 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=640&q=80', 260000, 'image/jpeg'),
(13, 1013, 'IMAGE', 'mouse.jpg', 'items/13/mouse.jpg', 'https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=640&q=80', 190000, 'image/jpeg'),
(14, 1014, 'IMAGE', 'monitor-arm.jpg', 'items/14/monitor-arm.jpg', 'https://images.unsplash.com/photo-1547082299-de196ea013d6?auto=format&fit=crop&w=640&q=80', 315000, 'image/jpeg'),
(15, 1015, 'IMAGE', 'desk-lamp.jpg', 'items/15/desk-lamp.jpg', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=640&q=80', 205000, 'image/jpeg'),
(16, 1016, 'IMAGE', 'storage-box.jpg', 'items/16/storage-box.jpg', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=640&q=80', 230000, 'image/jpeg'),
(17, 1017, 'IMAGE', 'suitcase.jpg', 'items/17/suitcase.jpg', 'https://images.unsplash.com/photo-1581553680321-4fffae59fccd?auto=format&fit=crop&w=640&q=80', 280000, 'image/jpeg'),
(18, 1018, 'IMAGE', 'knee-pad.jpg', 'items/18/knee-pad.jpg', 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=640&q=80', 175000, 'image/jpeg'),
(19, 1019, 'IMAGE', 'speaker.jpg', 'items/19/speaker.jpg', 'https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=640&q=80', 260000, 'image/jpeg'),
(20, 1020, 'IMAGE', 'tuner.jpg', 'items/20/tuner.jpg', 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?auto=format&fit=crop&w=640&q=80', 160000, 'image/jpeg')
ON DUPLICATE KEY UPDATE url = VALUES(url), size_bytes = VALUES(size_bytes), content_type = VALUES(content_type);

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
