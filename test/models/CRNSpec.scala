package models

import common.DBKeys
import helpers.AbstractTest
import play.api.libs.json.{JsSuccess, JsValue, Json}


class CRNSpec extends AbstractTest {
  val testRN: CRN = CRN("testCrn")
  val testRNJs: JsValue = Json.parse(
    s"""{
				"${DBKeys.crn}": "${testRN.crn}"
			  }""".stripMargin)

  "client" can {
    "format to json" should {
      "succeed" in {
        Json.toJson(testRN) shouldBe testRNJs
      }
    }

    "format from json" should {
      "succeed" in {
        Json.fromJson[CRN](testRNJs) shouldBe JsSuccess(testRN)
      }
    }
  }
}
