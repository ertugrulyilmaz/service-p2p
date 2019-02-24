package com.revolut.service.p2p.domain

import scalikejdbc._

case class UserTransfer(id: Long, sender: Long, receiverId: Long, amount: Double, currency: String, note: Option[String], createdAt: Long)

object UserTransfer extends SQLSyntaxSupport[UserTransfer] {
  override val tableName = "user_deposits"

  def apply(rs: WrappedResultSet) = new UserTransfer(
    rs.long("id"),
    rs.long("sender_id"),
    rs.long("receiver_id"),
    rs.double("amount"),
    rs.string("currency"),
    rs.stringOpt("note"),
    rs.long("created_at")
  )
}
