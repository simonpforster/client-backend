package models

import com.mongodb.DBRef
import common.DBKeys
import helpers.AbstractTest
import play.api.libs.json.{JsSuccess, JsValue, Json}


class ARNSpec extends AbstractTest {

	val testARN: ARN = ARN("testArn")

	val testRNJs: JsValue = Json.parse(
		s"""{
				"${DBKeys.arn}": "${testARN.arn}"
			}""".stripMargin)

	"client" can {
		"format to json" should {
			"succeed" in {
				Json.toJson(testARN) shouldBe testRNJs
			}
		}

		"format from json" should {
			"succeed" in {
				Json.fromJson[ARN](testRNJs) shouldBe JsSuccess(testARN)
			}
		}
	}
}
