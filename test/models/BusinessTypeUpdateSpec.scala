package models

import common.DBKeys
import helpers.AbstractTest
import play.api.libs.json.{JsObject, Json}

class BusinessTypeUpdateSpec extends AbstractTest {
  val updatedDetails:BusinessTypeUpdateDetails = BusinessTypeUpdateDetails("CRNTEST", "TestType")
  val uDetailsJson: JsObject = Json.obj(
    s"${DBKeys.crn}"-> s"${updatedDetails.crn}",
    s"${DBKeys.businessType}" -> s"${updatedDetails.businessType}"
  )
  "Business type update details" can {
    "format" should {
      "turn updated business type to readable json" in {
        Json.toJson(updatedDetails) shouldBe uDetailsJson
      }
      "turn readable json to business type update details" in {
        Json.fromJson[BusinessTypeUpdateDetails](uDetailsJson).get shouldBe updatedDetails
      }
    }
  }
}
