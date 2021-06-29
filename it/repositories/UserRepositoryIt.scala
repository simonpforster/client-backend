package repositories

import models.{EncryptedPassword, User}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import service.EncryptionService
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class UserRepositoryIt extends AnyWordSpec with GuiceOneServerPerSuite
	with DefaultPlayMongoRepositorySupport[User]
	with Matchers
	with ScalaFutures {

	override def repository = new UserRepository(mongoComponent)

	override implicit lazy val app: Application = new GuiceApplicationBuilder()
		.configure().build()

	val crypto: EncryptionService = app.injector.instanceOf[EncryptionService]
	val nonce: Array[Byte] = crypto.getNonce
	val testPass = "testPass"
	val ePassword = new EncryptedPassword(crypto.encrypt(testPass.getBytes, crypto.getKey, nonce), nonce)

	private val testUser = User("testCrn", ePassword)

	"UserRepository" can {
		"read" should {
			"succeed" in {
				await(repository.create(testUser))

				val something = await(repository.read("testCrn")).get
				something.crn shouldBe testUser.crn
				crypto.decrypt(something.password.ePassword, crypto.getKey, something.password.nonce) shouldBe "testPass"
			}

			"fail because of not found" in {
				await(repository.read("adadfss")) shouldBe None
			}
		}
	}
}
