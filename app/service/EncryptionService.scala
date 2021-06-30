package service

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.spec.{GCMParameterSpec, SecretKeySpec}
import javax.crypto.{Cipher, SecretKey}
import javax.inject.{Inject, Singleton}

@Singleton
class EncryptionService @Inject()(configuration: play.api.Configuration)() {
  val AES_KEY_SIZE: Int = 256
  val GCM_IV_LENGTH: Int = 12
  val GCM_TAG_LENGTH: Int = 16

  def getKey: SecretKeySpec = {
    val appKeyString: String = configuration.underlying.getString("play.http.secret.key")
    val decodedString: Array[Byte] = Base64.getDecoder.decode(appKeyString)
    val originalKey: SecretKeySpec = new SecretKeySpec(decodedString, 0, decodedString.length, "AES")
    originalKey
  }

  def getNonce: Array[Byte] = {
    val nonce: Array[Byte] = BigInt(GCM_IV_LENGTH).toByteArray
    val random: SecureRandom = new SecureRandom()
    random.nextBytes(nonce)
    nonce
  }

  def encrypt(plainText: Array[Byte], key: SecretKey, nonce: Array[Byte]): Array[Byte] = {
    val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val keySpec: SecretKeySpec = new SecretKeySpec(key.getEncoded, "AES")
    val gcmParameterSpec: GCMParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec)
    val cipherText: Array[Byte] = cipher.doFinal(plainText)
    cipherText
  }

  def decrypt(cipherText: Array[Byte], key: SecretKey, nonce: Array[Byte]): String = {
    val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val keySpec: SecretKeySpec = new SecretKeySpec(key.getEncoded, "AES")
    val gcmParameterSpec: GCMParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce)
    cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec)
    val decrypted: Array[Byte] = cipher.doFinal(cipherText)
    val decryptedString: String = decrypted.map(_.toChar).mkString
    decryptedString
  }
}
