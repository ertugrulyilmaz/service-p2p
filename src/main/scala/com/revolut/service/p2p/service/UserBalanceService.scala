package com.revolut.service.p2p.service

import com.revolut.service.p2p.domain.UserBalance
import scalikejdbc._

object UserBalanceService {

  def create(userId: Long, available: Double, currency: String)(implicit s: DBSession = AutoSession): Long = {
    sql"INSERT INTO user_balances (user_id, available, reserved, currency) VALUES (${userId}, ${available}, 0.0, ${currency})"
      .updateAndReturnGeneratedKey
      .apply()
  }

  def findById(id: Long)(implicit s: DBSession = AutoSession): Option[UserBalance] = {
    sql"SELECT id, user_id, available, reserved, currency FROM user_balances WHERE id = ${id}"
      .map { rs => UserBalance(rs) }.
      single()
      .apply()
  }

  def findByUserId(userId: Long)(implicit s: DBSession = AutoSession): List[UserBalance] = {
    sql"SELECT id, user_id, available, reserved, currency FROM user_balances WHERE user_id = ${userId}"
      .map { rs => UserBalance(rs) }.
      list
      .apply()
  }

  def findByUserIdAndCurrency(userId: Long, currency: String)(implicit s: DBSession = AutoSession): Option[UserBalance] = {
    sql"SELECT id, user_id, available, reserved, currency FROM user_balances WHERE user_id = ${userId} AND currency = ${currency}"
      .map { rs => UserBalance(rs) }.
      single()
      .apply()
  }

  def increaseAvailable(userId: Long, amount: Double, currency: String)(implicit s: DBSession = AutoSession): Option[UserBalance] = {
    val latestId = findByUserIdAndCurrency(userId, currency) match {
      case Some(UserBalance(id, _, available, _, _)) =>
        sql"UPDATE user_balances SET available = ${available + amount} WHERE id = ${id}".update.apply()

        id
      case None =>
        create(userId, amount, currency)
    }

    findById(latestId)
  }

  def decreaseAvailable(userId: Long, amount: Double, currency: String)(implicit s: DBSession = AutoSession): Option[UserBalance] = {
    findByUserIdAndCurrency(userId, currency) match {
      case Some(UserBalance(id, _, available, _, _)) =>
        sql"UPDATE user_balances SET available = ${available - amount} WHERE id = ${id}".update.apply()

        findById(id)
      case None =>
        Option.empty
    }
  }

  def decreaseReserve(userId: Long, amount: Double, currency: String)(implicit s: DBSession = AutoSession): Option[UserBalance] = {
    findByUserIdAndCurrency(userId, currency) match {
      case Some(UserBalance(id, _, _, reserved, _)) =>
        sql"UPDATE user_balances SET reserved = ${reserved - amount} WHERE id = ${id}".update.apply()

        findById(id)
      case None =>
        Option.empty
    }
  }

  def decreaseAvailableAndIncreaseReserve(userId: Long, amount: Double, currency: String)(implicit s: DBSession = AutoSession): Option[UserBalance] = {
    findByUserIdAndCurrency(userId, currency) match {
      case Some(UserBalance(id, _, available, reserved, _)) =>
        sql"UPDATE user_balances SET available = ${available - amount}, reserved = ${reserved + amount} WHERE id = ${id}".update.apply()

        findById(id)
      case None =>
        Option.empty
    }
  }

}

