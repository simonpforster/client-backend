package models

import helpers.AbstractTest
import play.api.libs.json.{JsObject, Json}

class UserLoginSpec extends AbstractTest {
  val userLogin: UserLogin = UserLogin(
    crn = "MyCRN",
    password = "MyPass")

  val loginJson: JsObject = Json.obj(
    "crn" -> s"${userLogin.crn}",
    "password" -> s"${userLogin.password}"
  )

  "UserLogin" can {
    "format" should {
      "turn a UserLogin to readable Json" in {
        Json.toJson(userLogin)
      }
      "Turn readable json to a UserLogin" in {
        Json.fromJson[UserLogin](loginJson).get shouldBe userLogin
      }
    }
  }

}
