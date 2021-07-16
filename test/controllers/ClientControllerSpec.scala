package controllers

import helpers.AbstractTest
import models.{BusinessTypeUpdateDetails, Client, ContactNumberUpdateDetails, NameUpdateDetails, PropertyUpdateDetails}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play.materializer
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import repositories.{ClientRepository, UserRepository}

import scala.concurrent.Future

class ClientControllerSpec extends AbstractTest with GuiceOneAppPerSuite {

  private val testClient: Client = Client(
    crn = "testCrn",
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = "12",
    postcode = "testPostcode",
    businessType = "testBusinessType",
    arn = Some("testArn"))
  private val testClientList = List(testClient, testClient.copy(crn = "testCrn2"))
  private val testClientCrn = Json.obj(
    "crn" -> "testCrn"
  )
  private val testNameUpdateDetails = NameUpdateDetails(
    crn = testClient.crn,
    name = "newName")
  private val testBusinessTypeUpdateDetails = BusinessTypeUpdateDetails(
    crn = testClient.crn,
    businessType = "newBusinessType")
  private val testContactNumberUpdateDetails = ContactNumberUpdateDetails(
    crn = testClient.contactNumber,
    contactNumber = "newNumber")
  private val testPropertyUpdateDetails = PropertyUpdateDetails(
    crn = testClient.crn,
    propertyNumber = "newPropertyNumber",
    postcode = "newPostcode")
  private val testArnJson = Json.obj(
    "arn" -> "testArn"
  )
  private val testBodyCAPair = Json.obj(
    "crn" -> "testCrn",
    "arn" -> "testArn"
  )
  private val testBadJson = Json.obj(
    "monkey" -> "do"
  )
  private val testClientDeleteJson = Json.obj(
    "crn" -> "RANDOM"
  )
  private val testClientDeleteBadJson = Json.obj(
    "firstField" -> "fail",
    "secondField" -> "RANDOM"
  )
  val clientRepository: ClientRepository = mock(classOf[ClientRepository])
  val userRepository: UserRepository = mock(classOf[UserRepository])
  val clientController: ClientController = new ClientController(
    cc = Helpers.stubControllerComponents(),
    clientRepository = clientRepository,
    userRepository = userRepository)
  val fakePatchRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "PATCH",
    path = "/")
  val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = "/")
  val fakeDeleteRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "DELETE",
    path = "/")
  val fakePutRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "PUT",
    path = "/")
  "ClientController" can {
    "read" should {
      "Ok" in {
        when(clientRepository.read(any())) thenReturn Future.successful(Some(testClient))
        val result: Future[Result] = clientController.read.apply(fakeGetRequest.withBody(testClientCrn))
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(testClient)
      }
      "NotFound" in {
        when(clientRepository.read(any())) thenReturn Future.successful(None)
        val result: Future[Result] = clientController.read.apply(fakeGetRequest.withBody(testClientCrn))
        status(result) shouldBe NOT_FOUND
      }
      "BadRequest" in {
        val result: Future[Result] = clientController.read.apply(fakeGetRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "read all agent" should {
      "Ok" in {
        when(clientRepository.readAllAgent(any())) thenReturn Future.successful(testClientList)
        val result: Future[Result] = clientController.readAllAgent.apply(fakeGetRequest.withBody(testArnJson))
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(testClientList)
      }
      "Ok empty" in {
        when(clientRepository.readAllAgent(any())) thenReturn Future.successful(List())
        val result: Future[Result] = clientController.readAllAgent.apply(fakeGetRequest.withBody(testArnJson))
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(List[Client]())
      }
      "BadRequest" in {
        val result: Future[Result] = clientController.readAllAgent.apply(fakeGetRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "addAgent" should {
      "NoContent" in {
        when(clientRepository.addAgent(any(), any())) thenReturn Future.successful((true, true))
        val result: Future[Result] = clientController.addAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe NO_CONTENT
      }

      "Not Found" in {
        when(clientRepository.addAgent(any(), any())) thenReturn Future.successful((false, true))
        val result: Future[Result] = clientController.addAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe NOT_FOUND
      }

      "Conflict" in {
        when(clientRepository.addAgent(any(), any())) thenReturn Future.successful((true, false))
        val result: Future[Result] = clientController.addAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe CONFLICT
      }
      "BadRequest" in {
        val result: Future[Result] = clientController.addAgent.apply(fakePatchRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "removeAgent" should {
      "return NoContent" in {
        when(clientRepository.removeAgent(any(), any())) thenReturn Future.successful((true, true))
        val result: Future[Result] = clientController.removeAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe NO_CONTENT
      }
      "return Not Found" in {
        when(clientRepository.removeAgent(any(), any())) thenReturn Future.successful((false, true))
        val result: Future[Result] = clientController.removeAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe NOT_FOUND
      }
      "return Conflict" in {
        when(clientRepository.removeAgent(any(), any())) thenReturn Future.successful((true, false))
        val result: Future[Result] = clientController.removeAgent.apply(fakePatchRequest.withBody(testBodyCAPair))
        status(result) shouldBe CONFLICT
      }
      "return BadRequest" in {
        val result: Future[Result] = clientController.removeAgent.apply(fakePatchRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "deleteClient" should {
      "return NoContent" when {
        "both clientRepository.delete & userRepository.delete returns TRUE " in {
          when(clientRepository.delete(any())) thenReturn Future.successful(true)
          when(userRepository.delete(any())) thenReturn Future.successful(true)
          val result: Future[Result] = clientController.deleteClient.apply(fakeDeleteRequest.withBody(testClientDeleteJson))
          status(result) shouldBe NO_CONTENT
        }
      }
      "return NotFound" when {
        "clientRepository.delete FALSE" in {
          when(clientRepository.delete(any())) thenReturn Future.successful(false)
          val result: Future[Result] = clientController.deleteClient.apply(fakeDeleteRequest.withBody(testClientDeleteJson))
          status(result) shouldBe NOT_FOUND
        }
        "clientRepository.delete TRUE & userRepository.delete FALSE" in {
          when(clientRepository.delete(any())) thenReturn Future.successful(true)
          when(userRepository.delete(any())) thenReturn Future.successful(false)
          val result: Future[Result] = clientController.deleteClient.apply(fakeDeleteRequest.withBody(testClientDeleteJson))
          status(result) shouldBe NOT_FOUND
        }
      }
      "return BadRequest" when {
        "wrong model received" in {
          val result: Future[Result] = clientController.deleteClient.apply(fakeDeleteRequest.withBody(testClientDeleteBadJson))
          status(result) shouldBe BAD_REQUEST
        }
      }
    }

    "update name" should {
      "return NoContent with update success" in {
        when(clientRepository.updateName(any())) thenReturn Future.successful(true)
        val result: Future[Result] = clientController.updateName.apply(fakePatchRequest.withBody(Json.toJson(testNameUpdateDetails)))
        status(result) shouldBe NO_CONTENT
      }
      "return NotFound with update unsuccessful" in {
        when(clientRepository.updateName(any())) thenReturn Future.successful(false)
        val result: Future[Result] = clientController.updateName.apply(fakePatchRequest.withBody(Json.toJson(testNameUpdateDetails)))
        status(result) shouldBe NOT_FOUND
      }
      "return a BadRequest with Js Error" in {
        val result: Future[Result] = clientController.updateName.apply(fakePatchRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }

    "update property details" should {
      "return NoContent with update success" in {
        when(clientRepository.updateProperty(any())) thenReturn Future.successful(true)
        val result: Future[Result] = clientController.updateProperty.apply(fakePatchRequest.withBody(Json.toJson(testPropertyUpdateDetails)))
        status(result) shouldBe NO_CONTENT
      }
      "return NotFound with update unsuccessful" in {
        when(clientRepository.updateProperty(any())) thenReturn Future.successful(false)
        val result: Future[Result] = clientController.updateProperty.apply(fakePatchRequest.withBody(Json.toJson(testPropertyUpdateDetails)))
        status(result) shouldBe NOT_FOUND
      }
      "return a BadRequest with Js Error" in {
        val result: Future[Result] = clientController.updateProperty.apply(fakePatchRequest.withBody(testBadJson))
        status(result) shouldBe BAD_REQUEST
      }
    }

    "updateContactNumber" should {
      "return NoContent" when {
        "updated successfully" in {
          when(clientRepository.updateContactNumber(any())) thenReturn Future.successful(true)
          val result: Future[Result] = clientController.updateContactNumber.apply(fakePatchRequest.withBody(Json.toJson(testContactNumberUpdateDetails)))
          status(result) shouldBe NO_CONTENT
        }
      }
      "return NotFound" when {
        "update unsuccessful" in {
          when(clientRepository.updateContactNumber(any())) thenReturn Future.successful(false)
          val result: Future[Result] = clientController.updateContactNumber.apply(fakePatchRequest.withBody(Json.toJson(testContactNumberUpdateDetails)))
          status(result) shouldBe NOT_FOUND
        }
      }
      "return BadRequest" when {
        "when request body doesn't match the model" in {
          val result: Future[Result] = clientController.updateContactNumber.apply(fakePatchRequest.withBody(Json.toJson(testBadJson)))
          status(result) shouldBe BAD_REQUEST
        }
      }

      "update business type" should {
        "return NoContent with update success" in {
          when(clientRepository.updateBusinessType(any())) thenReturn Future.successful(true)
          val result: Future[Result] = clientController.updateBusinessType.apply(fakePatchRequest.withBody(Json.toJson(testBusinessTypeUpdateDetails)))
          status(result) shouldBe NO_CONTENT
        }
        "return NotFound with update unsuccessful" in {
          when(clientRepository.updateBusinessType(any())) thenReturn Future.successful(false)
          val result: Future[Result] = clientController.updateBusinessType.apply(fakePatchRequest.withBody(Json.toJson(testBusinessTypeUpdateDetails)))
          status(result) shouldBe NOT_FOUND
        }
        "return a BadRequest with Js Error" in {
          val result: Future[Result] = clientController.updateBusinessType.apply(fakePatchRequest.withBody(testBadJson))
          status(result) shouldBe BAD_REQUEST
        }
      }
    }
  }
}


