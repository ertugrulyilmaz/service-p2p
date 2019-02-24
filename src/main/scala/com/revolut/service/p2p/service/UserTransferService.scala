package com.revolut.service.p2p.service

import java.util.Date

import com.twitter.util.logging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc._
import com.revolut.service.p2p.domain.{UserBalance, UserTransfer}

object UserTransferService {

  private[this] final lazy val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  def create(senderId: Long, receiverId: Long, amount: Double, currency: String, note: Option[String])
            (implicit s: DBSession = AutoSession): Long = {
    val createdAt = new Date().getTime

    sql"INSERT INTO user_transfers (sender_id, receiver_id, amount, currency, note, created_at) VALUES (${senderId}, ${receiverId}, ${amount}, ${currency}, ${note}, ${createdAt})".updateAndReturnGeneratedKey.apply()
  }

  def findById(id: Long)(implicit s: DBSession = AutoSession): Option[UserTransfer] = {
    sql"SELECT id, sender_id, receiver_id, amount, currency, note, created_at FROM user_transfers WHERE id = ${id}"
      .map { rs => UserTransfer(rs) }.single.apply()
  }

  def send(senderId: Long, receiverId: Long, amount: Double, currency: String, note: Option[String])
          (implicit s: DBSession = AutoSession): Option[UserTransfer] = {
    UserBalanceService.findByUserIdAndCurrency(senderId, currency) match {
      case Some(UserBalance(_, _, available, _, _)) if available >= amount =>
        using(ConnectionPool('default).borrow()) { conn: java.sql.Connection =>
          val db = DB(conn)

          try {
            db.begin()

            db withinTx { implicit session =>
              val id = create(senderId, receiverId, amount, currency, note)(session)

              UserBalanceService.decreaseAvailable(senderId, amount, currency)(session)

              UserBalanceService.increaseAvailable(receiverId, amount, currency)(session)

              db.commit()

              findById(id)(session)
            }
          } catch {
            case e: Exception =>
              db.rollbackIfActive()

              Option.empty
          } finally {
            db.close()
          }
        }
      case Some(UserBalance(_, _, available, _, _)) if available < amount =>
        throw new Exception("Sender does not have enough balance")
      case None => Option.empty
        throw new Exception("Sender does not exists")
    }
  }

}
