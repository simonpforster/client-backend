package models

import helpers.AbstractTest
import play.api.libs.json.{JsSuccess, JsValue, Json}


class ClientSpec extends AbstractTest {

	val testClient: Client = Client("testCrn", "testName", "testBusiness", "testContact", 12, "testPostcode", "testBusinessType", Some("testArn"))

	val testClientJs: JsValue = Json.parse(
		"""{
				"crn": "testCrn",
				"name": "testName",
				"businessName": "testBusiness",
				"contactNumber": "testContact",
				"propertyNumber": 12,
				"postcode": "testPostcode",
				"businessType": "testBusinessType",
				"arn": "testArn"
			}""".stripMargin)

	val testClientJsNone: JsValue = Json.parse(
		"""{
				"crn": "testCrn",
				"name": "testName",
				"businessName": "testBusiness",
				"contactNumber": "testContact",
				"propertyNumber": 12,
				"postcode": "testPostcode",
				"businessType": "testBusinessType"
			}""".stripMargin)

	"client" can {
		"format to json" should {
			"succeed with ARN" in {
				Json.toJson(testClient) shouldBe testClientJs
			}

			"succeed without ARN" in {
				Json.toJson(testClient.copy(arn = None)) shouldBe testClientJsNone
			}
		}

		"format from json" should {
			"succeed with ARN" in {
				Json.fromJson[Client](testClientJs) shouldBe JsSuccess(testClient)
			}

			"succeed without ARN" in {
				Json.fromJson[Client](testClientJsNone) shouldBe JsSuccess(testClient.copy(arn = None))
			}
		}
	}
}
