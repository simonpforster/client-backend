package models

import common.DBKeys
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, JsValue, Json}

class ClientRegSpec extends AnyWordSpec with Matchers {
  val testClientReg: ClientRegistration = ClientRegistration(
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = "12",
    postcode = "testPostcode",
    businessType = "testBusinessType",
    password = "testPassword")
  val testClientJs: JsValue = Json.parse(
    s"""{
        "${DBKeys.name}": "${testClientReg.name}",
				"${DBKeys.businessName}": "${testClientReg.businessName}",
				"${DBKeys.contactNumber}": "${testClientReg.contactNumber}",
				"${DBKeys.propertyNumber}": "${testClientReg.propertyNumber}",
				"${DBKeys.postcode}": "${testClientReg.postcode}",
				"${DBKeys.businessType}": "${testClientReg.businessType}",
        "${DBKeys.password}": "${testClientReg.password}"
      }""".stripMargin)

  "Client Registration" can {
    "format to json" should {
      "Convert Client Registration to json" in {
        Json.toJson(testClientReg) shouldBe testClientJs
      }
    }
    "format from json" should {
      "return a user" in {
        Json.fromJson[ClientRegistration](testClientJs) shouldBe JsSuccess(testClientReg)
      }
    }
  }
}
