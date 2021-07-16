package models

import common.DBKeys
import helpers.AbstractTest
import play.api.libs.json.{JsObject, Json}

class PropertyUpdateSpec extends AbstractTest {
  val updatedDetails: PropertyUpdateDetails = PropertyUpdateDetails("CRNTEST", "newPropertyNumber", "newPostcode")
  val uDetailsJson: JsObject = Json.obj(
    s"${DBKeys.crn}" -> s"${updatedDetails.crn}",
    s"${DBKeys.propertyNumber}" -> s"${updatedDetails.propertyNumber}",
    s"${DBKeys.postcode}" -> s"${updatedDetails.postcode}"
  )
  "property update details" can {
    "format" should {
      "turn updated property details to readable Json" in {
        Json.toJson(updatedDetails) shouldBe uDetailsJson
      }
      "turn readable json to updated property details" in {
        Json.fromJson[PropertyUpdateDetails](uDetailsJson).get shouldBe updatedDetails
      }
    }
  }
}
