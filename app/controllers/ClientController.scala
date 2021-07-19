package controllers

import models._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.{ClientRepository, UserRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class ClientController @Inject()(cc: ControllerComponents,
                                 clientRepository: ClientRepository,
                                 userRepository: UserRepository)
  extends AbstractController(cc) {

  def read(crn: String): Action[AnyContent] = Action.async { implicit request =>
    clientRepository.read(crn).map {
      case Some(client) => Ok(Json.toJson(client))
      case None => NotFound
    }.recover { case _ => BadRequest }
  }

  def readAllAgent(arn: String): Action[AnyContent] = Action.async { implicit request =>
    clientRepository.readAllAgent(arn).map { list => Ok(Json.toJson(list)) }
      .recover { case _ => BadRequest }
  }

  def addAgent(crn: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    Try {
      (request.body \ "arn").as[String]
    } match {
      case Success(arn) => clientRepository.addAgent(crn, arn).map {
        case (true, true) => NoContent
        case (false, true) => NotFound
        case _ => Conflict
      }
      case Failure(_) => Future(BadRequest)
    }

  }

  def removeAgent(crn: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    Try {
      (request.body \ "arn").as[String]
    } match {
      case Success(arn) => clientRepository.removeAgent(crn, arn).map {
        case (true, true) => NoContent
        case (false, true) => NotFound
        case _ => Conflict
      }
      case Failure(_) => Future(BadRequest)
    }
  }

  def deleteClient(crn: String): Action[AnyContent] = Action.async { implicit request =>
    clientRepository.delete(crn).flatMap {
      case true => userRepository.delete(crn).map {
        case true => NoContent
        case false => NotFound
      }
      case false => Future.successful(NotFound)
    }.recover { case _ => BadRequest }
  }

  def updateName(crn: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[NameUpdateDetails] match {
      case JsSuccess(nameUpdateDetails, _) =>
        clientRepository.updateName(crn, nameUpdateDetails.name).map {
          case true => NoContent
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def updateBusinessType(crn: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[BusinessTypeUpdateDetails] match {
      case JsSuccess(businessTypeUpdateDetails, _) =>
        clientRepository.updateBusinessType(crn, businessTypeUpdateDetails.businessType).map {
          case true => NoContent
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def updateContactNumber(crn: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ContactNumberUpdateDetails] match {
      case JsSuccess(contactNumberUpdateDetails, _) =>
        clientRepository.updateContactNumber(crn, contactNumberUpdateDetails.contactNumber).map {
          case true => NoContent
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def updateProperty(crn: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[PropertyUpdateDetails] match {
      case JsSuccess(propertyUpdateDetails, _) =>
        clientRepository.updateProperty(crn, propertyUpdateDetails.propertyNumber, propertyUpdateDetails.postcode).map {
          case true => NoContent
          case false => NotFound
        }
      case JsError(_) => Future(BadRequest)
    }
  }
}