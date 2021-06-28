package repositories

import models.Vehicle
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.mvc.Results.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.status

class DataRepositorySpec extends PlaySpec with GuiceOneAppPerTest{

  object testController extends DataRepository

  "DataRepository .getVehicle" should {

    "successfully return vehicle" when {

      "vehicle name matches vehicle option" in {

        val result = testController.getVehicle("BMW")
        result mustBe Some(Vehicle(4, true, "BMW"))
      }
    }

    "unsuccessfully return vehicle" when {

      "vehicle name matches vehicle option" in {

        val result = testController.getVehicle("test")
        result mustBe None
      }
    }
  }
}
