package controllers

import models.ClientRegistration
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import service.RegistrationService
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class RegistrationController @Inject()(cc: ControllerComponents,
                                       rs: RegistrationService)
  extends AbstractController(cc) {
  def register: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ClientRegistration] match {
      case JsSuccess(client, _) =>
        rs.register(client).map {
          case Some(client) => Ok(Json.toJson(client))
          case _ => Unauthorized
        }
      case JsError(_) => Future(BadRequest)
    }
  }
}
