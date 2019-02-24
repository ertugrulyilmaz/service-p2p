package com.revolut.service.p2p.controller

import com.revolut.service.p2p.service.UserService._
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.RouteParam
import scalikejdbc._

import scala.language.implicitConversions

case class GetUserRequest(@RouteParam("id") id: Long)
case class PostUserRequest(email: String)

class UserController (implicit s: DBSession = AutoSession) extends Controller {

  get("/user/:id") { request: GetUserRequest =>
    findById(request.id)
  }

  post("/user") { request: PostUserRequest =>
    create(request.email)
  }

}
