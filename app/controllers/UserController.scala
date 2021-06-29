package controllers

import play.api.mvc.{AbstractController, ControllerComponents}
import repositories.UserRepository

import javax.inject.{Inject, Singleton}

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userRepository: UserRepository)
  extends AbstractController(cc) {

}
