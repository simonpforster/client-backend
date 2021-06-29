package controllers

import models.CRN
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import repositories.UserRepository

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(cc: ControllerComponents,
															 userRepository: UserRepository, ec: ExecutionContext)
	extends AbstractController(cc) {

	// NOT USED NO ROUTES
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
}
