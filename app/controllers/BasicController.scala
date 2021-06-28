package controllers
import akka.util.Helpers.Requiring
import models.Vehicle
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.Results.Ok

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, Request}
import repositories.DataRepository

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success


@Singleton
class BasicController @Inject()(cc: ControllerComponents,
                                dataRepository: DataRepository, ec: ExecutionContext)
  extends AbstractController(cc) {


  def index = Action { implicit request =>
    Ok("Hello, Scala!")
  }

//  def getOneVehicleName(vehicleName: String) = Action { implicit request: Request[AnyContent] =>
//
//    val vehicle = dataRepository.getVehicle(vehicleName)
//     vehicle match {
//      case Some(Vehicle(wheels,heavy,name)) => Ok(Json.toJson(vehicle.get))
//      case _ =>  NotFound
//    }
//
//  }
//
  def receiveForm() = Action.async { implicit request: Request[AnyContent] =>
    val jsonReceived = request.body.asJson

    val vehicleNameFromJsonReceived = jsonReceived match {
      case Some(value) => jsonReceived.get.\("Vehicle Name").as[String]
      case None => ""
    }

    dataRepository.getVehicle(vehicleNameFromJsonReceived).map(items =>{
      Ok(Json.toJson(items.head))}) recover {
      case _ => InternalServerError(Json.obj(
        "message" -> "Error reading item from Mongo"
      ))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Vehicle] match {
      case JsSuccess(vehicle, _) =>
        dataRepository.create(vehicle).map(_ => Created)
      case JsError(_) => Future(BadRequest)
    }
  }


}