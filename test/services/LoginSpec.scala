package services

import controllers.UserController
import helpers.AbstractTest
import models.{Client, EncryptedPassword, User, UserLogin}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import repositories.{ClientRepository, UserRepository}
import service.{EncryptionService, LoginService}
import scala.concurrent.Future

class LoginSpec extends AbstractTest with GuiceOneAppPerSuite {

  val userRepo: UserRepository = mock(classOf[UserRepository])
  val clientRepo: ClientRepository = mock(classOf[ClientRepository])
  val userController: UserController = mock(classOf[UserController])
  val crypto: EncryptionService = app.injector.instanceOf[EncryptionService]
  val fakePostRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")
  val service: LoginService = new LoginService(
    cc = Helpers.stubControllerComponents(),
    userController = userController,
    clientRepo = clientRepo
  )
  val userLogin: UserLogin = UserLogin(
    crn = "crnTest",
    password = "testPass")
  val userLoginJson: JsValue = Json.toJson(userLogin)
  val nonce: Array[Byte] = crypto.getNonce
  val ePassword: EncryptedPassword = EncryptedPassword(
    ePassword = crypto.encrypt(userLogin.password.getBytes(), crypto.getKey, nonce),
    nonce = nonce)
  val dbUser: User = User(
    crn = userLogin.crn,
    password = ePassword)
  val client: Client = Client(
    crn = userLogin.crn,
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = 12,
    postcode = "testPostcode",
    businessType = "testBusinessType")
  val badJson: JsObject = Json.obj(
    "BadBoi" -> "McGee"
  )
  "Login Service" can {
    "login" should {
      "return the client from a user" in {
        when(userController.checkMatches(any())) thenReturn Future.successful(true)
        when(clientRepo.read(any())) thenReturn Future.successful(Some(client))
        val result: Future[Result] = service.login.apply(fakePostRequest.withBody(userLoginJson))
        status(result) shouldBe 200
        contentAsJson(result) shouldBe Json.toJson(client)
      }
      "return Unauthorised if passwords do not match" in {
        when(userController.checkMatches(any())) thenReturn Future.successful(false)
        when(clientRepo.read(any())) thenReturn Future.successful(None)
        val result: Future[Result] = service.login.apply(fakePostRequest.withBody(userLoginJson))
        status(result) shouldBe 401
      }
      "return a bad request if json is incorrect" in {
        val result: Future[Result] = service.login.apply(fakePostRequest.withBody(badJson))
        status(result) shouldBe 400
      }
    }
  }
}
