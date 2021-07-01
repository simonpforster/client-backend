package models

import play.api.libs.json.{Json, OFormat}

case class EncryptedPassword(ePassword: Array[Byte],
                             nonce: Array[Byte])

object EncryptedPassword {
  implicit val format: OFormat[EncryptedPassword] = Json.format[EncryptedPassword]
}