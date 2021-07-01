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
  val testPass: String = "testPass"
  val ePassword: EncryptedPassword = EncryptedPassword(
    ePassword = crypto.encrypt(testPass.getBytes, crypto.getKey, nonce),
    nonce = nonce)
  val testUser: User = User(
    crn = "testCrn",
    password = ePassword)

  def equalsTestUser(user: User): Boolean = {
    user.crn == testUser.crn && crypto.decrypt(user.password.ePassword, crypto.getKey, user.password.nonce) == "testPass"
  }

  "UserRepository" can {
    "create" should {
      "return true" in {
        await(repository.create(testUser)) shouldBe true
        equalsTestUser(await(repository.read(testUser.crn)).get) shouldBe true
      }

      "fail because conflict" in {
        await(repository.create(testUser))
        await(repository.create(testUser)) shouldBe false
      }
    }


    "read" should {
      "succeed" in {
        await(repository.create(testUser))

        val user: User = await(repository.read("testCrn")).get
        equalsTestUser(user) shouldBe true
      }

      "fail because of not found" in {
        await(repository.read("adadfss")) shouldBe None
      }
    }
  }

  "delete" should {
    "succeed" in {
      await(repository.create(testUser))

      await(repository.delete(testUser.crn)) shouldBe true
    }

    "fail because not found" in {
      await(repository.delete(testUser.crn)) shouldBe false
    }
  }
}
