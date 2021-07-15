package models

import play.api.libs.json.{Json, OFormat}

case class NameUpdateDetails(crn: String,
                             name: String)

object NameUpdateDetails {
  implicit val format: OFormat[NameUpdateDetails] = Json.format[NameUpdateDetails]
}


case class ContactNumberUpdateDetails(crn: String,
                                      contactNumber: String)

object ContactNumberUpdateDetails {
  implicit val format: OFormat[ContactNumberUpdateDetails] = Json.format[ContactNumberUpdateDetails]
}
