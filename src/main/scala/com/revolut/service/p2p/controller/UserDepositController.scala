package com.revolut.service.p2p.controller

import com.revolut.service.p2p.service.UserDepositService._
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.RouteParam
import scalikejdbc._

import scala.language.implicitConversions

case class UserDepositRequest(@RouteParam("userId") userId: Long, sender: String, amount: Double, currency: String, trxId: String)
case class UserDepositCompleteRequest(@RouteParam("userId") userId: Long, @RouteParam("id") id: Long)

class UserDepositController (implicit s: DBSession = AutoSession) extends Controller {

  post("/user/:userId/deposit") { request: UserDepositRequest =>
    val userId = request.userId
    val sender = request.sender
    val amount = request.amount
    val currency = request.currency
    val trxId = request.trxId

    deposit(userId, sender, amount, currency, trxId)
  }

  put("/user/:userId/deposit/:id/complete") { request: UserDepositCompleteRequest =>
    complete(request.userId, request.id)
  }

}
