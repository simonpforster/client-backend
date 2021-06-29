package controllers

import helpers.AbstractTest
import models.ClientAgentPair
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import repositories.ClientRepository

import scala.concurrent.Future

class ClientControllerSpec extends AbstractTest with GuiceOneAppPerSuite {

	private val testClientAgentPair: ClientAgentPair = ClientAgentPair("testCrn", "testArn")
	private val testBodyCAPair = Json.obj(
		"crn" -> "testCrn",
		"arn" -> "testArn"
	)

	private val testBadJson = Json.obj(
		"monkey"-> "do"
	)

	val clientRepository: ClientRepository = mock(classOf[ClientRepository])

	val clientController: ClientController = new ClientController(Helpers.stubControllerComponents(), clientRepository, Helpers.stubControllerComponents().executionContext)

	private val fakeRequest = FakeRequest("PATCH", "/")

	"ClientController" can {
		"addAgent" should {
			"NoContent" in {
				when(clientRepository.addAgent(any(), any())).thenReturn(Future.successful((true, true)))

				val result = clientController.addAgent.apply(fakeRequest.withBody(testBodyCAPair))

				status(result) shouldBe NO_CONTENT
			}

			"Not Found" in {
				when(clientRepository.addAgent(any(), any())).thenReturn(Future.successful((false, true)))

				val result = clientController.addAgent.apply(fakeRequest.withBody(testBodyCAPair))

				status(result) shouldBe NOT_FOUND
			}

			"Conflict" in {
				when(clientRepository.addAgent(any(), any())).thenReturn(Future.successful((true, false)))

				val result = clientController.addAgent.apply(fakeRequest.withBody(testBodyCAPair))

				status(result) shouldBe CONFLICT
			}

			"BadRequest" in {
				val result = clientController.addAgent.apply(fakeRequest.withBody(testBadJson))

				status(result) shouldBe BAD_REQUEST
			}
 		}
	}

}
