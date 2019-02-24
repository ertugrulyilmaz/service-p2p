package com.revolut.service.p2p.controller

import com.revolut.service.p2p.service.UserWithdrawService._
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.RouteParam
import scalikejdbc._

import scala.language.implicitConversions

case class UserWithdrawRequest(@RouteParam("userId") userId: Long, receiver: String, amount: Double, currency: String)
case class UserWithdrawCompleteRequest(@RouteParam("userId") userId: Long, @RouteParam("id") id: Long, trxId: String)

class UserWithdrawController (implicit s: DBSession = AutoSession) extends Controller {

  post("/user/:userId/withdraw") { request: UserWithdrawRequest =>
    val userId = request.userId
    val receiver = request.receiver
    val amount = request.amount
    val currency = request.currency

    withdraw(userId, receiver, amount, currency)
  }

  put("/user/:userId/withdraw/:id/complete") { request: UserWithdrawCompleteRequest =>
    complete(request.userId, request.id, request.trxId)
  }

}
