package controllers

import models.{CRN, UserLogin}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import repositories.UserRepository
import service.EncryptionService

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserController @Inject()(cc: ControllerComponents,
                               userRepository: UserRepository, crypto: EncryptionService)
  extends AbstractController(cc) {

  val read: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CRN] match {
      case JsSuccess(value, _) =>
        userRepository.read(value.crn).map {
          case Some(user) => Ok(Json.toJson(user))
          case None => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
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
