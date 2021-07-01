package controllers

import helpers.AbstractTest
import models.{EncryptedPassword, User, UserLogin}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{await, contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import repositories.UserRepository
import service.EncryptionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserControllerSpec extends AbstractTest with GuiceOneAppPerSuite {
  val crypto: EncryptionService = app.injector.instanceOf[EncryptionService]
  val nonce: Array[Byte] = crypto.getNonce
  val testPass = "testPass"
  val ePassword = new EncryptedPassword(
    ePassword = crypto.encrypt(testPass.getBytes, crypto.getKey, nonce),
    nonce = nonce)
  val testUser: User = User(
    crn = "testCrn",
    password = ePassword)
  val testUserLogin: UserLogin = UserLogin(
    crn = testUser.crn,
    password = testPass
  )
  val wrongUserLogin: UserLogin = UserLogin(
    crn = "Monkey",
    password = "do"
  )
  val testUserJson: JsValue = Json.toJson(testUser)
  val userRepository: UserRepository = mock(classOf[UserRepository])
  val userController: UserController = new UserController(
    cc = Helpers.stubControllerComponents(),
    userRepository = userRepository,
    crypto = crypto)
  //may be needed in future
  //val fakePatchRequest = FakeRequest("PATCH", "/")
  val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(method = "GET", path = "/")
  val testUserCrn: JsObject = Json.obj(
    "crn" -> "testCrn"
  )
  val testBadJson: JsObject = Json.obj(
    "monkey" -> "do"
  )
  "UserController" can {
    "read" should {
      "Ok" in {
        when(userRepository.read(any())) thenReturn Future.successful(Some(testUser))

        val result: Future[Result] = userController.read.apply(fakeGetRequest.withBody(testUserCrn))
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(testUser)
      }

      "NotFound" in {
        when(userRepository.read(any())) thenReturn Future.successful(None)

        val result: Future[Result] = userController.read.apply(fakeGetRequest.withBody(testUserCrn))

        status(result) shouldBe NOT_FOUND
      }

      "BadRequest" in {
        val result: Future[Result] = userController.read.apply(fakeGetRequest.withBody(testBadJson))

        status(result) shouldBe BAD_REQUEST
      }
    }
    "checkMatches" should {
      "return true if crn and password match" in {
        when(userRepository.read(any())) thenReturn Future.successful(Some(testUser))

        val result = userController.checkMatches(testUserLogin)

        await(result) shouldBe true
      }
      "return false if no match" in {
        when(userRepository.read(any())) thenReturn Future.successful(Some(testUser))

        val result = userController.checkMatches(wrongUserLogin)

        await(result) shouldBe false
      }
      "return false if user does not exist" in {
        when(userRepository.read(any())) thenReturn Future.successful(None)
        val result = userController.checkMatches(testUserLogin)

        await(result) shouldBe false
      }
    }
  }
}
