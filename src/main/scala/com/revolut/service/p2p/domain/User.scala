package com.revolut.service.p2p.domain

import scalikejdbc._

case class User(id: Long, email: String, status: String, createdAt: Long)

object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"

  def apply(rs: WrappedResultSet) = new User(
    rs.long("id"),
    rs.string("email"),
    rs.string("status"),
    rs.long("created_at"))
}
