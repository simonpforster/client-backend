package controllers
import play.api.mvc._
import repositories.ClientRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext


@Singleton
class BasicController @Inject()(cc: ControllerComponents,
                                clientRepository: ClientRepository, ec: ExecutionContext)
  extends AbstractController(cc) {



}