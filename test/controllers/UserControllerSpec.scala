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
  val testPass: String = "testPass"
  val badString: String = "badString"
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
        when(userRepository.read(any())) thenReturn Future(Some(testUser))
        val result: Future[Result] = userController.read.apply(fakeGetRequest.withBody(testUserCrn))
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(testUser)
      }

      "NotFound" in {
        when(userRepository.read(any())) thenReturn Future(None)
        val result: Future[Result] = userController.read.apply(fakeGetRequest.withBody(testUserCrn))
        status(result) shouldBe NOT_FOUND
      }

      "BadRequest" in {
        val result: Future[Result] = userController.read.apply(fakeGetRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }

    "check matches" should {
      "succeed" in {
        when(userRepository.read(any())) thenReturn Future(Some(testUser))
        await(userController.checkMatches(UserLogin(testUser.crn,testPass))) shouldBe true
      }

      "fail because of wrong password" in {
        when(userRepository.read(any())) thenReturn Future(Some(testUser))
        await(userController.checkMatches(UserLogin(testUser.crn, badString))) shouldBe false
      }

      "fail because no user" in {
        when(userRepository.read(any())) thenReturn Future(None)
        await(userController.checkMatches(UserLogin(testUser.crn, testPass))) shouldBe false
      }

      "fail because the repository wonky" in {
        when(userRepository.read(any())) thenReturn Future(Some(testUser))
        await(userController.checkMatches(UserLogin(badString, testPass))) shouldBe false
      }
    }
  }
}
