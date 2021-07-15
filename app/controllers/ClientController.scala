package controllers

import models.{ARN, BusinessTypeUpdateDetails, CRN, ClientAgentPair, ContactNumberUpdateDetails, NameUpdateDetails, PropertyUpdateDetails}
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

  val updateName: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[NameUpdateDetails] match {
      case JsSuccess(nameUpdateDetails, _) =>
        clientRepository.updateName(nameUpdateDetails).map {
          case true => NoContent
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  val updateBusinessType: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[BusinessTypeUpdateDetails] match {
      case JsSuccess(businessTypeUpdateDetails, _) =>
        clientRepository.updateBusinessType(businessTypeUpdateDetails).map {
          case true => NoContent
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }

val updateContactNumber = Action.async(parse.json) { implicit request =>
    request.body.validate[ContactNumberUpdateDetails] match {
      case JsSuccess(value, _) =>
        clientRepository.updateContactNumber(value).map {
          case true => NoContent
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }
    
  val updateProperty: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[PropertyUpdateDetails] match {
      case JsSuccess(propertyUpdateDetails, _) =>
        clientRepository.updateProperty(propertyUpdateDetails).map {
          case true => NoContent
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }
}