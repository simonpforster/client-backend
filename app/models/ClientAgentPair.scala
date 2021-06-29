package models

import play.api.libs.json.{Json, OFormat}

case class ClientAgentPair(crn: String, arn: String)

object ClientAgentPair {
	implicit val format: OFormat[ClientAgentPair] = Json.format[ClientAgentPair]
}