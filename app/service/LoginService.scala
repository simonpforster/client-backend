package service

import controllers.UserController
import models.UserLogin
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import repositories.ClientRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginService @Inject()(cc: ControllerComponents,
                             userController: UserController,
                             clientRepo: ClientRepository)
  extends AbstractController(cc) {

  def login: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UserLogin] match {
      case JsSuccess(user, _) =>
        userController.checkMatches(user).flatMap {
          case true =>
            clientRepo.read(user.crn).map {
              case Some(client) => Ok(Json.toJson(client))
            }
          case false => Future(Unauthorized)
        }
      case JsError(_) => Future(BadRequest)
    }
  }
}
