package models

import play.api.libs.json.{Json, OFormat}

case class Client(crn: String,
                  name: String,
                  businessName: String,
                  contactNumber: String,
                  propertyNumber: Int,
                  postcode: String,
                  businessType: String,
                  arn: Option[String] = None)

object Client {
  implicit val format: OFormat[Client] = Json.format[Client]
}