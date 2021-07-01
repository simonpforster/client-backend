package controllers

import helpers.AbstractTest
import models.Client
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import service.RegistrationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationControllerSpec extends AbstractTest {
  val rs: RegistrationService = mock(classOf[RegistrationService])
  val controller: RegistrationController = new RegistrationController(Helpers.stubControllerComponents(), rs)
  val fakePostRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")
  val testClient: Client = Client(
    crn = "testCrn",
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = 12,
    postcode = "testPostcode",
    businessType = "testBusinessType",
    arn = Some("testArn"))
  val testBadJson: JsObject = Json.obj(
    "monkey" -> "do"
  )
  val testUserJson: JsObject = Json.obj(
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
        val result: Future[Result] = controller.register.apply(fakePostRequest.withBody(testUserJson))
        contentAsJson(result) shouldBe Json.toJson(testClient)
      }
    }
    "return a Unauthorized" in {
      when(rs.register(any())) thenReturn Future(None)

      val result: Future[Result] = controller.register.apply(fakePostRequest.withBody(testUserJson))

      status(result) shouldBe UNAUTHORIZED
    }

    "return a BadRequest" in {
      val result: Future[Result] = controller.register.apply(fakePostRequest.withBody(testBadJson))

      status(result) shouldBe BAD_REQUEST
    }
  }

}
