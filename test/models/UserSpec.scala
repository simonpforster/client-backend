package models

import common.DBKeys
import helpers.AbstractTest
import play.api.libs.json.{JsValue, Json}

class UserSpec extends AbstractTest {
  val user: User = User(
    crn = "myCRN",
    password = EncryptedPassword(
      ePassword = "MyPass".getBytes,
      nonce = "MyNonce".getBytes))

  val userJson: JsValue = Json.parse(
    s"""{"${DBKeys.crn}": "${user.crn}",
       "${DBKeys.password}":{ "ePassword": ${user.password.ePassword.mkString("[", ", ", "]")},
       "nonce": ${user.password.nonce.mkString("[", ", ", "]")}}}""".stripMargin
  )

  "User" can {
    "format" should {
      "Turn a user to readable json" in {
        Json.toJson(user) shouldBe userJson
      }
      "Turn readable json to a user" in {
        val myJson = Json.fromJson[User](userJson).get
        myJson.crn shouldBe user.crn
        myJson.password.ePassword.map(_.toChar).mkString shouldBe user.password.ePassword.map(_.toChar).mkString
        myJson.password.nonce.map(_.toChar).mkString shouldBe user.password.nonce.map(_.toChar).mkString
      }
    }
  }
}
