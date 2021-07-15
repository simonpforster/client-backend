package models

import play.api.libs.json.{Json, OFormat}

case class PropertyUpdateDetails(crn:String,
                                 propertyNumber:String,
                                 postcode:String
                                )

object PropertyUpdateDetails {
  implicit val format: OFormat[PropertyUpdateDetails] = Json.format[PropertyUpdateDetails]
}