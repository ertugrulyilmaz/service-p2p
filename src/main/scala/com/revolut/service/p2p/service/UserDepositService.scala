package com.revolut.service.p2p.service

import java.util.Date

import com.revolut.service.p2p.domain.UserDeposit
import com.revolut.service.p2p.service.UserBalanceService._
import com.twitter.util.logging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc._

object UserDepositService {

  private[this] final lazy val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  def deposit(userId: Long, sender: String, amount: Double, currency: String, trxId: String)
             (implicit s: DBSession = AutoSession): Option[UserDeposit] = {
    val createdAt = new Date().getTime
    val status = "PENDING"

    val id =
      sql"""INSERT INTO user_deposits(user_id, sender, amount, currency, trx_id, status, created_at)
           VALUES(${userId}, ${sender}, ${amount}, ${currency}, ${trxId}, ${status}, ${createdAt})"""
        .updateAndReturnGeneratedKey
        .apply()

    findById(id, userId)
  }

  def findById(id: Long, userId: Long)(implicit s: DBSession = AutoSession): Option[UserDeposit] = {
    sql"SELECT id, user_id, sender, amount, currency, trx_id, status, created_at, updated_at FROM user_deposits WHERE id = ${id} AND user_id = ${userId}"
      .map { rs => UserDeposit(rs) }.single.apply()
  }

  def updateCompleted(id: Long, userId: Long)(implicit s: DBSession = AutoSession): Unit = {
    sql"UPDATE user_deposits SET status = 'COMPLETED' WHERE id = ${id} AND user_id = ${userId}".update.apply()
  }

  def complete(userId: Long, id: Long)(implicit s: DBSession = AutoSession): Option[UserDeposit] = {
    findById(id, userId) match {
      case Some(UserDeposit(_, _, _, amount, currency, _, status, _, _)) if status == "PENDING" =>
        using(ConnectionPool('default).borrow()) { conn: java.sql.Connection =>
          val db = DB(conn)

          try {
            db.begin()
            db withinTx { implicit session =>
              updateCompleted(id, userId)(session)

              increaseAvailable(userId, amount, currency)(session)

              db.commit()

              findById(id, userId)(session)
            }
          } catch {
            case e: Exception =>
              logger.error(s"${e}")

              db.rollbackIfActive()

              Option.empty
          } finally {
            db.close()
          }
        }
      case Some(userDeposit) if userDeposit.status == "COMPLETED" =>
        Option(userDeposit)
      case None => Option.empty
        throw new Exception("User does not exists")
    }
  }

}
