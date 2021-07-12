package models

import common.DBKeys
import helpers.AbstractTest
import play.api.libs.json.{JsObject, Json}

class NameUpdateSpec extends AbstractTest {
  val updatedDetails: NameUpdateDetails = NameUpdateDetails("CRNTEST", "newName")
  val uDetailsJson: JsObject = Json.obj(
    s"${DBKeys.crn}" -> s"${updatedDetails.crn}",
    s"${DBKeys.name}" -> s"${updatedDetails.name}"
  )
  "Name update details" can {
    "format" should {
      "turn updated name details to readable Json" in {
        Json.toJson(updatedDetails) shouldBe uDetailsJson
      }
      "turn readable json to update name details" in {
        Json.fromJson[NameUpdateDetails](uDetailsJson).get shouldBe updatedDetails
      }
    }
  }
}
