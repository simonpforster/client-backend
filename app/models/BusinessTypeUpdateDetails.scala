package models

import play.api.libs.json.{Json, OFormat}

case class BusinessTypeUpdateDetails(crn: String, businessType: String)

object BusinessTypeUpdateDetails {
  implicit val format: OFormat[BusinessTypeUpdateDetails] = Json.format[BusinessTypeUpdateDetails]
}
