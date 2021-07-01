package models

import play.api.libs.json.{Json, OFormat}

case class UserLogin(crn: String,
                     password: String)

object UserLogin {
  implicit val format: OFormat[UserLogin] = Json.format[UserLogin]
}
