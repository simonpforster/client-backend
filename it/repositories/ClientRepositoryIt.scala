package repositories

import models.Client
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import scala.concurrent.ExecutionContext.Implicits.global

class ClientRepositoryIt extends AnyWordSpec with GuiceOneServerPerSuite
  with DefaultPlayMongoRepositorySupport[Client]
  with Matchers
  with ScalaFutures {

  override def repository = new ClientRepository(mongoComponent)

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .configure().build()

  val testClient: Client = Client(
    crn = "testCrn",
    name = "testName",
    businessName = "testBusinessName",
    contactNumber = "testNumber",
    propertyNumber = "12",
    postcode = "testCode",
    businessType = "testType")
  val testClientWithARN: Client = testClient.copy(arn = Some("arnTest"))
  val crnTest: String = "testCrn2"
  val arnTest: String = "arnTest"
  val updatedClient: Client = testClient.copy(name = "newTestName")
  val updatedClientWithARN: Client = testClient.copy(name = "newTestName", arn = Some("arnTest"))
  val badClient: Client = testClient.copy(crn = "WrongCrn")

  "ClientRepository" can {
    "create" should {
      "return true" in {
        val result: Boolean = await(repository.create(testClient))
        result shouldBe true
      }
      "create" should {
        "fail if not unique" in {
          await(repository.create(testClient))
          val result: Boolean = await(repository.create(testClient))
          result shouldBe false
        }
      }
    }
    "read client" should {
      "succeed" in {
        await(repository.create(testClient))

        await(repository.read(testClient.crn)) shouldBe Some(testClient)
      }

      "fail because not found" in {
        await(repository.read(badClient.crn)) shouldBe None
      }
    }

    "read all agent" should {
      "succeed" in {
        val testClientList = List(testClient.copy(arn = testClientWithARN.arn), testClient.copy(crn = crnTest, arn = testClientWithARN.arn))
        testClientList.map(x => await(repository.create(x)))

        await(repository.readAllAgent(arnTest)) shouldBe testClientList
      }

      "empty list" in {
        await(repository.readAllAgent(arnTest)) shouldBe List()
      }
    }

    "delete client" should {
      "succeed" in {
        await(repository.create(testClient))

        await(repository.delete(testClient.crn)) shouldBe true
      }

      "fail" in {
        await(repository.delete(testClient.crn)) shouldBe false
      }
    }

    "add agent" should {
      "succeed" in {
        await(repository.create(testClient))

        await(repository.addAgent(crn = testClient.crn, arn = arnTest)) shouldBe(true, true)

        await(repository.read(testClient.crn)) shouldBe Some(testClient.copy(arn = Some(arnTest)))
      }

      "fail because not found" in {
        await(repository.create(testClient))

        await(repository.addAgent(badClient.crn, arnTest)) shouldBe(false, true)
      }

      "fail because conflict" in {
        await(repository.create(testClient.copy(arn = updatedClientWithARN.arn)))

        await(repository.addAgent(testClientWithARN.crn, arnTest)) shouldBe(true, false)

        await(repository.read(testClientWithARN.crn)) shouldBe Some(testClient.copy(arn = updatedClientWithARN.arn))
      }
    }

    "remove agent" should {
      "succeed" in {
        await(repository.create(testClient.copy(arn = updatedClientWithARN.arn)))

        await(repository.removeAgent(testClientWithARN.crn, arnTest)) shouldBe(true, true)

        await(repository.read(testClientWithARN.crn)) shouldBe Some(testClient)
      }

      "fail because not found" in {
        await(repository.create(testClient.copy(arn = testClientWithARN.arn)))

        await(repository.removeAgent(badClient.crn, arnTest)) shouldBe(false, true)

        await(repository.read(testClient.crn)) shouldBe Some(testClient.copy(arn = testClientWithARN.arn))
      }

      "fail because of conflict" in {
        await(repository.create(testClient))

        await(repository.removeAgent(testClient.crn, arnTest)) shouldBe(true, false)

        await(repository.read(testClient.crn)) shouldBe Some(testClient)
      }
      "update" should {
        "succeed without arn" in {
          await(repository.create(testClient))

          await(repository.update(updatedClient))

          await(repository.read(testClient.crn)) shouldBe Some(updatedClient)
        }
        "succeed with arn" in {
          await(repository.create(testClientWithARN))

          await(repository.update(updatedClient)) shouldBe true

          await(repository.read(testClient.crn)) shouldBe Some(updatedClientWithARN)
        }
        "fail with arn when incorrect details added" in {
          await(repository.create(testClient))

          await(repository.update(badClient)) shouldBe false

          await(repository.read(testClient.crn)) shouldBe Some(testClient)
        }
      }
    }
  }
}
