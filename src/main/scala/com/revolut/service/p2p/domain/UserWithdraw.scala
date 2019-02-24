package com.revolut.service.p2p.domain

import scalikejdbc._

case class UserWithdraw(id: Long, userId: Long, receiver: String, amount: Double, currency: String, trxId: Option[String], status: String, createdAt: Long, updatedAt: Option[Long])

object UserWithdraw extends SQLSyntaxSupport[UserWithdraw] {
  override val tableName = "user_withdraws"

  def apply(rs: WrappedResultSet) = new UserWithdraw(
    rs.long("id"),
    rs.long("user_id"),
    rs.string("receiver"),
    rs.double("amount"),
    rs.string("currency"),
    rs.stringOpt("trx_id"),
    rs.string("status"),
    rs.long("created_at"),
    rs.longOpt("updated_at")
  )
}
