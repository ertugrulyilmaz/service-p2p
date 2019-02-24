package com.revolut.service.p2p.domain

import scalikejdbc._

case class UserDeposit(id: Long, userId: Long, sender: String, amount: Double, currency: String, trxId: String, status: String, createdAt: Long, updatedAt: Option[Long])

object UserDeposit extends SQLSyntaxSupport[UserDeposit] {
  override val tableName = "user_deposits"

  def apply(rs: WrappedResultSet) =
    new UserDeposit(
      rs.long("id"),
      rs.long("user_id"),
      rs.string("sender"),
      rs.double("amount"),
      rs.string("currency"),
      rs.string("trx_id"),
      rs.string("status"),
      rs.long("created_at"),
      rs.longOpt("updated_at")
    )

}
