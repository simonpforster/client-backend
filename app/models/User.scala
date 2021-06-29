package models

import play.api.libs.json.{Json, OFormat}

case class User( crn: String,
                 password: EncryptedPassword)

object User{
  implicit val format: OFormat[User] = Json.format[User]
}