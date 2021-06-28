package models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsValue, Json}
import play.api.test.Injecting

import scala.language.postfixOps

class ClientSpec extends AnyWordSpec with GuiceOneAppPerTest with Injecting with Matchers {

	val testClient: Client = Client("testCrn", "testFirst", "testLast", "testBusiness", "testContact", 12, "testPostCode", "testBusinessType", Some("testArn"))

	val testClientJs: JsValue = Json.parse(
		"""{
				"crn": "testCrn",
				"firstName": "testFirst",
				"lastName": "testLast",
				"businessName": "testBusiness",
				"contactNumber": "testContact",
				"propertyNumber": 12,
				"postCode": "testPostCode",
				"businessType": "testBusinessType",
				"arn": "testArn"
			}""".stripMargin)

	val testClientJsNone: JsValue = Json.parse(
		"""{
				"crn": "testCrn",
				"firstName": "testFirst",
				"lastName": "testLast",
				"businessName": "testBusiness",
				"contactNumber": "testContact",
				"propertyNumber": 12,
				"postCode": "testPostCode",
				"businessType": "testBusinessType"
			}""".stripMargin)

	"client" can {
		"format" should {
			"be match" in {
				Json.toJson(testClient) shouldBe testClientJs
			}

			"not match" in {
				Json.toJson(testClient.copy(arn = None)) shouldBe testClientJsNone
			}
		}
	}
}
