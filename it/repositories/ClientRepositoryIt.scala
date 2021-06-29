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

	val testClient = Client("testCrn", "testName", "testBusinessName", "testNumber", 12, "testCode", "testType")

	"ClientRepository" can {
		"read client" in {
			"succeed" in {
				await(repository.create(testClient))

				await(repository.read("testCrn")) shouldBe Some(testClient)
			}

			"fail because not found" in {
				await(repository.read("scasfa")) shouldBe None
			}
		}


		"add agent" should {
			"succeed" in {
				await(repository.create(testClient))

				await(repository.addAgent("testCrn", "testArn")) shouldBe (true, true)

				await(repository.read("testCrn")) shouldBe Some(testClient)
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
	}


}
