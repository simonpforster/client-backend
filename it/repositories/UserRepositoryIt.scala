package repositories

import models.User
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class UserRepositoryIt extends AnyWordSpec with GuiceOneServerPerSuite
	with DefaultPlayMongoRepositorySupport[User]
	with Matchers
	with ScalaFutures {

	override def repository = new UserRepository(mongoComponent)

	override implicit lazy val app: Application = new GuiceApplicationBuilder()
		.configure().build()

	private val testUser = User("testCrn", "testPass")

	"UserRepository" can {
		"read" should {
			"succeed" in {
				await(repository.create(testUser))

				await(repository.read("testCrn")) shouldBe Some(testUser)
			}

			"fail because of not found" in {
				await(repository.read("adadfss")) shouldBe None
			}
		}
	}
}
