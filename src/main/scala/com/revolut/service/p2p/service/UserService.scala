package com.revolut.service.p2p.service

import java.util.Date

import com.revolut.service.p2p.domain.User
import scalikejdbc._

object UserService {

  def create(email: String)(implicit s: DBSession = AutoSession): Option[User] = {
    val status = "A"
    val createdAt = new Date().getTime

    val id = sql"INSERT INTO users (email, status, created_at) VALUES (${email}, ${status}, ${createdAt})"
      .updateAndReturnGeneratedKey
      .apply()

    findById(id)
  }

  def findById(id: Long)(implicit s: DBSession = AutoSession): Option[User] = {
    sql"SELECT id, email, status, created_at FROM users WHERE id = ${id}"
      .map { rs => User(rs) }.single.apply()
  }

}

