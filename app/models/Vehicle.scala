package models


import play.api.libs.json.Json

case class Vehicle( wheels: Int,
                    heavy: Boolean,
                    name: String)


object Vehicle{
  implicit val format = Json.format[Vehicle]
}