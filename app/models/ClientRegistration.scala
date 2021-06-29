package models

import play.api.libs.json.{Json, OFormat}

case class ClientRegistration(name:String, businessName:String, contactNumber:String, propertyNumber:Int, postcode:String, businessType:String, password:String)

object ClientRegistration {
  implicit val format: OFormat[ClientRegistration] = Json.format[ClientRegistration]
}
