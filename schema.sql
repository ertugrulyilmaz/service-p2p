CREATE TABLE IF NOT EXISTS users (
  id            SERIAL          NOT NULL PRIMARY KEY,
  email         VARCHAR(64)     NOT NULL,
  status        VARCHAR(1)      NOT NULL, -- P: Passive, A: Active, D: Deleted
  created_at    BIGINT          NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uniq_users_email ON users(email);

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS user_balances (
  id            SERIAL          NOT NULL PRIMARY KEY,
  user_id       BIGINT          NOT NULL,
  available     DECIMAL(19, 8)  NOT NULL,
  reserved      DECIMAL(19, 8)  NOT NULL,
  currency      VARCHAR(10)     NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_balances_user_id ON user_balances(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS uniq_user_balances_user_id_currency ON user_balances(user_id, currency);

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS user_transfers (
  id            SERIAL          NOT NULL PRIMARY KEY,
  sender_id     BIGINT          NOT NULL,
  receiver_id   BIGINT          NOT NULL,
  amount        DECIMAL(19, 8)  NOT NULL,
  currency      VARCHAR(10)     NOT NULL,
  note          VARCHAR(100)    DEFAULT NULL,
  created_at    BIGINT          NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_transfers_sender_id ON user_transfers(sender_id);
CREATE INDEX IF NOT EXISTS idx_user_transfers_receiver_id ON user_transfers(receiver_id);

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS user_deposits (
  id            SERIAL          NOT NULL PRIMARY KEY,
  user_id       BIGINT          NOT NULL,
  sender        VARCHAR(100)    NOT NULL,
  amount        DECIMAL(19, 8)  NOT NULL,
  currency      VARCHAR(10)     NOT NULL,
  trx_id        VARCHAR(128)    NOT NULL,
  status        VARCHAR(10)     NOT NULL,
  created_at    BIGINT          NOT NULL,
  updated_at    BIGINT          DEFAULT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_deposits_user_id ON user_deposits(user_id);
CREATE INDEX IF NOT EXISTS idx_user_deposits_sender ON user_deposits(sender);

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS user_withdraws (
  id            SERIAL          NOT NULL PRIMARY KEY,
  user_id       BIGINT          NOT NULL,
  receiver      VARCHAR(100)    NOT NULL,
  amount        DECIMAL(19, 8)  NOT NULL,
  currency      VARCHAR(10)     NOT NULL,
  trx_id        VARCHAR(128)    NOT NULL,
  status        VARCHAR(10)     NOT NULL,
  created_at    BIGINT          NOT NULL,
  updated_at    BIGINT          DEFAULT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_withdraws_user_id ON user_withdraws(user_id);
CREATE INDEX IF NOT EXISTS idx_user_withdraws_receiver ON user_withdraws(receiver);
