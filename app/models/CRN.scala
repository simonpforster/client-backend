package models

import play.api.libs.json.{Json, OFormat}

case class CRN(crn: String)

object CRN {
  implicit val format: OFormat[CRN] = Json.format[CRN]
}