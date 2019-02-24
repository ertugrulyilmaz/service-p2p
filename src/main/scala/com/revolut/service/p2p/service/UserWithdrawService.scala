package com.revolut.service.p2p.service

import java.util.Date

import com.revolut.service.p2p.domain.UserWithdraw
import com.twitter.util.logging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc._

object UserWithdrawService {

  private[this] final lazy val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  def create(userId: Long, receiver: String, amount: Double, currency: String)
            (implicit s: DBSession = AutoSession): Long = {
    val createdAt = new Date().getTime
    val status = "PENDING"

    sql"INSERT INTO user_withdraws(user_id, receiver, amount, currency, status, created_at) VALUES(${userId}, ${receiver}, ${amount}, ${currency}, ${status}, ${createdAt})".updateAndReturnGeneratedKey.apply()
  }

  def findById(id: Long, userId: Long)(implicit s: DBSession = AutoSession): Option[UserWithdraw] = {
    sql"SELECT id, user_id, receiver, amount, currency, trx_id, status, created_at, updated_at FROM user_withdraws WHERE id = ${id} AND user_id = ${userId}"
      .map { rs => UserWithdraw(rs) }.single.apply()
  }

  def withdraw(userId: Long, receiver: String, amount: Double, currency: String)
              (implicit s: DBSession = AutoSession): Option[UserWithdraw] = {
    using(ConnectionPool('default).borrow()) { conn: java.sql.Connection =>
      val db = DB(conn)

      try {
        db.begin()

        db withinTx { implicit session =>
          val id = create(userId, receiver, amount, currency)(session)

          UserBalanceService.decreaseAvailableAndIncreaseReserve(userId, amount, currency)(session)

          db.commit()

          findById(id, userId)(session)
        }
      } catch {
        case e: Exception =>
          db.rollbackIfActive()

          Option.empty
      } finally {
        db.close()
      }
    }
  }

  def updateCompleted(id: Long, userId: Long, trxId: String)(implicit s: DBSession = AutoSession): Unit = {
    sql"UPDATE user_withdraws SET status = 'COMPLETED', trx_id = ${trxId} WHERE id = ${id} AND user_id = ${userId}".update.apply()
  }

  def complete(userId: Long, id: Long, trxId: String)(implicit s: DBSession = AutoSession): Option[UserWithdraw] = {
    findById(id, userId) match {
      case Some(UserWithdraw(_, _, _, amount, currency, _, status, _, _)) if status == "PENDING" =>
        using(ConnectionPool('default).borrow()) { conn: java.sql.Connection =>
          val db = DB(conn)

          try {
            db.begin()

            db withinTx { implicit session =>
              updateCompleted(id, userId, trxId)(session)

              UserBalanceService.decreaseReserve(userId, amount, currency)(session)

              db.commit()

              findById(id, userId)(session)
            }
          } catch {
            case e: Exception =>
              db.rollbackIfActive()

              Option.empty
          } finally {
            db.close()
          }
        }
      case Some(userWithdraw) if userWithdraw.status == "COMPLETED" =>
        Option(userWithdraw)
      case None => Option.empty
        throw new Exception("User does not exists")
    }
  }

}
