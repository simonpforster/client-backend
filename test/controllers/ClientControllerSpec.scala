package controllers

import helpers.AbstractTest
import models.{Client, ClientAgentPair}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play.materializer
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import repositories.{ClientRepository, UserRepository}
import scala.concurrent.Future

class ClientControllerSpec extends AbstractTest with GuiceOneAppPerSuite {

  private val testClientAgentPair: ClientAgentPair = ClientAgentPair("testCrn", "testArn")
  private val testClient: Client = Client("testCrn", "testName", "testBusiness", "testContact", 12, "testPostcode", "testBusinessType", Some("testArn"))
  private val testClientList = List(testClient, testClient.copy(crn = "testCrn2"))
  private val testClientCrn = Json.obj(
    "crn" -> "testCrn"
  )
  private val testArnJson = Json.obj(
    "arn" -> "testArn"
  )
  private val testBodyCAPair = Json.obj(
    "crn" -> "testCrn",
    "arn" -> "testArn"
  )
  private val testBadJson = Json.obj(
    "monkey"-> "do"
  )
  private val testClientDeleteJson = Json.obj(
    "crn"-> "RANDOM"
  )
  private val testClientDeleteBadJson = Json.obj(
    "firstField" -> "fail",
    "secondField"-> "RANDOM"
  )
  val clientRepository: ClientRepository = mock(classOf[ClientRepository])
  val userRepository: UserRepository = mock(classOf[UserRepository])
  val clientController: ClientController = new ClientController(Helpers.stubControllerComponents(), clientRepository, userRepository)
  private val fakePatchRequest = FakeRequest("PATCH", "/")
  private val fakeGetRequest = FakeRequest("GET", "/")
  private val fakeDeleteRequest = FakeRequest("DELETE", "/")

  "ClientController" can {
    "read" should {
      "Ok" in {
        when(clientRepository.read(any())).thenReturn(Future.successful(Some(testClient)))
        val result = clientController.read.apply(fakeGetRequest.withBody(testClientCrn))
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(testClient)
      }
      "NotFound" in {
        when(clientRepository.read(any())).thenReturn(Future.successful(None))
        val result = clientController.read.apply(fakeGetRequest.withBody(testClientCrn))
        status(result) shouldBe NOT_FOUND
      }
      "BadRequest" in {
        val result = clientController.read.apply(fakeGetRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "read all agent" should {
      "Ok" in {
        when(clientRepository.readAllAgent(any())).thenReturn(Future.successful(testClientList))
        val result = clientController.readAllAgent.apply(fakeGetRequest.withBody(testArnJson))
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(testClientList)
      }
      "Ok empty" in {
        when(clientRepository.readAllAgent(any())).thenReturn(Future.successful(List()))
        val result = clientController.readAllAgent.apply(fakeGetRequest.withBody(testArnJson))
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(List[Client]())
      }
      "BadRequest" in {
        val result = clientController.readAllAgent.apply(fakeGetRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "addAgent" should {
      "NoContent" in {
        when(clientRepository.addAgent(any(), any())).thenReturn(Future.successful((true, true)))
        val result = clientController.addAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe NO_CONTENT
      }
      "Not Found" in {
        when(clientRepository.addAgent(any(), any())).thenReturn(Future.successful((false, true)))
        val result = clientController.addAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe NOT_FOUND
      }
      "Conflict" in {
        when(clientRepository.addAgent(any(), any())).thenReturn(Future.successful((true, false)))
        val result = clientController.addAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe CONFLICT
      }
      "BadRequest" in {
        val result = clientController.addAgent.apply(fakePatchRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "removeAgent" should {
      "return NoContent" in {
        when(clientRepository.removeAgent(any(), any())).thenReturn(Future.successful((true, true)))
        val result = clientController.removeAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe NO_CONTENT
      }
      "return Not Found" in {
        when(clientRepository.removeAgent(any(), any())).thenReturn(Future.successful((false, true)))
        val result = clientController.removeAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe NOT_FOUND
      }
      "return Conflict" in {
        when(clientRepository.removeAgent(any(), any())).thenReturn(Future.successful((true, false)))
        val result = clientController.removeAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe CONFLICT
      }
      "return BadRequest" in {
        val result = clientController.removeAgent.apply(fakePatchRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "deleteClient" should{
      "return NoContent" when {
        "both clientRepository.delete & userRepository.delete returns TRUE " in {
          when(clientRepository.delete(any())).thenReturn(Future.successful((true)))
          when(userRepository.delete(any())).thenReturn(Future.successful((true)))
          val result = clientController.deleteClient.apply(fakeDeleteRequest.withBody(testClientDeleteJson))
          status(result) shouldBe NO_CONTENT
        }
      }
      "return NotFound" when {
        "clientRepository.delete FALSE" in {
          when(clientRepository.delete(any())).thenReturn(Future.successful((false)))
          val result = clientController.deleteClient.apply(fakeDeleteRequest.withBody(testClientDeleteJson))
          status(result) shouldBe NOT_FOUND
        }
        "clientRepository.delete TRUE & userRepository.delete FALSE" in {
          when(clientRepository.delete(any())).thenReturn(Future.successful((true)))
          when(userRepository.delete(any())).thenReturn(Future.successful((false)))
          val result = clientController.deleteClient.apply(fakeDeleteRequest.withBody(testClientDeleteJson))
          status(result) shouldBe NOT_FOUND
        }
      }
      "return BadRequest" when {
        "wrong model received" in {
          val result = clientController.deleteClient.apply(fakeDeleteRequest.withBody(testClientDeleteBadJson))
          status(result) shouldBe BAD_REQUEST
        }
      }
    }
  }
}

