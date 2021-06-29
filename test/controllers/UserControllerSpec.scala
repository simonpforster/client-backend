package controllers

import helpers.AbstractTest
import org.mockito.Mockito.mock
import play.api.test.Helpers
import repositories.UserRepository

class UserControllerSpec extends AbstractTest {

  val dao: UserRepository = mock(classOf[UserRepository])
  val controller = new UserController(Helpers.stubControllerComponents(), dao)
  "UserController" can {
    "Generate CRN" should {
      "return a string" in {
      }
    }
  }
}
