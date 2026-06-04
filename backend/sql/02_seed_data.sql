USE second_hand_trade;
SET NAMES utf8mb4;

-- 普通用户表 users 不插入账号数据；仅保留管理员账号 admin/admin123456。

INSERT INTO admin_users (id, username, password_hash, role, status, last_login_at) VALUES
(1, 'admin', 'sha256$ac0e7d037817094e9e0b4441f9bae3209d67b02fa484917065f71b16109a1a78', 'SUPER_ADMIN', 'NORMAL', '2026-06-04 09:00:00')
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

INSERT INTO user_privacy (id, user_id, phone_visible, wechat_visible, qq, wechat) VALUES
(1, 1001, 0, 1, '810001001', 'campus_u1001'),
(2, 1002, 1, 0, '810001002', 'campus_u1002'),
(3, 1003, 0, 0, '810001003', 'campus_u1003'),
(4, 1004, 1, 1, '810001004', 'campus_u1004'),
(5, 1005, 0, 1, '810001005', 'campus_u1005'),
(6, 1006, 1, 0, '810001006', 'campus_u1006'),
(7, 1007, 0, 0, '810001007', 'campus_u1007'),
(8, 1008, 1, 1, '810001008', 'campus_u1008'),
(9, 1009, 0, 1, '810001009', 'campus_u1009'),
(10, 1010, 1, 0, '810001010', 'campus_u1010')
ON DUPLICATE KEY UPDATE phone_visible = VALUES(phone_visible), wechat_visible = VALUES(wechat_visible), qq = VALUES(qq), wechat = VALUES(wechat);

INSERT INTO items (id, seller_id, category_id, title, description, price, original_price, condition_level, campus, dormitory, trade_place, trade_modes, status, swap_supported, view_count, favorite_count, created_at) VALUES
(1, 1001, 1, '高等数学同济第七版上下册', '教材干净，少量重点标注，适合期末复习。', 28.00, 68.00, 'LIKE_NEW', '校本部', '桃李园3栋', '图书馆北门', 'OFFLINE', 'ON_SALE', 1, 238, 18, '2026-06-01 10:00:00'),
(2, 1002, 2, 'iPad Air 5 64G 深空灰', '课堂记笔记使用，屏幕无划痕，附保护壳。', 2699.00, 4399.00, 'GOOD', '东校区', '东苑6栋', '东校区食堂门口', 'OFFLINE,ESCROW', 'ON_SALE', 0, 520, 43, '2026-06-01 11:00:00'),
(3, 1003, 3, '宿舍小冰箱52L', '毕业搬宿舍出，制冷正常。', 260.00, 499.00, 'GOOD', '西校区', '西苑2栋', '西苑快递站', 'OFFLINE', 'RESERVED', 1, 342, 29, '2026-06-01 12:00:00'),
(4, 1004, 4, 'Nike运动双肩包', '容量大，可放电脑和球鞋。', 99.00, 249.00, 'LIKE_NEW', '南校区', '南苑5栋', '操场入口', 'OFFLINE', 'ON_SALE', 0, 155, 12, '2026-06-02 09:00:00'),
(5, 1005, 5, '羽毛球拍双拍套装', '含两支球拍和拍套，适合新手。', 68.00, 168.00, 'FAIR', '大学城校区', '榕园1栋', '体育馆门口', 'OFFLINE', 'ON_SALE', 1, 126, 9, '2026-06-02 10:00:00'),
(6, 1006, 2, '机械键盘87键茶轴', '灯效正常，键帽完整，手感安静。', 128.00, 299.00, 'LIKE_NEW', '校本部', '梅园8栋', '二教门口', 'OFFLINE,ESCROW', 'SOLD', 0, 410, 35, '2026-06-02 11:00:00'),
(7, 1007, 1, '考研英语真题套装', '近十年真题，解析册齐全。', 35.00, 118.00, 'GOOD', '东校区', '东苑1栋', '教学楼A座', 'OFFLINE', 'ON_SALE', 0, 287, 24, '2026-06-03 09:00:00'),
(8, 1008, 3, '宿舍折叠桌', '床上学习桌，可折叠，桌面稳定。', 22.00, 59.00, 'FAIR', '西校区', '西苑7栋', '宿舍楼下', 'OFFLINE', 'ON_SALE', 1, 93, 7, '2026-06-03 10:00:00'),
(9, 1009, 6, '入门吉他一把', '适合社团练习，琴弦刚换。', 180.00, 399.00, 'GOOD', '校本部', '松园4栋', '音乐教室门口', 'OFFLINE', 'ON_SALE', 1, 176, 15, '2026-06-03 11:00:00'),
(10, 1010, 5, '山地车通勤自行车', '车况正常，适合校内通勤。', 320.00, 899.00, 'GOOD', '南校区', '南苑3栋', '南门车棚', 'OFFLINE', 'ON_SALE', 0, 301, 27, '2026-06-04 09:00:00')
ON DUPLICATE KEY UPDATE title = VALUES(title), price = VALUES(price), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

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
(10, 10, 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=640&q=80', 1)
ON DUPLICATE KEY UPDATE image_url = VALUES(image_url), sort_order = VALUES(sort_order);

INSERT INTO favorites (id, user_id, item_id) VALUES
(1, 2001, 1), (2, 2002, 1), (3, 2003, 2), (4, 2004, 3), (5, 2005, 4),
(6, 2006, 5), (7, 2007, 6), (8, 2008, 7), (9, 2009, 8), (10, 2010, 9)
ON DUPLICATE KEY UPDATE created_at = CURRENT_TIMESTAMP;

INSERT INTO item_comments (id, item_id, user_id, parent_id, content) VALUES
(1, 1, 2001, NULL, '今晚图书馆北门可以交易吗？'),
(2, 1, 1001, 1, '可以，19点以后都方便。'),
(3, 2, 2002, NULL, '可以现场验机吗？'),
(4, 2, 1002, 3, '可以，东校区食堂门口。'),
(5, 3, 2003, NULL, '冰箱声音大吗？'),
(6, 4, 2004, NULL, '背包容量多大？'),
(7, 5, 2005, NULL, '支持换篮球吗？'),
(8, 7, 2006, NULL, '资料有答案解析吗？'),
(9, 9, 2007, NULL, '吉他有琴包吗？'),
(10, 10, 2008, NULL, '自行车能试骑吗？')
ON DUPLICATE KEY UPDATE content = VALUES(content);

INSERT INTO orders (id, order_no, item_id, buyer_id, seller_id, amount, status, trade_mode, trade_code, buyer_message, completed_at, created_at) VALUES
(1, 'OD202606040001', 1, 2001, 1001, 28.00, 'PENDING', 'OFFLINE', 'A7K9', '今晚7点图书馆北门交易', NULL, '2026-06-04 10:00:00'),
(2, 'OD202606040002', 2, 2002, 1002, 2699.00, 'ACCEPTED', 'ESCROW', 'B2Q8', '希望现场验机', NULL, '2026-06-04 10:20:00'),
(3, 'OD202606040003', 3, 2003, 1003, 260.00, 'DISPUTING', 'OFFLINE', 'C3M2', '西苑快递站面交', NULL, '2026-06-04 11:00:00'),
(4, 'OD202606040004', 4, 2004, 1004, 99.00, 'COMPLETED', 'OFFLINE', 'D4N1', '操场入口交易', '2026-06-04 13:30:00', '2026-06-04 12:00:00'),
(5, 'OD202606040005', 5, 2005, 1005, 68.00, 'CANCELED', 'OFFLINE', 'E5R6', '时间不合适', NULL, '2026-06-04 12:20:00'),
(6, 'OD202606040006', 6, 2006, 1006, 128.00, 'COMPLETED', 'ESCROW', 'F6T7', '线上担保交易', '2026-06-04 14:30:00', '2026-06-04 13:00:00'),
(7, 'OD202606040007', 7, 2007, 1007, 35.00, 'PENDING', 'OFFLINE', 'G7U8', '教学楼A座交易', NULL, '2026-06-04 13:20:00'),
(8, 'OD202606040008', 8, 2008, 1008, 22.00, 'ACCEPTED', 'OFFLINE', 'H8V9', '宿舍楼下交易', NULL, '2026-06-04 14:00:00'),
(9, 'OD202606040009', 9, 2009, 1009, 180.00, 'PENDING', 'OFFLINE', 'J9W1', '想先看琴况', NULL, '2026-06-04 15:00:00'),
(10, 'OD202606040010', 10, 2010, 1010, 320.00, 'ACCEPTED', 'OFFLINE', 'K1X2', '南门车棚试骑', NULL, '2026-06-04 16:00:00')
ON DUPLICATE KEY UPDATE status = VALUES(status), amount = VALUES(amount), updated_at = CURRENT_TIMESTAMP;

INSERT INTO order_status_logs (id, order_id, from_status, to_status, operator_id, operator_type, remark) VALUES
(1, 1, NULL, 'PENDING', 2001, 'USER', '买家发起预订'),
(2, 2, 'PENDING', 'ACCEPTED', 1002, 'USER', '卖家确认订单'),
(3, 3, 'ACCEPTED', 'DISPUTING', 2003, 'USER', '买家申请纠纷'),
(4, 4, 'ACCEPTED', 'COMPLETED', 1004, 'USER', '线下面交完成'),
(5, 5, 'PENDING', 'CANCELED', 2005, 'USER', '买家取消订单'),
(6, 6, 'ACCEPTED', 'COMPLETED', 2006, 'USER', '买家确认收货'),
(7, 7, NULL, 'PENDING', 2007, 'USER', '买家发起预订'),
(8, 8, 'PENDING', 'ACCEPTED', 1008, 'USER', '卖家确认订单'),
(9, 9, NULL, 'PENDING', 2009, 'USER', '买家发起预订'),
(10, 10, 'PENDING', 'ACCEPTED', 1010, 'USER', '卖家确认订单')
ON DUPLICATE KEY UPDATE to_status = VALUES(to_status), remark = VALUES(remark);

INSERT INTO reviews (id, order_id, reviewer_id, target_user_id, rating, content) VALUES
(1, 4, 2004, 1004, 5, '交易准时，商品描述准确。'),
(2, 4, 1004, 2004, 5, '买家沟通顺畅。'),
(3, 6, 2006, 1006, 4, '键盘状态不错。'),
(4, 6, 1006, 2006, 5, '付款及时。'),
(5, 1, 2001, 1001, 5, '教材保存很好。'),
(6, 2, 2002, 1002, 4, '验机顺利。'),
(7, 7, 2007, 1007, 5, '资料齐全。'),
(8, 8, 2008, 1008, 4, '桌子稳定。'),
(9, 9, 2009, 1009, 5, '吉他音色不错。'),
(10, 10, 2010, 1010, 4, '自行车车况正常。')
ON DUPLICATE KEY UPDATE rating = VALUES(rating), content = VALUES(content);

INSERT INTO chats (id, item_id, buyer_id, seller_id, last_message, last_message_at) VALUES
(1, 1, 2001, 1001, '今晚图书馆北门可以面交。', '2026-06-04 10:05:00'),
(2, 2, 2002, 1002, '可以现场验机。', '2026-06-04 10:25:00'),
(3, 3, 2003, 1003, '冰箱声音不大。', '2026-06-04 11:10:00'),
(4, 4, 2004, 1004, '容量可以放15寸电脑。', '2026-06-04 12:10:00'),
(5, 5, 2005, 1005, '暂时只卖不换。', '2026-06-04 12:30:00'),
(6, 6, 2006, 1006, '键盘可试用。', '2026-06-04 13:10:00'),
(7, 7, 2007, 1007, '有完整解析册。', '2026-06-04 13:30:00'),
(8, 8, 2008, 1008, '桌面无明显划痕。', '2026-06-04 14:10:00'),
(9, 9, 2009, 1009, '吉他有琴包。', '2026-06-04 15:05:00'),
(10, 10, 2010, 1010, '可以试骑。', '2026-06-04 16:05:00')
ON DUPLICATE KEY UPDATE last_message = VALUES(last_message), last_message_at = VALUES(last_message_at);

INSERT INTO chat_messages (id, chat_id, sender_id, message_type, content, image_url, item_id, filtered, created_at) VALUES
(1, 1, 2001, 'TEXT', '教材还在吗？', NULL, NULL, 0, '2026-06-04 10:01:00'),
(2, 1, 1001, 'TEXT', '还在，今晚图书馆北门可以面交。', NULL, NULL, 0, '2026-06-04 10:05:00'),
(3, 2, 2002, 'TEXT', 'iPad可以现场验机吗？', NULL, NULL, 0, '2026-06-04 10:20:00'),
(4, 2, 1002, 'TEXT', '可以现场验机。', NULL, NULL, 0, '2026-06-04 10:25:00'),
(5, 3, 2003, 'TEXT', '冰箱声音大吗？', NULL, NULL, 0, '2026-06-04 11:05:00'),
(6, 3, 1003, 'TEXT', '正常使用声音不大。', NULL, NULL, 0, '2026-06-04 11:10:00'),
(7, 4, 2004, 'ITEM_CARD', '发送商品卡片', NULL, 4, 0, '2026-06-04 12:05:00'),
(8, 5, 2005, 'TEXT', '可以用篮球置换吗？', NULL, NULL, 0, '2026-06-04 12:25:00'),
(9, 6, 2006, 'TEXT', '不要脱离平台交易。', NULL, NULL, 1, '2026-06-04 13:05:00'),
(10, 10, 1010, 'IMAGE', '自行车细节图', 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=640&q=80', NULL, 0, '2026-06-04 16:05:00')
ON DUPLICATE KEY UPDATE content = VALUES(content), filtered = VALUES(filtered);

INSERT INTO wanted_posts (id, user_id, title, description, category_id, campus, budget_min, budget_max, status) VALUES
(1, 2001, '求购二手自行车', '校内通勤用，车况正常即可。', 5, '校本部', 200.00, 350.00, 'OPEN'),
(2, 2002, '求购Java课程设计教材', '需要近两年版本。', 1, '东校区', 20.00, 40.00, 'OPEN'),
(3, 2003, '求购宿舍落地灯', '亮度可调优先。', 3, '西校区', 30.00, 60.00, 'OPEN'),
(4, 2004, '求购蓝牙耳机', '续航正常，无明显损坏。', 2, '南校区', 80.00, 200.00, 'OPEN'),
(5, 2005, '求购拉杆箱', '24寸左右。', 4, '大学城校区', 80.00, 180.00, 'OPEN'),
(6, 2006, '求购羽毛球拍', '初学者使用。', 5, '校本部', 50.00, 120.00, 'OPEN'),
(7, 2007, '求购考研政治资料', '肖四肖八优先。', 1, '东校区', 20.00, 50.00, 'OPEN'),
(8, 2008, '求购小电饭煲', '宿舍可用，容量小。', 3, '西校区', 50.00, 120.00, 'CLOSED'),
(9, 2009, '求购吉他变调夹', '民谣吉他用。', 6, '校本部', 10.00, 30.00, 'OPEN'),
(10, 2010, '求购电脑支架', '可折叠，适合宿舍桌。', 2, '南校区', 20.00, 60.00, 'OPEN')
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status);

INSERT INTO swap_requests (id, request_no, requester_id, target_item_id, offered_item_id, owner_id, status, message, handled_at, created_at) VALUES
(1, 'SW202606040001', 2001, 5, 1, 1005, 'PENDING', '想用高数教材加差价换羽毛球拍套装。', NULL, '2026-06-04 10:10:00'),
(2, 'SW202606040002', 2002, 1, 2, 1001, 'REJECTED', '想用 iPad 配件换高数教材。', '2026-06-04 11:30:00', '2026-06-04 10:30:00'),
(3, 'SW202606040003', 2003, 3, 8, 1003, 'PENDING', '用宿舍折叠小桌换小冰箱，补差价。', NULL, '2026-06-04 11:00:00'),
(4, 'SW202606040004', 2004, 4, 10, 1004, 'ACCEPTED', '用自行车换运动背包，线下面交。', '2026-06-04 12:40:00', '2026-06-04 12:00:00'),
(5, 'SW202606040005', 2005, 9, 5, 1009, 'PENDING', '用羽毛球拍换入门吉他，愿意补差价。', NULL, '2026-06-04 12:30:00'),
(6, 'SW202606040006', 2006, 6, 7, 1006, 'CANCELLED', '临时取消键盘置换申请。', '2026-06-04 14:00:00', '2026-06-04 13:10:00'),
(7, 'SW202606040007', 2007, 7, 1, 1007, 'PENDING', '用教材换考研英语真题套装。', NULL, '2026-06-04 13:40:00'),
(8, 'SW202606040008', 2008, 8, 3, 1008, 'ACCEPTED', '用宿舍小家电换折叠小桌。', '2026-06-04 15:20:00', '2026-06-04 14:20:00'),
(9, 'SW202606040009', 2009, 10, 9, 1010, 'REJECTED', '用吉他换自行车，卖家暂不接受。', '2026-06-04 16:00:00', '2026-06-04 15:00:00'),
(10, 'SW202606040010', 2010, 2, 10, 1002, 'PENDING', '用自行车加现金换 iPad。', NULL, '2026-06-04 16:30:00')
ON DUPLICATE KEY UPDATE status = VALUES(status), message = VALUES(message), handled_at = VALUES(handled_at);

INSERT INTO reports (id, reporter_id, target_type, target_id, report_type, content, status, handled_by, handled_at, result_remark) VALUES
(1, 2001, 'ITEM', 2, '虚假商品', '图片疑似网图，拒绝验机。', 'PENDING', NULL, NULL, NULL),
(2, 2002, 'USER', 1003, '欺诈', '要求脱离平台先转账。', 'PENDING', NULL, NULL, NULL),
(3, 2003, 'MESSAGE', 9, '敏感交易', '聊天中出现脱离平台交易提示。', 'APPROVED', 1, '2026-06-04 14:00:00', '已警告用户并记录扣分。'),
(4, 2004, 'ITEM', 3, '描述不符', '商品状态与描述不一致。', 'PENDING', NULL, NULL, NULL),
(5, 2005, 'ITEM', 5, '违禁内容', '疑似发布不合规物品。', 'REJECTED', 1, '2026-06-04 12:00:00', '证据不足。'),
(6, 2006, 'USER', 1006, '骚扰', '频繁发送无关消息。', 'PENDING', NULL, NULL, NULL),
(7, 2007, 'ITEM', 7, '虚假价格', '价格与详情描述不一致。', 'PENDING', NULL, NULL, NULL),
(8, 2008, 'MESSAGE', 8, '联系方式外泄', '聊天中引导私聊。', 'APPROVED', 1, '2026-06-04 16:00:00', '已隐藏违规消息。'),
(9, 2009, 'ITEM', 9, '图片不清晰', '无法确认商品状态。', 'REJECTED', 1, '2026-06-04 17:00:00', '不属于违规。'),
(10, 2010, 'USER', 1010, '迟到爽约', '多次约定不出现。', 'PENDING', NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE status = VALUES(status), result_remark = VALUES(result_remark);

INSERT INTO disputes (id, dispute_no, order_id, applicant_id, reason, evidence_urls, status, handled_by, handled_at, result_remark) VALUES
(1, 'DP202606040001', 3, 2003, '商品制冷效果与描述不一致。', '[]', 'PENDING', NULL, NULL, NULL),
(2, 'DP202606040002', 4, 2004, '交易地点临时变更产生争议。', '[]', 'PROCESSING', 1, NULL, '正在核对聊天记录。'),
(3, 'DP202606040003', 5, 2005, '取消订单后退款沟通不一致。', '[]', 'REJECTED', 1, '2026-06-04 15:00:00', '订单未付款，无需退款。'),
(4, 'DP202606040004', 6, 2006, '商品配件缺失。', '[]', 'REFUND_APPROVED', 1, '2026-06-04 16:00:00', '同意部分退款。'),
(5, 'DP202606040005', 7, 2007, '资料版本不符。', '[]', 'PENDING', NULL, NULL, NULL),
(6, 'DP202606040006', 8, 2008, '桌子有划痕。', '[]', 'PROCESSING', 1, NULL, '等待卖家补充说明。'),
(7, 'DP202606040007', 9, 2009, '吉他琴弦损坏。', '[]', 'PENDING', NULL, NULL, NULL),
(8, 'DP202606040008', 10, 2010, '自行车刹车问题。', '[]', 'PENDING', NULL, NULL, NULL),
(9, 'DP202606040009', 1, 2001, '面交时间冲突。', '[]', 'REJECTED', 1, '2026-06-04 17:30:00', '双方自行取消即可。'),
(10, 'DP202606040010', 2, 2002, '屏幕轻微划痕争议。', '[]', 'PROCESSING', 1, NULL, '等待验机图片。')
ON DUPLICATE KEY UPDATE status = VALUES(status), result_remark = VALUES(result_remark);

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
ON DUPLICATE KEY UPDATE enabled = VALUES(enabled);

INSERT INTO announcements (id, title, content, scope_type, campus, popup_enabled, status, published_at, created_by) VALUES
(1, '毕业季闲置交易安全提醒', '请优先选择同校区面交，贵重商品现场验机。', 'ALL', NULL, 1, 'PUBLISHED', '2026-06-01 09:00:00', 1),
(2, '东校区周末跳蚤市场开放通知', '东校区本周六开放线下闲置交换点。', 'CAMPUS', '东校区', 1, 'PUBLISHED', '2026-06-02 09:00:00', 1),
(3, '平台敏感词规则更新', '聊天中请勿引导私下转账或脱离平台交易。', 'ALL', NULL, 0, 'PUBLISHED', '2026-06-03 09:00:00', 1),
(4, '校本部图书资料专区上线', '教材教辅分类新增考研和公共课标签。', 'CAMPUS', '校本部', 0, 'PUBLISHED', '2026-06-03 10:00:00', 1),
(5, '西校区宿舍用品专场', '宿舍搬迁季生活用品集中推荐。', 'CAMPUS', '西校区', 0, 'DRAFT', NULL, 1),
(6, '南校区运动户外专场', '运动装备类商品推荐活动开启。', 'CAMPUS', '南校区', 0, 'PUBLISHED', '2026-06-03 12:00:00', 1),
(7, '大学城校区以物换物活动', '支持置换商品会展示专属标签。', 'CAMPUS', '大学城校区', 1, 'PUBLISHED', '2026-06-04 09:00:00', 1),
(8, '举报审核处理说明', '恶意举报会影响账号信用分。', 'ALL', NULL, 0, 'DRAFT', NULL, 1),
(9, '订单纠纷处理时效说明', '纠纷将在3个工作日内完成初审。', 'ALL', NULL, 0, 'PUBLISHED', '2026-06-04 10:00:00', 1),
(10, '校园卡支付测试通知', '校园卡支付参数正在测试，暂以面交为主。', 'ALL', NULL, 0, 'DRAFT', NULL, 1)
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), published_at = VALUES(published_at);

INSERT INTO notifications (id, user_id, type, title, content, read_at) VALUES
(1, 2001, 'ORDER', '订单已创建', '你已预订高等数学教材。', NULL),
(2, 2002, 'CHAT', '卖家回复你了', 'iPad卖家已同意现场验机。', NULL),
(3, 2003, 'DISPUTE', '纠纷已提交', '管理员将在3个工作日内处理。', NULL),
(4, 2004, 'SYSTEM', '交易安全提醒', '请勿脱离平台私下转账。', '2026-06-04 12:00:00'),
(5, 2005, 'ORDER', '订单已取消', '你的羽毛球拍订单已取消。', NULL),
(6, 2006, 'REVIEW', '待评价订单', '请对机械键盘交易进行评价。', NULL),
(7, 2007, 'ITEM', '关注分类新增商品', '教材教辅新增多件同校区商品。', NULL),
(8, 2008, 'REPORT', '举报处理结果', '你的举报已通过审核。', '2026-06-04 16:00:00'),
(9, 2009, 'CHAT', '新咨询消息', '有人咨询你的吉他商品。', NULL),
(10, 2010, 'ORDER', '卖家已确认订单', '自行车订单等待面交。', NULL)
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content), read_at = VALUES(read_at);

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
(10, 1010, 'IMAGE', 'bike.jpg', 'items/10/bike.jpg', 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=640&q=80', 360000, 'image/jpeg')
ON DUPLICATE KEY UPDATE url = VALUES(url), size_bytes = VALUES(size_bytes);

INSERT INTO audit_logs (id, admin_id, action, target_type, target_id, detail, ip) VALUES
(1, 1, 'LOGIN', 'ADMIN', 1, '管理员登录后台', '127.0.0.1'),
(2, 1, 'APPROVE_REPORT', 'REPORT', 3, '通过敏感交易举报', '127.0.0.1'),
(3, 1, 'REJECT_REPORT', 'REPORT', 5, '驳回证据不足举报', '127.0.0.1'),
(4, 1, 'RESOLVE_DISPUTE', 'DISPUTE', 4, '同意部分退款', '127.0.0.1'),
(5, 1, 'CREATE_NOTICE', 'ANNOUNCEMENT', 1, '发布毕业季安全提醒', '127.0.0.1'),
(6, 1, 'UPDATE_SETTING', 'SETTING', 1, '更新交易规则', '127.0.0.1'),
(7, 1, 'REMOVE_ITEM', 'ITEM', 3, '下架描述不符商品', '127.0.0.1'),
(8, 1, 'ENABLE_CATEGORY', 'CATEGORY', 1, '启用教材教辅分类', '127.0.0.1'),
(9, 1, 'UPDATE_WORD', 'SENSITIVE_WORD', 1, '维护敏感词库', '127.0.0.1'),
(10, 1, 'LOGOUT', 'ADMIN', 1, '管理员退出后台', '127.0.0.1')
ON DUPLICATE KEY UPDATE action = VALUES(action), detail = VALUES(detail);
