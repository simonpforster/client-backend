package models

import play.api.libs.json.Json

case class Client (crn: String, firstName: String, lastName: String, businessName: String, contactNumber: String, propertyNumber: Int, postCode: String, businessType: String, arn: Option[String] = None)

object Client {
	implicit val format = Json.format[Client]
}