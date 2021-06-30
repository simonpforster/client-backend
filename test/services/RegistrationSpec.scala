package services

import helpers.AbstractTest
import models.{Client, ClientRegistration}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import repositories.{ClientRepository, UserRepository}
import service.{EncryptionService, RegistrationService}

import scala.concurrent.Future

class RegistrationSpec extends AbstractTest with GuiceOneAppPerSuite {
  val testCRN: String = "CRNTEST"
  val userRepo: UserRepository = mock(classOf[UserRepository])
  val clientRepo: ClientRepository = mock(classOf[ClientRepository])
  val crypto: EncryptionService = app.injector.instanceOf[EncryptionService]
  val service: RegistrationService = new RegistrationService(
    userRepo = userRepo,
    clientRepo = clientRepo,
    crypto = crypto)
  val clientReg: ClientRegistration = new ClientRegistration(
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = 12,
    postcode = "testPostcode",
    businessType = "testBusinessType",
    password = "testPassword")
  val clientTest: Client = new Client(
    crn = testCRN,
    name = clientReg.name,
    businessName = clientReg.businessName,
    contactNumber = clientReg.contactNumber,
    propertyNumber = clientReg.propertyNumber,
    postcode = clientReg.postcode,
    businessType = clientReg.businessType)
  "Registration Service" can {
    "Add a user and client to the database" should {
      "return true with correct details" in {
        when(userRepo.create(any())) thenReturn Future.successful(true)
        when(clientRepo.create(any())) thenReturn Future.successful(true)
        val result = service.register(clientReg)
        await(result).get.name shouldBe clientTest.name
      }
      "return a false when their is an error" in {
        when(userRepo.create(any())) thenReturn Future.successful(false)
        when(clientRepo.create(any())) thenReturn Future.successful(false)
        val result = service.register(clientReg)
        await(result) shouldBe None
      }
    }

    "Generate CRN" should {
      "Return a string" in {
        val myCRN: String = service.generateCRN()
        myCRN should include
        "CRN"
      }
    }
  }
}
