package controllers


import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import models._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import repositories.UserRepository
import service.EncryptionService

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserController @Inject()(cc: ControllerComponents,
                               userRepository: UserRepository, crypto: EncryptionService)
  extends AbstractController(cc) {

  def read(crn: String): Action[AnyContent] = Action.async { implicit request =>
    userRepository.read(crn).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }.recover{case _ => BadRequest}
  }

  def checkMatches(requestedUser: UserLogin): Future[Boolean] = {
    userRepository.read(requestedUser.crn).map {
      case Some(user) => if (user.crn == requestedUser.crn) {
        val userPass: String = crypto.decrypt(user.password.ePassword, crypto.getKey, user.password.nonce)
        userPass == requestedUser.password
      } else false
      case _ => false
    }
  }
}
