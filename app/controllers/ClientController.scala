package controllers

import models.{ARN, CRN, Client, ClientAgentPair}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.{ClientRepository, UserRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ClientController @Inject()(cc: ControllerComponents,
                                 clientRepository: ClientRepository,
                                 userRepository: UserRepository)
  extends AbstractController(cc) {

  val read: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CRN] match {
      case JsSuccess(value, _) =>
        clientRepository.read(value.crn).map {
          case Some(client) => Ok(Json.toJson(client))
          case None => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  val readAllAgent: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ARN] match {
      case JsSuccess(value, _) => clientRepository.readAllAgent(value.arn).map { list => Ok(Json.toJson(list)) }
      case JsError(_) => Future(BadRequest)
    }
  }

  val addAgent: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ClientAgentPair] match {
      case JsSuccess(value, _) => clientRepository.addAgent(value.crn, value.arn).map {
        case (true, true) => NoContent
        case (false, true) => NotFound
        case _ => Conflict
      }
      case JsError(_) => Future.successful(BadRequest)
    }
  }

  val deleteClient: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CRN] match {
      case JsSuccess(value, _) => clientRepository.delete(value.crn).flatMap {
        case true => userRepository.delete(value.crn).map {
          case true => NoContent
          case false => NotFound
        }
        case false => Future.successful(NotFound)
      }
      case JsError(_) => Future.successful(BadRequest)
    }
  }

  val removeAgent: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ClientAgentPair] match {
      case JsSuccess(value, _) => clientRepository.removeAgent(value.crn, value.arn).map {
        case (true, true) => NoContent
        case (false, true) => NotFound
        case _ => Conflict
      }
      case JsError(_) => Future.successful(BadRequest)
    }
  }

  val update: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Client] match {
      case JsSuccess(client, _) =>
        clientRepository.update(client).map {
          case true => Created
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }
}