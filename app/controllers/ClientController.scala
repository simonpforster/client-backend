package controllers

import models.{CRN, ClientAgentPair}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.ClientRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class ClientController @Inject()(cc: ControllerComponents,
																 clientRepository: ClientRepository, ec: ExecutionContext)
  extends AbstractController(cc) {

	val read: Action[JsValue] = Action.async(parse.json) { implicit request =>
		request.body.validate[CRN] match {
			case JsSuccess(value, _) =>
				clientRepository.read(value.crn).map{_ match {
					case Some(client) => Ok(Json.toJson(client))
					case None => NotFound
				}}
			case JsError(_) => Future(BadRequest)
		}
	}

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

	val deleteClient: Action[JsValue] = Action.async(parse.json) { implicit request =>
		request.body.validate[CRN] match {
			case JsSuccess(value, _) => clientRepository.delete(value.crn).map{_ match {
				case true =>  NoContent
				case false => NotFound
			}}
			case JsError(_) => Future.successful(BadRequest)
		}
	}
}