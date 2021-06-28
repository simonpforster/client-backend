package controllers

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class BasicController @Inject()(cc: ControllerComponents,
                                ec: ExecutionContext)
  extends AbstractController(cc) {



}