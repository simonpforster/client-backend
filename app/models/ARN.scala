package models

import play.api.libs.json.{Json, OFormat}

case class ARN(arn: String)

object ARN {
	implicit val format: OFormat[ARN] = Json.format[ARN]
}