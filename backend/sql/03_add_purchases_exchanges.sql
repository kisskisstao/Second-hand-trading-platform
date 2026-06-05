USE second_hand_trade;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS purchases (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  category_id BIGINT,
  campus VARCHAR(64) NOT NULL,
  budget_min DECIMAL(10,2),
  budget_max DECIMAL(10,2),
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_purchases_user_id (user_id),
  INDEX idx_purchases_category_id (category_id),
  INDEX idx_purchases_campus (campus),
  INDEX idx_purchases_status (status),
  INDEX idx_purchases_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS exchanges (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exchange_no VARCHAR(64) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  target_item_id BIGINT,
  target_category_id BIGINT,
  expected_title VARCHAR(100),
  description TEXT,
  campus VARCHAR(64),
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_exchanges_user_id (user_id),
  INDEX idx_exchanges_item_id (item_id),
  INDEX idx_exchanges_target_item_id (target_item_id),
  INDEX idx_exchanges_target_category_id (target_category_id),
  INDEX idx_exchanges_status (status),
  INDEX idx_exchanges_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
