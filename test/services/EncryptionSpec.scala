package services

import com.typesafe.config.ConfigFactory
import helpers.AbstractTest
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import service.EncryptionService

class EncryptionSpec extends AbstractTest with GuiceOneAppPerSuite {
  val plainText = "password"
  ConfigFactory.load()
  val crypto: EncryptionService = app.injector.instanceOf[EncryptionService]
  "Encryption Service" can {
    "Encrypt and decrypt sensitive data" should {
      "return the same data after encryption + decryption" in {
        val nonce = crypto.getNonce
        val mySecretCode = crypto.encrypt(plainText.getBytes, crypto.getKey, nonce)
        mySecretCode should not equal plainText.getBytes
        val decoded = crypto.decrypt(mySecretCode, crypto.getKey, nonce)
        decoded shouldBe plainText
      }
    }
  }
}
