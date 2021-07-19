package models

import play.api.libs.json.{Json, OFormat}

case class NameUpdateDetails(name: String)

object NameUpdateDetails {
  implicit val format: OFormat[NameUpdateDetails] = Json.format[NameUpdateDetails]
}

case class ContactNumberUpdateDetails(contactNumber: String)

object ContactNumberUpdateDetails {
  implicit val format: OFormat[ContactNumberUpdateDetails] = Json.format[ContactNumberUpdateDetails]
}

case class BusinessTypeUpdateDetails(businessType: String)

object BusinessTypeUpdateDetails {
  implicit val format: OFormat[BusinessTypeUpdateDetails] = Json.format[BusinessTypeUpdateDetails]
}

case class PropertyUpdateDetails(propertyNumber:String,
                                 postcode:String)

object PropertyUpdateDetails {
  implicit val format: OFormat[PropertyUpdateDetails] = Json.format[PropertyUpdateDetails]
}

