package services

import com.typesafe.config.ConfigFactory
import helpers.AbstractTest
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import service.EncryptionService

class EncryptionSpec extends AbstractTest with GuiceOneAppPerSuite {
  val plainText: String = "password"
  ConfigFactory.load()
  val crypto: EncryptionService = app.injector.instanceOf[EncryptionService]
  "Encryption Service" can {
    "Encrypt and decrypt sensitive data" should {
      "return the same data after encryption + decryption" in {
        val nonce: Array[Byte] = crypto.getNonce
        val mySecretCode: Array[Byte] = crypto.encrypt(plainText.getBytes, crypto.getKey, nonce)
        mySecretCode should not equal plainText.getBytes
        val decoded: String = crypto.decrypt(mySecretCode, crypto.getKey, nonce)
        decoded shouldBe plainText
      }
    }
  }
}
