package com.revolut.service.p2p.domain

import scalikejdbc._

case class UserBalance(id: Long, userId: Long, available: Double, reserved: Double, currency: String)

object UserBalance extends SQLSyntaxSupport[UserBalance] {
  override val tableName = "user_balances"

  def apply(rs: WrappedResultSet) = new UserBalance(
    rs.long("id"),
    rs.long("user_id"),
    rs.double("available"),
    rs.double("reserved"),
    rs.string("currency"))
}
