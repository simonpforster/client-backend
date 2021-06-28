package models

import helpers.AbstractTest
import play.api.libs.json.{JsSuccess, JsValue, Json}

class UserSpec extends AbstractTest {
  val user: User = User("myCRN", "MyPass")
  val userJson: JsValue = Json.parse(
    s"""{"crn": "${user.crn}", "password": "${user.password}"}""".stripMargin
  )
  "User" can {
    "format" should {
      "Turn a user to readable json" in {
        Json.toJson(user) shouldBe userJson
      }
      "Turn readable json to a user" in {
        Json.fromJson[User](userJson) shouldBe JsSuccess(user)
      }
    }
  }
}
