package controllers

import helpers.AbstractTest
import models.User
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import repositories.UserRepository

import scala.concurrent.Future

class UserControllerSpec extends AbstractTest {



	private val testBadJson = Json.obj(
		"monkey"-> "do"
	)

	private val testUser = User("testCrn", "testPass")
	private val testUserJson = Json.toJson(testUser)

	private val testUserCrn = Json.obj(
		"crn" -> "testCrn"
	)


	val userRepository: UserRepository = mock(classOf[UserRepository])

	val userController: UserController = new UserController(Helpers.stubControllerComponents(), userRepository, Helpers.stubControllerComponents().executionContext)

	private val fakePatchRequest = FakeRequest("PATCH", "/")
	private val fakeGetRequest = FakeRequest("GET", "/")


	"UserController" can {
		"read" should {
			"Ok" in {
				when(userRepository.read(any())).thenReturn(Future.successful(Some(testUser)))

				val result = userController.read.apply(fakeGetRequest.withBody(testUserCrn))

				status(result) shouldBe OK
				contentAsJson(result) shouldBe Json.toJson(testUser)
			}

			"NotFound" in {
				when(userRepository.read(any())).thenReturn(Future.successful(None))

				val result = userController.read.apply(fakeGetRequest.withBody(testUserCrn))

				status(result) shouldBe NOT_FOUND
			}

			"BadRequest" in {
				val result = userController.read.apply(fakeGetRequest.withBody(testBadJson))

				status(result) shouldBe BAD_REQUEST
			}
		}
	}
}
