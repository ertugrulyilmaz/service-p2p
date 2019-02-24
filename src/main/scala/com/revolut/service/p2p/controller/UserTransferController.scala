package com.revolut.service.p2p.controller

import com.revolut.service.p2p.service.UserTransferService._
import com.twitter.finatra.http.Controller
import scalikejdbc._

import scala.language.implicitConversions

case class UserTransferRequest(senderId: Long, receiverId: Long, amount: Double, currency: String, note: Option[String])

class UserTransferController (implicit s: DBSession = AutoSession) extends Controller {

  post("/user/transfer") { request: UserTransferRequest =>
    val senderId = request.senderId
    val receiverId = request.receiverId
    val amount = request.amount
    val currency = request.currency
    val note = request.note

    send(senderId, receiverId, amount, currency, note)
  }

}
