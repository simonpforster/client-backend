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

	val testClient: Client = Client("testCrn", "testName", "testBusinessName", "testNumber", 12, "testCode", "testType")

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

				await(repository.read("testCrn")) shouldBe Some(testClient)
			}

			"fail because not found" in {
				await(repository.read("scasfa")) shouldBe None
			}
		}

		"read all agent" should {
			"succeed" in {
				val testClientList = List(testClient.copy(arn = Some("testArn")), testClient.copy(crn = "testCrn2", arn = Some("testArn")))
				testClientList.map(x => await(repository.create(x)))

				await(repository.readAllAgent("testArn")) shouldBe testClientList
			}

			"empty list" in {
				await(repository.readAllAgent("testArn")) shouldBe List()
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

				await(repository.addAgent("testCrn", "testArn")) shouldBe (true, true)

				await(repository.read("testCrn")) shouldBe Some(testClient.copy(arn = Some("testArn")))
			}

			"fail because not found" in {
				await(repository.create(testClient))

				await(repository.addAgent("someCrn", "testArn")) shouldBe (false, true)
			}

			"fail because conflict" in {
				await(repository.create(testClient.copy(arn = Some("otherArn"))))

				await(repository.addAgent("testCrn", "testArn")) shouldBe (true, false)

				await(repository.read("testCrn")) shouldBe Some(testClient.copy(arn = Some("otherArn")))
			}
		}

		"remove agent" should {
			"succeed" in {
				await(repository.create(testClient.copy(arn = Some("testArn"))))

				await(repository.removeAgent("testCrn", "testArn")) shouldBe (true, true)

				await(repository.read("testCrn")) shouldBe Some(testClient)
			}

			"fail because not found" in {
				await(repository.create(testClient.copy(arn = Some("testArn"))))

				await(repository.removeAgent("testBadCrn", "testArn")) shouldBe (false, true)

				await(repository.read("testCrn")) shouldBe Some(testClient.copy(arn = Some("testArn")))
			}

			"fail because of conflict" in {
				await(repository.create(testClient))

				await(repository.removeAgent("testCrn", "testArn")) shouldBe (true, false)

				await(repository.read("testCrn")) shouldBe Some(testClient)
			}
		}
	}
}
