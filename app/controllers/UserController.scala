package controllers

import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import repositories.{ClientRepository, UserRepository}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UserController @Inject()(cc: ControllerComponents,
															 userRepository: UserRepository, ec: ExecutionContext)
	extends AbstractController(cc) {


	val read: Action[JsValue] = Action.async(parse.json) {

	}
}
