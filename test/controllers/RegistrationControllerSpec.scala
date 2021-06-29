package controllers

import helpers.AbstractTest
import models.Client
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.{await, contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import service.RegistrationService
import play.api.http.Status._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationControllerSpec extends AbstractTest {

  val rs: RegistrationService = mock(classOf[RegistrationService])
  val controller: RegistrationController = new RegistrationController(Helpers.stubControllerComponents(),rs)
  val testClient: Client = new Client("testCrn", "testName", "testBusiness", "testContact", 12, "testPostcode", "testBusinessType", Some("testArn"))
  val fakePostRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")
  private val testBadJson = Json.obj(
    "monkey"-> "do"
  )
  val testUserJson = Json.obj(
    "name" -> testClient.name,
            "businessName" -> testClient.businessName,
            "contactNumber" -> testClient.contactNumber,
            "propertyNumber" -> testClient.propertyNumber,
            "postcode" -> testClient.postcode,
            "businessType" -> testClient.businessType,
            "password" -> "testPass"
  )
  "Registration Controller" can {
    "register" should {
      "return the registered user" in {
        when(rs.register(any())) thenReturn Future(Some(testClient))
        val result = controller.register.apply(fakePostRequest.withBody(testUserJson))
        contentAsJson(result) shouldBe Json.toJson(testClient)
      }
    }
    "return a Unauthorized" in {
      when(rs.register(any())) thenReturn Future(None)

      val result = controller.register.apply(fakePostRequest.withBody(testUserJson))

      status(result) shouldBe UNAUTHORIZED
    }

    "return a BadRequest" in {
      val result = controller.register.apply(fakePostRequest.withBody(testBadJson))

      status(result) shouldBe BAD_REQUEST
    }
  }

}
