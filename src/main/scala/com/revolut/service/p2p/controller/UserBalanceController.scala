package com.revolut.service.p2p.controller

import com.revolut.service.p2p.service.UserBalanceService._
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.{QueryParam, RouteParam}
import scalikejdbc._

import scala.language.implicitConversions

case class GetUserBalanceRequest(@RouteParam("userId") userId: Long, @QueryParam("currency") currency: Option[String])

class UserBalanceController (implicit s: DBSession = AutoSession) extends Controller {

  get("/user/:userId/balance") { request: GetUserBalanceRequest =>
    request match {
      case GetUserBalanceRequest(userId, Some(currency)) => findByUserIdAndCurrency(userId, currency)
      case GetUserBalanceRequest(userId, None) => findByUserId(userId)
    }
  }

}
