package models

import play.api.libs.json.{Json, OFormat}

case class NameUpdateDetails(crn: String,
                             name: String)

object NameUpdateDetails {
  implicit val format: OFormat[NameUpdateDetails] = Json.format[NameUpdateDetails]
}
