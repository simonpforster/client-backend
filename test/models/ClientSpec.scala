package models

import common.DBKeys
import helpers.AbstractTest
import play.api.libs.json.{JsSuccess, JsValue, Json}

class ClientSpec extends AbstractTest {
  val testClient: Client = Client(
    crn = "testCrn",
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = "12",
    postcode = "testPostcode",
    businessType = "testBusinessType",
    arn = Some("testArn"))
  val testARN: String = "testArn"
  val testClientJs: JsValue = Json.parse(
    s"""{
				"${DBKeys.crn}": "${testClient.crn}",
				"${DBKeys.name}": "${testClient.name}",
				"${DBKeys.businessName}": "${testClient.businessName}",
				"${DBKeys.contactNumber}": "${testClient.contactNumber}",
				"${DBKeys.propertyNumber}": "${testClient.propertyNumber}",
				"${DBKeys.postcode}": "${testClient.postcode}",
				"${DBKeys.businessType}": "${testClient.businessType}",
				"${DBKeys.arn}": "$testARN"
			  }""".stripMargin)
  val testClientJsNone: JsValue = Json.parse(
   s"""{
				"${DBKeys.crn}": "${testClient.crn}",
				"${DBKeys.name}": "${testClient.name}",
				"${DBKeys.businessName}": "${testClient.businessName}",
				"${DBKeys.contactNumber}": "${testClient.contactNumber}",
				"${DBKeys.propertyNumber}": "${testClient.propertyNumber}",
				"${DBKeys.postcode}": "${testClient.postcode}",
				"${DBKeys.businessType}": "${testClient.businessType}"
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
