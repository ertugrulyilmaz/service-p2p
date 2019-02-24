package com.revolut.service.p2p

import com.revolut.service.p2p.controller._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import scalikejdbc._

object Application extends ApplicationServer

class ApplicationServer(implicit s: DBSession = AutoSession) extends HttpServer {

  Class.forName("org.h2.Driver")

  override val disableAdminHttpServer: Boolean = true

  override protected def defaultHttpPort: String = ":8080"

  override protected def defaultHttpServerName: String = "Service-P2P"

  override def configureHttp(router: HttpRouter) {
    val settings = ConnectionPoolSettings(
      initialSize = 5,
      maxSize = 20,
      connectionTimeoutMillis = 3000L,
      validationQuery = "select 1 from dual")
    ConnectionPool.add('default, "jdbc:h2:mem:service-p2p", "user", "pass", settings)

    sql"CREATE TABLE IF NOT EXISTS users (id SERIAL NOT NULL PRIMARY KEY, email VARCHAR(64) NOT NULL, status VARCHAR(1) NOT NULL, created_at BIGINT NOT NULL);".execute.apply()
    sql"CREATE UNIQUE INDEX uniq_users_email ON users(email);".execute.apply()

    sql"CREATE TABLE IF NOT EXISTS user_balances (id SERIAL NOT NULL PRIMARY KEY, user_id BIGINT NOT NULL, available DECIMAL(19, 8) NOT NULL, reserved DECIMAL(19, 8) NOT NULL, currency VARCHAR(10) NOT NULL);".execute.apply()
    sql"CREATE INDEX IF NOT EXISTS idx_user_balances_user_id ON user_balances(user_id);".execute.apply()
    sql"CREATE UNIQUE INDEX IF NOT EXISTS uniq_user_balances_user_id_currency ON user_balances(user_id, currency);".execute.apply()

    sql"CREATE TABLE IF NOT EXISTS user_transfers (id SERIAL NOT NULL PRIMARY KEY, sender_id BIGINT NOT NULL, receiver_id BIGINT NOT NULL, amount DECIMAL(19, 8) NOT NULL, currency VARCHAR(10) NOT NULL, note VARCHAR(100) DEFAULT NULL, created_at BIGINT NOT NULL);".execute.apply()
    sql"CREATE INDEX IF NOT EXISTS idx_user_transfers_sender_id ON user_transfers(sender_id);".execute.apply()
    sql"CREATE INDEX IF NOT EXISTS idx_user_transfers_receiver_id ON user_transfers(receiver_id);".execute.apply()

    sql"CREATE TABLE IF NOT EXISTS user_deposits (id SERIAL NOT NULL PRIMARY KEY, user_id BIGINT NOT NULL, sender VARCHAR(100) NOT NULL, amount DECIMAL(19, 8) NOT NULL, currency VARCHAR(10) NOT NULL, trx_id VARCHAR(128) NOT NULL, status VARCHAR(10) NOT NULL, created_at BIGINT NOT NULL, updated_at BIGINT DEFAULT NULL);".execute.apply()
    sql"CREATE INDEX IF NOT EXISTS idx_user_deposits_user_id ON user_deposits(user_id);".execute.apply()
    sql"CREATE INDEX IF NOT EXISTS idx_user_deposits_sender ON user_deposits(sender);".execute.apply()

    sql"CREATE TABLE IF NOT EXISTS user_withdraws (id SERIAL NOT NULL PRIMARY KEY, user_id BIGINT NOT NULL, receiver VARCHAR(100) NOT NULL, amount DECIMAL(19, 8) NOT NULL, currency VARCHAR(10) NOT NULL, trx_id VARCHAR(128) DEFAULT NULL, status VARCHAR(10) NOT NULL, created_at BIGINT NOT NULL, updated_at BIGINT DEFAULT NULL);".execute.apply()
    sql"CREATE INDEX IF NOT EXISTS idx_user_withdraws_user_id ON user_withdraws(user_id);".execute.apply()
    sql"CREATE INDEX IF NOT EXISTS idx_user_withdraws_receiver ON user_withdraws(receiver);".execute.apply()

    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add(new UserController())
      .add(new UserBalanceController())
      .add(new UserDepositController())
      .add(new UserTransferController())
      .add(new UserWithdrawController())
  }

}
