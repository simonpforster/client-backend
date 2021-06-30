package models

import helpers.AbstractTest
import play.api.libs.json.{JsSuccess, JsValue, Json}


class ARNSpec extends AbstractTest {

	val testRN: ARN = ARN("testArn")

	val testRNJs: JsValue = Json.parse(
		"""{
				"arn": "testArn"
			}""".stripMargin)

	"client" can {
		"format to json" should {
			"succeed" in {
				Json.toJson(testRN) shouldBe testRNJs
			}
		}

		"format from json" should {
			"succeed" in {
				Json.fromJson[ARN](testRNJs) shouldBe JsSuccess(testRN)
			}
		}
	}
}
