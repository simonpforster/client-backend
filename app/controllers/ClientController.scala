package controllers

import models.ClientAgentPair
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import play.api.mvc._
import repositories.ClientRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class ClientController @Inject()(cc: ControllerComponents,
																 clientRepository: ClientRepository, ec: ExecutionContext)
  extends AbstractController(cc) {

	val addAgent: Action[JsValue] = Action.async(parse.json) { implicit request =>
		request.body.validate[ClientAgentPair] match {
			case JsSuccess(value, _) => clientRepository.addAgent(value.crn, value.arn).map{_ match {
				case (true, true) => NoContent
				case (false, true) => NotFound
				case (true, false) => Conflict
				case _ => InternalServerError // impossible
			}}
			case JsError(_) => Future.successful(BadRequest)
		}
	}

	val read: Action[JsValue] = Action.async(parse.json) { implicit request =>

	}


}